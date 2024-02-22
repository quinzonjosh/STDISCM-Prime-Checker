import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        // ServerSocket constructor params:
        // param 1: server IP address
        //      - on the computer of the server, open cmd, type ipconfig and get the ipv4 address
        // param 2: port number to use
        Socket socket = new Socket("localhost", 4999);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        int nStartPoint;
        int nEndPoint;

        // USER INPUT
        System.out.print("Input start point: ");
        nStartPoint = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline from nextInt()

        System.out.print("Input end point: ");
        nEndPoint = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline from nextInt()

        dataOutputStream.writeInt(nStartPoint);
        dataOutputStream.writeInt(nEndPoint);

        dataOutputStream.close();
        socket.close();


    }

}
