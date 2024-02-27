import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SlaveServer {

    private static int nStartPoint;
    private static int nEndPoint;
    private static int nThreads;
    private static List<Integer> localPrimes = new CopyOnWriteArrayList<>();


    public static void main(String[] args) {
        waitDataFromMasterServer();
        sendPrimesListToMasterServer();
    }

    private static void sendPrimesListToMasterServer() {
        try{
            Socket socket = new Socket("localhost", 4999);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            int sample = 100;

            // send the size of the local primes
//            dataOutputStream.writeInt(localPrimes.size());
            dataOutputStream.writeInt(sample);

            // then send all local primes
            // sample data to send the arraylist of integers
            for(int i=0; i<sample; i++){
                dataOutputStream.writeInt(i);
            }

            System.out.println("Slave Server successfully sent data");

            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void waitDataFromMasterServer() {

        try(ServerSocket slaveServerSocket = new ServerSocket(5001)){

            Socket masterServerSocket = slaveServerSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(masterServerSocket.getInputStream());
            nStartPoint = dataInputStream.readInt();
            nEndPoint = dataInputStream.readInt();
            nThreads = dataInputStream.readInt();

            System.out.println("Slave Server Received: ");
            System.out.println("Start point: " + nStartPoint);
            System.out.println("End point: " + nEndPoint);
            System.out.println("Thread count: " + nThreads);

            masterServerSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void checkPrimes() {

        List<Thread> threadList = new ArrayList<>();
        int nRangePerThread = (nEndPoint - nStartPoint) / nThreads;
        Lock localPrimesLock = new ReentrantLock();

        for(int i=1; i<=nThreads; i++){
            // SETUP RANGE OF NUMBERS TO BE ASSIGNED ON THE CURRENT THREAD
            int start = (i - 1) * nRangePerThread + 2;
            int end = i * nRangePerThread + 1;

            // LAST THREAD COVERS THE REMAINING RANGE OF IF nLimit % nThreads != 0
            if (i == nThreads) {
                end = nEndPoint;
            }

            // CHECK RESULTS
//            System.out.println("Thread " + i + " range:");
//            System.out.println("Start: " + start);
//            System.out.println("End: " + end);
//            System.out.println();

            // THREAD CREATION
            Thread thread = new Thread(new Main.PrimeTask(start, end, localPrimes, localPrimesLock));
            threadList.add(thread);
            thread.start();

            System.out.printf("%d local primes were found in slave server.\n", localPrimes.size());

        }

        // WAIT FOR ALL THREADS TO FINISH
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    static class PrimeTask implements Runnable{
        private final int start;
        private final int end;
        private final List<Integer> primes;
        private final Lock lock;

        PrimeTask(int start, int end, List<Integer> primes, Lock lock) {
            this.start = start;
            this.end = end;
            this.primes = primes;
            this.lock = lock;
        }

        @Override
        public void run() {
            for (int currentNum = start; currentNum <= end; currentNum++) {
                if (check_prime(currentNum)) {
                    lock.lock();
                    try {
                        primes.add(currentNum);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    /*
    This function checks if an integer n is prime.

    Parameters:
    n : int - integer to check

    Returns true if n is prime, and false otherwise.
    */
    public static boolean check_prime(int n) {
        for(int i = 2; i * i <= n; i++) {
            if(n % i == 0) {
                return false;
            }
        }
        return true;
    }

}
