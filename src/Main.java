import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int LIMIT = 10000000;  // 10 Million

    public static void main(String[] args) {
        List<Integer> primes = new ArrayList<Integer>();

        for(int current_num = 2; current_num <= LIMIT; current_num++) {
            if(check_prime(current_num)) {
                primes.add(current_num);
            }
        }

        System.out.printf("%d primes were found.\n",primes.size());
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