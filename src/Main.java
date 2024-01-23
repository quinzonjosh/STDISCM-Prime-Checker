import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime;

        // USER INPUT
        System.out.print("Enter the upper bound of integers to check: ");
        int nLimit = scanner.nextInt();

        System.out.print("Enter the number of threads to use: ");
        int nThreads = scanner.nextInt();

        // START TIME AFTER USER INPUT
//        startTime = System.currentTimeMillis();

        // SETUP THREADS AND NUMBER RANGE PER THREAD
        List<Thread> threads = new ArrayList<>();
        int nRangePerThread = (nLimit / nThreads) + 1;
        System.out.println("numbers per thread: " + nRangePerThread + "\n");

        // ITERATE THRU EVERY THREAD
        for(int i=0; i<nThreads; i++){
            //SETUP RANGE OF NUMBERS TO BE ASSIGNED ON THE CURRENT THREAD
            int start = i * nRangePerThread + 2;
            int end = (i + 1) * nRangePerThread;

            System.out.println(start);
            System.out.println(end);
            System.out.println();

            // CREATE A THREAD THAT ITERATES THRU EACH OF ITS NUMBERS


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