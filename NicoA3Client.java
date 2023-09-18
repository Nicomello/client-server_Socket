import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.*;

public class NicoA3Client
 {

    public static void main(String[] args) throws Exception {
        // read the file dvr.txt
        BufferedReader reader = new BufferedReader(new FileReader("SRC/dvr.txt"));
        String serverIp = reader.readLine(); // 10.55.1.1
        String clientIp = reader.readLine(); // 10.55.1.2
        int numEntries = Integer.parseInt(reader.readLine()); // 5
        String destIps = reader.readLine(); // 10.55.1.0 10.55.2.0 10.55.3.0 10.55.4.0 10.55.5.0
        String distances = reader.readLine(); // 5 9 8 0 6 
        reader.close();

        // form the DVR message
        StringBuilder sb = new StringBuilder();
        sb.append(serverIp).append("\n");
        sb.append(clientIp).append("\n");
        sb.append(numEntries).append("\n");
        sb.append(destIps).append("\n");;
        sb.append(distances).append("\n");;
        String dvrMessage = sb.toString();

        // make TCP socket connection to server and send DVR message
        Socket socket = new Socket("localhost", 4321);
        OutputStream os = socket.getOutputStream();
        os.write(dvrMessage.getBytes());
        os.flush();
        socket.close();

        // print message after sending DVR message
        System.out.println("DVR message sent");
    }
}
