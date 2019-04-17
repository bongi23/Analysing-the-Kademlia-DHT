import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KademliaAPI extends Remote {

    public void find_node() throws RemoteException;
}