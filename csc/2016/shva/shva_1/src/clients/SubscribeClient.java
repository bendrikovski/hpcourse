package clients;

import communication.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class SubscribeClient {

    private static final Logger LOG = Logger.getLogger("Client");
    private static final int PORT = 33333;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", PORT)) {
            makeRequest(socket);
        } catch (IOException e) {
            LOG.warning("Connection error");
            e.printStackTrace();
        }
    }


    public static void makeRequest(Socket socket) {
        Protocol.Subscribe task = buildSubscribe();
        Protocol.ServerRequest.Builder builder = Protocol.ServerRequest.newBuilder();
        builder.setRequestId(1).setClientId("user").setSubscribe(task);
        Protocol.ServerRequest request = builder.build();
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {
            outputStream.write(request.getSerializedSize());
            request.writeTo(outputStream);

            int size = inputStream.read();
            byte[] buf = new byte[size];
            inputStream.read(buf);
            System.out.println(Protocol.ServerResponse.parseFrom(buf).getSubscribeResponse().getValue());
        } catch (IOException e) {
            LOG.warning("Send request error");
            e.printStackTrace();
        }
    }

    public static Protocol.Subscribe buildSubscribe() {
        Protocol.Subscribe.Builder builder = Protocol.Subscribe.newBuilder();
        builder.setTaskId(0);
        return builder.build();
    }


}
