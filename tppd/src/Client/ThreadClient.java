package Client;

import Dados.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThreadClient extends Thread {     //Thread para tratar a comunicação com o Servidor
    private Socket socket;
    private ClienteComm cli;
    private ObjectOutputStream oout;
    private ObjectInputStream oin;

    public ThreadClient(Socket s,ClienteComm cli,ObjectOutputStream oout,ObjectInputStream oin){
        this.socket =s;
        this.cli=cli;
        this.oout = oout;
        this.oin = oin;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object obj = oin.readObject();

                if (obj instanceof Msg) {
                    System.out.println("Recebi objeto Msg\n");
                    System.out.println(((Msg) obj).getTexto());

                } else if (obj instanceof String) {
                    String str = (String) obj;
                    System.out.println("Recebi objeto String: " + str + '\n');

                    if (str.equalsIgnoreCase("Database Changed")) {
                        System.out.println("A base de dados foi atualizada\n");
                        oout.writeObject(new String("Database Update"));
                    }
                } else if (obj instanceof Request) {
                    Request request = (Request) obj;
                    EnviaFicheiroThread enviaFicheiroThread = new EnviaFicheiroThread(request,cli);
                    enviaFicheiroThread.setDaemon(true);
                    enviaFicheiroThread.start();
                } else if(obj instanceof DatabaseUpdate)
                {
                    //cli.updatedDb = (DatabaseUpdate) obj;
                    System.out.println("A minha base de dados interna foi atualizada\n");
                }
                else if(obj instanceof Boolean)
                {
                    if((Boolean)obj)
                    {
                        System.out.println("Registo realizado com sucesso!\n");
                        cli.autenticado=true;
                        cli.comecaDownloadsThread();
                    }
                    else
                        System.out.println("Registo falhado...\n");
                }
                else if(obj instanceof Utilizador)
                {
                    cli.utilizador = (Utilizador) obj;

                    System.out.println("Recebi obj Utilizador!\n");
                    cli.autenticado=true;
                    cli.comecaDownloadsThread();
                }
                else if(obj instanceof ArrayStoreException){
                        System.out.println("O login falhou...\n");
                }
                else if(obj instanceof Database){
                    cli.setDatabase((Database)obj);
                }
                else if(obj instanceof Map){
                    Map<String,List<String>>mapa = (Map) obj;
                    List<String>keys = new ArrayList<>();
                    keys.addAll(mapa.keySet());
                    if(keys.get(0).equals("utilizadores")){
                        mostraUtilizadores(mapa.get("utilizadores"));
                    }
                    else if(keys.get(0).equals("canais")){
                        mostraFicheiros(mapa.get("canais"));
                    }
                    else if(keys.get(0).equals("ficheiros")){
                        mostraFicheiros(mapa.get("ficheiros"));
                    }
                    else if(keys.get(0).equals("msgs")){
                        mostraFicheiros(mapa.get("msgs"));
                    }
                }
                //oout.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mostraUtilizadores(List<String>users){
        for(String s:users){
            System.out.println(s);
        }
    }
    public void mostraFicheiros(List<String> ficheiros){
        for(String s: ficheiros){
            System.out.println(s);
        }
    }
}
