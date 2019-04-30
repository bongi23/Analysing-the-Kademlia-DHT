import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Node {
	
	private HashMap<Integer, ArrayList<KadMessage>> routingTable;
	private NodeID id;	   /* identifier of the node */
	private int buckets;   /* size of the k-buckets  */
	
	public int neigs;	   /* number of neighbours */
	
	
	 public Node(int idSize, int k) {
		 this.id = new NodeID(idSize);
		 this.buckets = k;
		 this.routingTable = new HashMap<Integer, ArrayList<KadMessage>>();
		 this.neigs = 0;
	 }
	 
	 public NodeID getId() {
		 return this.id;
	 }
	 
	 /*
	  * Returns the bucket in which request should be stored
	  * */
	 private int getBucket(NodeID request) {
		double dist = NodeID.xorDistance(request.getId(),this.id.getId());
		int bucket = (int) (Math.log(dist)/Math.log(2));
		
		return bucket;
	
	 }
	 /*
	  * Recursive function that for every message in toContact first check if it contains
	  * the searching node, if not the id contained in the message is contacted and the
	  * same for each id in toContact.traversed
	  * 
	  * This method ends when there aren't id to contact or when searching is found
	  * 
	  * contacted is used in order to avoid re-contacting the same node obtained by different messages
	  * */
	 public void recursiveNodeSearch(NodeID searching, HashSet<KadMessage> toContact, HashSet<KadMessage> contacted) {
		 if(toContact.isEmpty()) return;
		 
		 Iterator<KadMessage> it = toContact.iterator();
		 HashSet<KadMessage> acc = new HashSet<KadMessage>();
		 
		 while(it.hasNext()) {
			 KadMessage msg = it.next();
			 
			 if(this.id.equals(msg.getReply())) continue;
			 
			 // found!!
			 if(searching.equals(msg.getReply())) {
				 this.insertRT(msg);
				 return;
			 }
			 
			 if(!this.insertRT(msg)) continue;
			 
			 // obtaining node object from coordinator
			 Node node = Coordinator.nodesRepo.get(msg.getReply());
			 acc.addAll(node.find_node(this.getId(), searching));
			 
			 contacted.add(msg);
			 
			 // contacting every node traversed by the message
			 for(NodeID id : msg.getTraversed()) {
				 if(this.id.equals(id)) continue;
				 
				 Node tmp = Coordinator.nodesRepo.get(id);
				 acc.addAll(tmp.find_node(this.getId(), searching));
				 contacted.add(msg);
			 }
		 }
		 
		 acc.removeAll(contacted);
		 recursiveNodeSearch(searching, acc, contacted);
	 }
	
	/*
	 * "Exposed" API by every node. Returns the list of closer neighbours to request 
	 */
	public HashSet<KadMessage> find_node(NodeID sender, NodeID request) {
		if(request.equals(this.id)) {
			HashSet<KadMessage> res = new HashSet<KadMessage>();
			res.add(new KadMessage(this.id));
			return res;
		}
		ArrayList<KadMessage> neig = this.getNeighbours(request);
		
		/* if there is space, add the sender of the request to my RT*/
		this.insertRT(new KadMessage(sender));
		
		if(neig == null) return new HashSet<KadMessage>();
		return new HashSet<KadMessage>(neig);
	}
	
	/*
	 * Logic of "find_node" function, returns when possible this.buckets neighbours 
	 * It may traverse every bucket of the routing table
	 * */
	private ArrayList<KadMessage> getNeighbours(NodeID request) {
		int bucket = this.getBucket(request);
		
		ArrayList<KadMessage> neigs = routingTable.get(bucket);
		
		/*result will be accumulated in res*/
		ArrayList<KadMessage> res = new ArrayList<KadMessage>(this.buckets);
		
		int first = bucket;
		
		if(neigs != null) {
			for(KadMessage msg : neigs)
				res.add(msg);
		}
		while(res.size() != this.buckets) {
			bucket = (bucket+1) % this.id.getSize();
			
			if (first==bucket) break;
			
			ArrayList<KadMessage> next = routingTable.get(bucket);
			if(next != null)
				for(KadMessage msg : next) 
					res.add(msg);
		}
		return res;
	}

	/*
	 * Store a message into the routing table, if there is
	 * enough space in the right bucket
	 * */
	public boolean insertRT(KadMessage msg) {
		// retrieving k-bucket
		int key = this.getBucket(msg.getReply());
		ArrayList<KadMessage> nodes = this.routingTable.get(key);
				
		if(nodes == null)
			nodes = new ArrayList<KadMessage>(this.buckets);
	
		if(nodes.size() < this.buckets && !nodes.contains(msg)) {
			nodes.add(msg);
			msg.addTraversed(this.id);
			this.routingTable.put(key, nodes);
			
			this.neigs++;
			return true;
		}
		
		return false;
	}

	public HashMap<Integer, ArrayList<KadMessage>> getRoutingTable() {
		return this.routingTable;
	}
	
}