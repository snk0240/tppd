package Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TransfereThread extends Thread {
    ServerSocket serverSocket;
    private int porto;
    String fileName;
    ComunicacaoComServidor comunicacaoComServidor;

    public TransfereThread(int porto,ComunicacaoComServidor comunicacaoComServidor){
        this.porto = porto;
        this.comunicacaoComServidor = comunicacaoComServidor;
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
                RecebeFicheiroThread recebeFicheiroThread = new RecebeFicheiroThread(comunicacaoComServidor,socket);
                recebeFicheiroThread.setDaemon(true);
                recebeFicheiroThread.start();
                System.out.println("Transferencia concluida.");
            } catch (Exception e) {
            }
        }
    }
}
