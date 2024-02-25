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

        int nMidPoint = (nEndPoint - nStartPoint) / 2 + nStartPoint;

        // pass data to slave server 1 at port 5000
        // replace "localhost with computer server's ipv4 address"
        Socket slaveServerSocket1 = new Socket("localhost", 5000);
        DataOutputStream dataOutputStream1 = new DataOutputStream(slaveServerSocket1.getOutputStream());
        dataOutputStream1.writeInt(nStartPoint);
        dataOutputStream1.writeInt(nMidPoint);
        dataOutputStream1.writeInt(nThreads);

        slaveServerSocket1.close();

        // pass data to slave server 2 at port 5001
        // replace "localhost with computer server's ipv4 address"
//        Socket slaveServerSocket2 = new Socket("localhost", 5001);
//        DataOutputStream dataOutputStream2 = new DataOutputStream(slaveServerSocket2.getOutputStream());
//        dataOutputStream2.writeInt(nMidPoint + 1);
//        dataOutputStream2.writeInt(nEndPoint);
//        dataOutputStream2.writeInt(nThreads);
//
//        slaveServerSocket2.close();
    }
}

