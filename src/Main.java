import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime;
        long endTime;

        // USER INPUT
        System.out.print("Enter the upper bound of integers to check: ");
        int nLimit = scanner.nextInt();

        System.out.print("Enter the number of threads to use: ");
        int nThreads = scanner.nextInt();

        scanner.close();

        // START TIME AFTER USER INPUT
        startTime = System.currentTimeMillis();

        // SETUP THREADS AND NUMBER RANGE PER THREAD
        List<Thread> threads = new ArrayList<>();
        int nRangePerThread = nLimit / nThreads;
        System.out.println("numbers per thread: " + nRangePerThread + "\n");

//        List<Integer> primes = new CopyOnWriteArrayList<>();  // The CopyOnWriteArrayList handles mutual exclusion internally (thread-safe).
        List<Integer> primes = new ArrayList<Integer>();  // This is not thread-safe, will result in race conditions without mutexes/locks.

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
            Thread thread = new Thread(new PrimeTask(start, end, primes));
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
    }

    static class PrimeTask implements Runnable{
        private final int start;
        private final int end;
        private final List<Integer> primes;

        PrimeTask(int start, int end, List<Integer> primes) {
            this.start = start;
            this.end = end;
            this.primes = primes;
        }

        @Override
        public void run() {
            for (int currentNum = start; currentNum <= end; currentNum++) {
                if (check_prime(currentNum)) {
                    primes.add(currentNum);
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