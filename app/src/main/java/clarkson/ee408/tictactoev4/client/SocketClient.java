package clarkson.ee408.tictactoev4.client;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class SocketClient {

    // Static instance
    private static SocketClient instance;

    // Socket attributes
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Gson gson;
    private final String hostName;
    private final int port;

    // Private constructor for Singleton
    private SocketClient() {
        hostName = "10.0.2.2";
        port = 6000;
        gson = new Gson();
    }

    // Synchronized getInstance method for thread safety

    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    // Close socket connection and IO streams
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions properly
        }
    }

    // Send a request without expecting a response
    public <T> T sendRequest(Object request, Class<T> responseClass) {
        synchronized (this) {
            if (socket == null || socket.isClosed()) {
                try {
                    // Connect to the server
                    socket = new Socket(hostName, port);
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    inputStream = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        T response = null;
        synchronized (this) {
            try {
                String jsonRequest = gson.toJson(request);
                outputStream.writeUTF(jsonRequest); // Send the serialized request
                outputStream.flush(); // Ensure data is sent immediately

                // Listen for a response from the server
                String jsonResponse = inputStream.readUTF();
                response = gson.fromJson(jsonResponse, responseClass);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception properly
            }
        }
        return response; // Return the response received from server
    }
}