package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetServiceObserverInterface extends Remote {
    public void notifyNewOperationConcluded(String description) throws RemoteException;
}
