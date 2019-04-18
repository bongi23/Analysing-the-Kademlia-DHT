import java.rmi.RemoteException;
import java.util.LinkedHashMap;

public class Node implements KademliaAPI{
	private LinkedHashMap<Integer, Node> routingTable;
	private byte[] id;
	
	 public Node(int idSize) {
		 int size = idSize > 8 ? idSize/8 : 1;
		 this.id = new byte[size];
		 this.routingTable = new LinkedHashMap<Integer, Node>();
	 }
	
	@Override
	public void find_node(byte[] id) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}