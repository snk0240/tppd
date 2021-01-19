package Server;

import Client.ClientMain;
import Dados.Msgt;
import Dados.Utilizador;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerServiceInterface extends Remote {
    public void addUserListener(GetServiceObserverInterface observer) throws RemoteException;
    public void removeUserListener(GetServiceObserverInterface observer) throws RemoteException;
    //ClientMainInterface?
    public boolean registaUserRmi(Utilizador utilizador, ClientMain cli) throws RemoteException;
    public boolean enviaMsgTodosRmi(Msgt msg, ClientMain cli) throws RemoteException;
}
