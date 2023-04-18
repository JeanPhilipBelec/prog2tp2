package client;


public class ClientLauncher {

    public final static int PORT = 1337;

    public static void main(String[] args) {
        Client client;
        try {
            client = new Client(PORT);
            System.out.println("Client is running...");
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
