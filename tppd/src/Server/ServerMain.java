package Server;

import Client.ClientMain;
import Dados.Msg;
import Dados.Msgt;
import Dados.Utilizador;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ServerMain extends UnicastRemoteObject implements ServerServiceInterface{

    public static final String EXIT = "EXIT";

    public static final String SERVICE_NAME = "GetRemoteService";
    public List<GetServiceObserverInterface> observers;

    public Server servidor1;
    public ServerComm s1;

    public ServerMain(Server servidor11, ServerComm s11) throws RemoteException{
        servidor1 = servidor11;
        s1 = s11;
        observers = new ArrayList<>();
    }

    @Override
    public boolean registaUserRmi(Utilizador utilizador, ClientMain cli) throws RemoteException {
        try{

            boolean conseguiu = cli.registaruser(utilizador);
            if(conseguiu=false){
                System.out.print("Surgiu um problema ao tentar registar o user!");

                notifyObservers("Surgiu um problema na autenticacao do user!");
                try{
                    notifyObservers(" num cliente em " + getClientHost());
                }catch(ServerNotActiveException ex){}
                notifyObservers(".\n\n");

                return false;
            }
            notifyObservers("Utilizador registado com sucesso");
            try{
                notifyObservers(" num cliente em " + getClientHost());
            }catch(ServerNotActiveException e){}
            notifyObservers(".\n\n");

            return true;

        } catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);

            notifyObservers("Ocorreu um problema ao registar o user");
            try{
                notifyObservers(" por um cliente em " + getClientHost());
            }catch(ServerNotActiveException ex){}
            notifyObservers(".\n\n");
        }
        return false;
    }

    @Override
    public boolean enviaMsgTodosRmi(Msgt msg, ClientMain cli) throws RemoteException {
        try{
            if(cli.enviaMsgTodos(msg)!=false){
                System.out.print("Surgiu um problema ao tentar enviar msg!");

                notifyObservers("Surgiu um problema no envio da msg!");
                try{
                    notifyObservers(" num cliente em " + getClientHost());
                }catch(ServerNotActiveException ex){}
                notifyObservers(".\n\n");

                return false;
            }
            notifyObservers("Mensagem enviada com sucesso");
            try{
                notifyObservers(" num cliente em " + getClientHost());
            }catch(ServerNotActiveException e){}
            notifyObservers(".\n\n");

            return true;

        } catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);

            notifyObservers("Ocorreu um problema ao enviar mensagem");
            try{
                notifyObservers(" por um cliente em " + getClientHost());
            }catch(ServerNotActiveException ex){}
            notifyObservers(".\n\n");
        }
        return false;
    }

    @Override
    public synchronized void addUserListener(GetServiceObserverInterface observer) throws RemoteException {
        if(!observers.contains(observer)){
            observers.add(observer);
            System.out.println("+ um observador.");
        }
    }

    @Override
    public synchronized void removeUserListener(GetServiceObserverInterface observer) throws RemoteException {
        if(observers.remove(observer))
            System.out.println("- um observador.");
    }

    public synchronized void notifyObservers(String msg)
    {
        int i;

        for(i=0; i < observers.size(); i++){
            try{
                observers.get(i).notifyNewOperationConcluded(msg);
            }catch(RemoteException e){
                observers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }

    public static void main(String[] args) throws RemoteException{
        int portUDP;
        int portTCP;
        String ipDB;
        Server servidor;
        ServerComm s;

        if (args.length != 3) {
            //portos de escuta tcp e udp e maquina da sua BD
            System.err.println("The arguments weren't introduced correctly: <UDP port>  <TCP port>  <BD IP>");
            return;
        }

        try {
            portUDP = Integer.parseInt(args[0]);
            portTCP = Integer.parseInt(args[1]);
            ipDB = args[2];
            System.out.println("Server UDP Port is " + portUDP + " and TCP Port: is " + portTCP + ", BD's ip is " + ipDB + "\n");

            servidor = new Server(ipDB, portTCP);

            s = new ServerComm(portUDP, portTCP, ipDB, servidor);
            s.start();

            try{

                Registry r;

                try{
                    System.out.println("Tentativa de lancamento do registry no porto " +Registry.REGISTRY_PORT + "...");
                    r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                    System.out.println("Registry lancado!");
                }catch(RemoteException e){
                    System.out.println("Registry provavelmente ja' em execucao!");
                    r = LocateRegistry.getRegistry();
                }

                ServerMain fileService = new ServerMain(servidor, s);

                System.out.println("Servico GetRemoteFile criado e em execucao ("+fileService.getRef().remoteToString()+"...");

                /*
                 * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
                 * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
                 */

                r.bind(SERVICE_NAME, fileService);

                System.out.println("Servico " + SERVICE_NAME + " registado no registry...");

                /*
                 * Para terminar um servico RMI do tipo UnicastRemoteObject:
                 *
                 *  UnicastRemoteObject.unexportObject(fileService, true);
                 */

            }catch(RemoteException e){
                System.out.println("Erro remoto - " + e);
                System.exit(1);
            }catch(Exception e){
                System.out.println("Erro - " + e);
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.err.println("The BD port should be an unsigned int:\t" + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}