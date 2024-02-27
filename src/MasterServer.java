import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MasterServer {

    static int nStartPoint;
    static int nEndPoint;

    static int nThreads = 128;

    static List<Integer> globalPrimes = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {

        System.out.println("Master server launched!");

        waitForClientRequest();
        splitAndSendDataToSlaveServers();
        waitForSlaveServerResponse();
        sendPrimesListToClient();

    }

    private static void sendPrimesListToClient() {
        try{
            // connect to client server
            Socket socket = new Socket("localhost", 4998);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // send total size of the primes list
            dataOutputStream.writeInt(globalPrimes.size());

            // send all primes list
            for (Integer globalPrime : globalPrimes) {
                dataOutputStream.writeInt(globalPrime);
            }

            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void waitForSlaveServerResponse() {
        // try-with-resources: ServerSocket matically closes after try block for proper cleaning of resources
        try (ServerSocket masterServerSocket = new ServerSocket(4999)) {


            Socket slaveServerSocket = masterServerSocket.accept();

            // receive data from the slave server
            DataInputStream dataInputStream = new DataInputStream(slaveServerSocket.getInputStream());

            // receive the arraylist size first
            int slavePrimesListSize = dataInputStream.readInt();

            for (int i = 1; i <= slavePrimesListSize; i++) {
                globalPrimes.add(dataInputStream.readInt());
            }

            System.out.println("Primes list form Slave Server 1:");
            System.out.println(globalPrimes);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void splitAndSendDataToSlaveServers() {
        int nMidPoint = (nEndPoint - nStartPoint) / 2 + nStartPoint;

        // pass data to slave server 1 at port 5000
        // replace "localhost with slave server's ipv4 address"
        try (Socket slaveServerSocket1 = new Socket("localhost", 5001)){
            DataOutputStream dataOutputStream1 = new DataOutputStream(slaveServerSocket1.getOutputStream());
            dataOutputStream1.writeInt(nStartPoint);
            dataOutputStream1.writeInt(nMidPoint);
            dataOutputStream1.writeInt(nThreads);

            System.out.println("Master server successfully sent ranges " + nStartPoint + " - " +
                    nMidPoint + " to slave server 1");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // pass data to slave server 2 at port 5001
        // replace "localhost with slave server's ipv4 address"
//        try (Socket slaveServerSocket2 = new Socket("localhost", 5001)){
//            DataOutputStream dataOutputStream2 = new DataOutputStream(slaveServerSocket2.getOutputStream());
//            dataOutputStream2.writeInt(nMidPoint + 1);
//            dataOutputStream2.writeInt(nEndPoint);
//            dataOutputStream2.writeInt(nThreads);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static void waitForClientRequest() {
        // try-with-resources: ServerSocket matically closes after try block for proper cleaning of resources
        try (ServerSocket masterServerSocket = new ServerSocket(4999)) {
            Socket clienSocket = masterServerSocket.accept();
            System.out.println("Client connected: " + clienSocket.getInetAddress() + ": " + clienSocket.getPort());

            // receive the start and end pt from the client
            DataInputStream dataInputStream = new DataInputStream(clienSocket.getInputStream());
            nStartPoint = dataInputStream.readInt();
            nEndPoint = dataInputStream.readInt();

            clienSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}

