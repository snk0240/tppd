package Client;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RecebeFicheiroThread extends Thread{
    private String fileName;
    private ClienteComm cli;
    private Socket socket;
    static final int MAX_SIZE = 10000;

    public RecebeFicheiroThread(ClienteComm cli, Socket socket){
        this.cli=cli;
        this.socket=socket;
    }

    @Override
    public void run(){
        try {
            ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            fileName = (String) oin.readObject();

            File path = new File(System.getProperty("user.dir"));

            cli.transferenciaFicheiros.saveFile(socket,fileName,path);
            try {
                socket.close();
            }catch(Exception ee){
                ee.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Transferencia concluida.");
    }
}
