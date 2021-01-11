package Client;

import Dados.Msgt;
import Dados.Utilizador;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    public boolean registaruser(Utilizador user) throws RemoteException;
    public boolean enviaMsgTodos(Msgt msg) throws RemoteException;
}
