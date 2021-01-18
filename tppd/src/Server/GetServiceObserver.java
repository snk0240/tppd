package Server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GetServiceObserver extends UnicastRemoteObject implements GetServiceObserverInterface {
    public GetServiceObserver() throws RemoteException {}

    public void notifyNewOperationConcluded(String description) throws RemoteException
    {
        System.out.print(description);
    }

    public static void main(String[] args) {
        try{
            //Cria e lanca o servico
            GetServiceObserver observer = new GetServiceObserver();
            System.out.println("Servico GetRemoteFileObserver criado e em execucao...");

            //Localiza o servico remoto nomeado "GetRemoteFile"
            String objectUrl = "rmi://127.0.0.1/GetRemoteService"; //rmiregistry on localhost

            if(args.length > 0)
                objectUrl = "rmi://"+args[0]+"/GetRemoteService";

            ServerServiceInterface getRemoteService = (ServerServiceInterface) Naming.lookup(objectUrl);

            //adiciona observador no servico remoto
            getRemoteService.addUserListener(observer);

            System.out.println("<Enter> para terminar...");
            System.out.println();
            System.in.read();

            getRemoteService.removeUserListener(observer);
            UnicastRemoteObject.unexportObject(observer, true);

        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(IOException | NotBoundException e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
