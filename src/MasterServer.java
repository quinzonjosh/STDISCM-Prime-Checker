import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterServer {


    public static void main(String[] args) {

        // try-with-resources: ServerSocket matically closes after try block for proper cleaning of resources
        try (ServerSocket serverSocket = new ServerSocket(4999)) {
            System.out.println("Master server launched!");

            // continuously accept incoming client requests
            while(true){
                Socket clienSocket = serverSocket.accept();
                System.out.println("Client connected: " + clienSocket.getInetAddress() + ": " + clienSocket.getPort());

                // receive the start and end pt from the client
                DataInputStream dataInputStream = new DataInputStream(clienSocket.getInputStream());
                int nStartPoint = dataInputStream.readInt();
                int nEndPoint = dataInputStream.readInt();

                // set fixed num of threads accross all slave servers
                int nThreads = 64;

                // split and send data to slave servers
                splitAndSendDataToSlaveServers(nStartPoint, nEndPoint, nThreads);

            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void splitAndSendDataToSlaveServers(int nStartPoint, int nEndPoint, int nThreads) throws IOException {
        // pass data to slave server at port 5000
        Socket slaveServerSocket = new Socket("localhost", 5000);
        DataOutputStream dataOutputStream = new DataOutputStream(slaveServerSocket.getOutputStream());
        dataOutputStream.writeInt(nStartPoint);
        dataOutputStream.writeInt(nEndPoint);
        dataOutputStream.writeInt(nThreads);

        slaveServerSocket.close();
    }
}

