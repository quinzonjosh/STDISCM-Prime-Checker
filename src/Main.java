import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int DEFAULT_THREAD_COUNT = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime, endTime;
        int nLimit;
        int nThreads;

//        // USER INPUT
//        System.out.print("Enter the upper bound of integers to check: ");
//        nLimit = scanner.nextInt();
//        scanner.nextLine(); // Consume the leftover newline from nextInt()
//
//        System.out.print("Enter the number of threads to use: ");
//        String input = scanner.nextLine();
////        nThreads = input.isEmpty() ? DEFAULT_THREAD_COUNT : Integer.parseInt(input);
//        if (input.isEmpty()) {
//            nThreads = DEFAULT_THREAD_COUNT;
//            System.out.println("No input entered. Using default thread count value: " + DEFAULT_THREAD_COUNT);
//        } else {
//            nThreads = Integer.parseInt(input);
//        }
//
//        scanner.close();

        long[][] data = new long[5][18];
        int col = 0;

        nLimit = 10000000;


        for(int attempt = 0; attempt < 5; attempt++){

            col = 0;
            for(nThreads = 1; nThreads <= 131072; nThreads *=2){

//            for(int num = 0; num < 3; num++){

                // START TIME AFTER USER INPUT
                startTime = System.currentTimeMillis();

                // SETUP THREADS AND NUMBER RANGE PER THREAD
                List<Thread> threads = new ArrayList<>();
                int nRangePerThread = nLimit / nThreads;
                System.out.printf("\nNumbers per thread: %d / %d = %d\n\n", nLimit, nThreads, nRangePerThread);

//        List<Integer> primes = new CopyOnWriteArrayList<>();  // The CopyOnWriteArrayList handles mutual exclusion internally (thread-safe).
                List<Integer> primes = new ArrayList<Integer>();  // This is not thread-safe, will result in race conditions without mutexes/locks.

                Lock primesLock = new ReentrantLock();

                // ITERATE THRU EVERY THREAD
                for(int i=1; i<=nThreads; i++) {
                    // SETUP RANGE OF NUMBERS TO BE ASSIGNED ON THE CURRENT THREAD
                    int start = (i - 1) * nRangePerThread + 2;
                    int end = i * nRangePerThread + 1;

                    // LAST THREAD COVERS THE REMAINING RANGE OF IF nLimit % nThreads != 0
                    if (i == nThreads) {
                        end = nLimit;
                    }

                    // CHECK RESULTS
                    System.out.println("Thread " + i + " range:");
                    System.out.println("Start: " + start);
                    System.out.println("End: " + end);
                    System.out.println();

                    // THREAD CREATION
                    Thread thread = new Thread(new PrimeTask(start, end, primes, primesLock));
                    threads.add(thread);
                    thread.start();
                }

                // WAIT FOR ALL THREADS TO FINISH
                for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // END TIME BEFORE PRINTING NO. OF PRIMES
                endTime = System.currentTimeMillis();

                System.out.printf("%d primes were found.\n", primes.size());
                System.out.println("Time taken: " + (endTime - startTime) + " ms");

//            }

                data[attempt][col] = endTime - startTime;
                col++;

            }




        }



        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 18; j++){
                System.out.printf("%d, ", data[i][j]);
            }
            System.out.println();
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