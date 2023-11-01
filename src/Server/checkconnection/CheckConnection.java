package Server.checkconnection;

import javax.swing.table.DefaultTableModel;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class CheckConnection implements Runnable {

    private DataInputStream dis;
    private ObjectOutputStream oos;
    private Socket client;
    private DefaultTableModel tableModel;
    private Map<String, Socket> clients;

    public CheckConnection(Socket client, DefaultTableModel tableModel, Map<String, Socket> clients) {
        this.tableModel = tableModel;
        this.clients = clients;
        this.client = client;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for(String key : clients.keySet()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(clients.get(key).getInputStream());
                dataInputStream.read();
            } catch (IOException e) {
                clients.remove(this.client.getInetAddress().getHostName() + "_" + this.client.getInetAddress().getHostAddress());
                int rowCount = tableModel.getRowCount();

                for (int i = 0; i < rowCount; i++) {
                    String hostName = (String) tableModel.getValueAt(i, 0);
                    String hostAddress = (String) tableModel.getValueAt(i, 1);
                    if (hostName.equals(this.client.getInetAddress().getHostName()) && hostAddress.equals(this.client.getInetAddress().getHostAddress())) {
                        tableModel.removeRow(i);
                    }
                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}