import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MasterServer {
    private static final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private static final ExecutorService slaveExecutor = Executors.newCachedThreadPool();
    private static final List<SlaveInfo> slaves = new CopyOnWriteArrayList<>();
    private static final int CLIENT_PORT = 4999;
    private static final int SLAVE_REGISTRATION_PORT = 5001;

    public static void main(String[] args) {
        // Separate thread for listening to slave server registrations
        Thread slaveListenerThread = new Thread(() -> listenForSlaveRegistrations());
        slaveListenerThread.start();

        // try-with-resources: ServerSocket aumatically closes after try block for proper cleaning of resources
        try (ServerSocket serverSocket = new ServerSocket(CLIENT_PORT)) {
            System.out.println("Master Server Listening for clients on port " + CLIENT_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientExecutor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            clientExecutor.shutdown();
            slaveExecutor.shutdown();
        }
    }

    private static void listenForSlaveRegistrations() {
        try (ServerSocket slaveListener = new ServerSocket(SLAVE_REGISTRATION_PORT)) {
            System.out.println("Listening for Slave registrations on port " + SLAVE_REGISTRATION_PORT);
            while (true) {
                Socket slaveSocket = slaveListener.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
                String slaveAddress = input.readLine();
                int slavePort = Integer.parseInt(input.readLine());
                slaves.add(new SlaveInfo(slaveAddress, slavePort));
                System.out.println("Registered new slave - Address: " + slaveAddress + ", Port: " + slavePort);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            int startPoint = dis.readInt();
            int endPoint = dis.readInt();
            int nThreads = dis.readInt();

            int rangeSize = (endPoint - startPoint + 1);
            int rangePerSlave = rangeSize / slaves.size();
            AtomicInteger totalPrimes = new AtomicInteger();

            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < slaves.size(); i++) {
                int finalI = i;
                int slaveStartPoint = startPoint + i * rangePerSlave;
                int slaveEndPoint = (i == slaves.size() - 1) ? endPoint : (slaveStartPoint + rangePerSlave - 1);
                SlaveInfo slaveInfo = slaves.get(i);
                System.out.println("Sending task from client to slave " + slaveInfo.getAddress() + ":" + slaveInfo.getPort() + " - " + slaveStartPoint + " to " + slaveEndPoint + " using " + nThreads + " threads.");
                // Submit slave handling as a Callable task to executor
                futures.add(slaveExecutor.submit(() -> {
                    try (Socket slaveSocket = new Socket(slaveInfo.getAddress(), slaveInfo.getPort())) {
                        DataOutputStream slaveDos = new DataOutputStream(slaveSocket.getOutputStream());
                        DataInputStream slaveDis = new DataInputStream(slaveSocket.getInputStream());
                        slaveDos.writeInt(slaveStartPoint);
                        slaveDos.writeInt(slaveEndPoint);
                        slaveDos.writeInt(nThreads);
                        totalPrimes.addAndGet(slaveDis.readInt()); // Receive prime count from slave
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            }

            // Wait for all futures to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            dos.writeInt(totalPrimes.get());
        } catch (IOException ex) {
            System.err.println("Error handling client: " + clientSocket);
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SlaveInfo {
        private String address;
        private int port;

        public SlaveInfo(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}

