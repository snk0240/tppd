package Client;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RecebeFicheiroThread extends Thread{
    private String fileName;
    private ComunicacaoComServidor comunicacaoComServidor;
    private Socket socket;
    static final int MAX_SIZE = 10000;

    public RecebeFicheiroThread(ComunicacaoComServidor comunicacaoComServidor, Socket socket){
        this.comunicacaoComServidor=comunicacaoComServidor;
        this.socket=socket;
    }

    @Override
    public void run(){
        try {
            //oin desserializa tipos primitivos de dados e objetos escritos antes pelo ObjectOutputStream (vindo do socket)
            ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            //Recebe o object convertido em String
            fileName = (String) oin.readObject();
            //Cria uma instancia de ficheiro no diretorio onde o utilizador quer receber
            File path = new File(comunicacaoComServidor.utilizador.getCaminhoReceber());
            //Guarda o ficheiro "fileName" pelo socket "socket" no diretorio "path"
            comunicacaoComServidor.transferenciaFicheiros.saveFile(socket,fileName,path);
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