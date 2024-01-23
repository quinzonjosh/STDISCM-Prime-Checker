import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int LIMIT;
        int threads;

        System.out.print("Input the upper bound of integers to check: ");
        LIMIT = scanner.nextInt();

        System.out.print("Input the number of threads to use: ");
        threads = scanner.nextInt();

        int rangePerThread = LIMIT / threads;


    }
}