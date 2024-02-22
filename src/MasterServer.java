import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class MasterServer {

    private static final Queue<Request> requestQueue = new LinkedList<>();

    public static void main(String[] args) {

        // try-with-resources: ServerSocket matically closes after try block for proper cleaning of resources
        try (ServerSocket serverSocket = new ServerSocket(4999)) {
            System.out.println("Master server launched!");

            // continuously accept incoming client requests
            while(true){
               // accept slave server connecting to master server
               Socket socket = serverSocket.accept();
               System.out.println("Client connected: " + socket.getInetAddress() + ": " + socket.getPort());

               // handle client request by adding to the queue
               handleClientInput(socket);

               // sanity check if client input was received
               printRequestQueue();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void printRequestQueue() {
        System.out.println("Request Queue:");
        for (Request request : requestQueue) {
            System.out.println("Client: " + request.getClientInfo() + ", Start Point: " + request.getStartPoint() + ", End Point: " + request.getEndPoint());
        }
        System.out.println();
    }

    private static void handleClientInput(Socket socket) {
        try{
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            int nStartPoint = dataInputStream.readInt();
            int nEndPoint = dataInputStream.readInt();

            // add request to the queue
            requestQueue.offer(new Request(socket.getInetAddress().getHostAddress(), nStartPoint, nEndPoint));

            dataInputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

