import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Node extends RemoteObject implements KademliaAPI{
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, ArrayList<NodeID>> routingTable;
	private NodeID id;
	private int buckets;
	
	//idSize in bits
	 public Node(int idSize, int tableDim, int k) {
		 this.id = new NodeID(idSize);
		 this.buckets = k;
		 this.routingTable = new HashMap<Integer, ArrayList<NodeID>>(tableDim);
	 }
	 
	 public NodeID getId() {
		 return this.id;
	 }
	 
	 public void recursiveNodeSearch(NodeID id, HashSet<NodeID> visit) {
		 Iterator<NodeID> it = visit.iterator();
		 HashSet<NodeID> acc = new HashSet<NodeID>();
		 while(it.hasNext()) {
			 NodeID n = it.next();
			 this.insertRT(n);
			 it.remove();
			 Node node = Coordinator.nodesRepo.get(n);
			 acc.addAll(node.find_node(this.getId(), id));
		 }
		 recursiveNodeSearch(id, acc);
		 
	 }
	 
	@Override
	public HashSet<NodeID> find_node(NodeID sender, NodeID request) {
		this.insertRT(sender);
		return new HashSet<NodeID>(this.getNeighbours(request));
	}

	private ArrayList<NodeID> getNeighbours(NodeID request) {
		int bucket = this.id.xorDistance(request.getId());
		return routingTable.get(bucket);
	}

	private void insertRT(NodeID sender) {
		ArrayList<NodeID> nodes = this.getNeighbours(sender);
		if(nodes == null) {
			nodes = new ArrayList<NodeID>(this.buckets);
			nodes.add(sender);
			this.routingTable.put(this.id.xorDistance(sender.getId()), nodes);
		}
		else if(nodes.size() < this.buckets) {
			nodes.add(sender);
		}
	}
	
}