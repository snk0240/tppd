package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

//Transferência de ficheiros é realizada com ligação temporária, para os clientes que estejam ligados a outros servidores.
//Existe uma ligação permamente entre clientes que estejam ligados ao mesmo Servidor.

public class TransferenciaFicheiros {

    private static int MAX_SIZE = 10000;

    public TransferenciaFicheiros(){
    }

    public void saveFile(Socket socket,String fileName, File pathReceber){

        try {
            //converte o diretorio do pathReceber e o nome do ficheiro no caminho certo do ficheiro
            String filepath = pathReceber.getCanonicalPath()+ File.separator+fileName;
            //prepara um buffer com MAX_SIZE numero de bytes para a transferencia
            byte[] fileChunk = new byte[MAX_SIZE];
            //cria uma InputStream "in" que vai ler do Socket
            InputStream in = socket.getInputStream();

            //prepara o FileOutputStream para receber no diretorio certo "filepath" já convertido
            FileOutputStream localFileOutputStream = new FileOutputStream(filepath);

            int nbytes=0;
            //recebe ficheiro do socket
            while ((nbytes = in.read(fileChunk)) > 0) {
                //escreve no diretorio certo o que "nbytes" de bytes que recebeu no buffer
                localFileOutputStream.write(fileChunk, 0, nbytes);
                //força a dar flush no buffer para que sejam escritos todos os bytes
                localFileOutputStream.flush();
            }
            localFileOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void LoadFile(String filename, InetAddress ip, int porto, String pathEnviar){

        String requestedCanonicalFilePath;
        FileInputStream requestedFileInputStream = null;

        byte []fileChunk = new byte[MAX_SIZE];
        int nbytes;
        OutputStream outputStream;
        File path = new File(pathEnviar);

        try {
            //Cria uma instancia de ficheiro no diretorio onde o utilizador quer receber
            requestedCanonicalFilePath = new File(path + File.separator + filename).getCanonicalPath();
            //verifica se houve sucesso na sua criação
            if (!requestedCanonicalFilePath.startsWith(path.getCanonicalPath() + File.separator)) {
                System.out.println("Nao e permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + path.getCanonicalPath() + "!");
                return;
            }

            //cria ObjectOutputStream que escreverá no socket o objeto com nome "filename"
            Socket socket = new Socket(ip,porto);
            ObjectOutputStream oout =new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject(filename);
            oout.close();

            //devolve um output para escrever no socket
            outputStream = socket.getOutputStream();

            //abre o ficheiro para leitura
            requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);
            System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

            //envia ficheiro pelo socket
            while ((nbytes = requestedFileInputStream.read(fileChunk)) > 0) {
                outputStream.write(fileChunk,0,nbytes);
                outputStream.flush();
            }
            System.out.println("Transferencia concluida");
            if(requestedFileInputStream != null) {
                try {
                    requestedFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
