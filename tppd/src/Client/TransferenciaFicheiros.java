package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TransferenciaFicheiros {

    private static int MAX_SIZE = 10240;

    public TransferenciaFicheiros(){
    }

    public void saveFile(Socket socket,String fileName, File pathReceber){

        try {
            String filepath = pathReceber.getCanonicalPath()+ File.separator+fileName;
            byte[] fileChunk = new byte[MAX_SIZE];
            InputStream in = socket.getInputStream();

            FileOutputStream localFileOutputStream = null;
            localFileOutputStream = new FileOutputStream(filepath);
            int nbytes=0;

            while ((nbytes = in.read(fileChunk)) > 0) {
                //System.out.println("Recebido o bloco n. " + ++contador + " com " + nbytes + " bytes.");
                localFileOutputStream.write(fileChunk, 0, nbytes);
                localFileOutputStream.flush();
                //System.out.println("Acrescentados " + nbytes + " bytes ao ficheiro " + localFilePath+ ".");
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
        //ObjectOutputStream oout=null;
        OutputStream outputStream;
        File path = new File(pathEnviar);

        try {
            requestedCanonicalFilePath = new File(path + File.separator + filename).getCanonicalPath();

            if (!requestedCanonicalFilePath.startsWith(path.getCanonicalPath() + File.separator)) {
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + path.getCanonicalPath() + "!");
                return;
            }

            Socket socket = new Socket(ip,porto);
            ObjectOutputStream oout =new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject(filename);
            //oout.close();

            outputStream = socket.getOutputStream();

            //TransferableObject t = new TransferableObject();
            requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);
            System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");
            //byte[] bytes= new byte[MAX_SIZE*10000];
            int pos =0;
            while ((nbytes = requestedFileInputStream.read(fileChunk)) > 0) {
                //System.arraycopy(fileChunk,0,bytes,pos,fileChunk.length);
                outputStream.write(fileChunk,0,nbytes);
                outputStream.flush();
            }

            //t.setBytes(bytes);
            //t.setFilename(filename);
            //oout.writeObject(t);
            //oout.flush();
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
