package Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TransfereThread extends Thread {
    ServerSocket serverSocket;
    private int porto;
    String fileName;
    ClienteComm cli;

    public TransfereThread(int porto,ClienteComm cli){
        this.porto = porto;
        this.cli = cli;
        try {
            serverSocket = new ServerSocket(porto);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run(){
        while(true) {
            try {
                System.out.println("TransfereThread iniciada\n");
                Socket socket = serverSocket.accept();
                System.out.println("Recebi pedido para receber ficheiro de: " + socket.getInetAddress().getHostAddress());
                RecebeFicheiroThread recebeFicheiroThread = new RecebeFicheiroThread(cli,socket);
                recebeFicheiroThread.setDaemon(true);
                recebeFicheiroThread.start();
                System.out.println("Transferencia concluida.");
            } catch (Exception e) {

            }
        }
    }

}
