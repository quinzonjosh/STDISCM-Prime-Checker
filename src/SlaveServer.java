import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SlaveServer {

    public static void main(String[] args) {
        try(ServerSocket slaveServerSocket = new ServerSocket(5000)){
            System.out.println("Slave server launched!");

            while (true){
                Socket masterServerSocket = slaveServerSocket.accept();
                System.out.println("Slave server connected to master server");

                receiveDataFromMasterServer(masterServerSocket);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void receiveDataFromMasterServer(Socket masterServerSocket) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(masterServerSocket.getInputStream());
        int nStartPoint = dataInputStream.readInt();
        int nEndPoint = dataInputStream.readInt();
        int nThreads = dataInputStream.readInt();

        System.out.println("Slave Server Received: ");
        System.out.println("Start point: " + nStartPoint);
        System.out.println("End point: " + nEndPoint);
        System.out.println("Thread count: " + nThreads);

        masterServerSocket.close();

    }

}
