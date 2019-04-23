import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Node implements KademliaAPI{
	public HashMap<Integer, ArrayList<KadMessage>> routingTable;
	private NodeID id;	   /* identifier of the node */
	private int buckets;   /* size of the k-buckets  */
	private int maxRTsize; /* maximal size of the routing table, used when it is necessary to visit it all */
	public int neigs;	   /* number of neighbours */
	
	
	 public Node(int idSize, int tabSize, int k) {
		 this.id = new NodeID(idSize);
		 this.buckets = k;
		 this.routingTable = new HashMap<Integer, ArrayList<KadMessage>>();
		 this.maxRTsize = tabSize;
		 this.neigs = 0;
	 }
	 
	 public NodeID getId() {
		 return this.id;
	 }
	 
	 // looking for id, asking to node in "visit"
	 public void recursiveNodeSearch(NodeID searching, HashSet<KadMessage> tocontact) {
		 if(tocontact.isEmpty()) return;
		 Iterator<KadMessage> it = tocontact.iterator();
		 HashSet<KadMessage> acc = new HashSet<KadMessage>();
		 while(it.hasNext()) {
			 KadMessage msg = it.next();
			 
			 if(NodeID.xorDistance(this.id.getId(), msg.getReply().getId()) == 0) continue;
			 
			 if(searching.equals(msg.getReply())) {
				 this.insertRT(msg);
				 return;
			 }
			 if(!this.insertRT(msg)) continue;
			 
			 Node node = Coordinator.nodesRepo.get(msg.getReply());
			 acc.addAll(node.find_node(this.getId(), searching));
			 for(NodeID id : msg.getTraversed()) {
				 Node tmp = Coordinator.nodesRepo.get(id);
				 acc.addAll(tmp.find_node(this.getId(), searching));
			 }
		 }
		 acc.removeAll(tocontact);
		 recursiveNodeSearch(searching, acc);
	 }
	 
	@Override
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

	private ArrayList<KadMessage> getNeighbours(NodeID request) {
		int dist = NodeID.xorDistance(request.getId(),this.id.getId());
		int bucket = (int) (Math.log(dist)/Math.log(2));
		
		ArrayList<KadMessage> neigs = routingTable.get(bucket);
		/*result will be accumulated in res*/
		ArrayList<KadMessage> res = new ArrayList<KadMessage>(this.buckets);
		
		int first = bucket;
		
		if(neigs != null) {
			for(KadMessage msg : neigs)
				res.add(msg);
			while(res.size() != this.buckets) {
				bucket = (bucket+1) % this.maxRTsize;
				if (first==bucket) break;
				ArrayList<KadMessage> next = routingTable.get(bucket);
				if(next != null) {
					for(KadMessage msg : next) {
						res.add(msg);
					}
				}
			}
		}
		else { 
			while(res.size() != this.buckets) {
				bucket = (bucket+1) %this.maxRTsize;
				if (first==bucket) break;
				ArrayList<KadMessage> next = routingTable.get(bucket);
				if(next != null) {
					for(KadMessage msg : next) {
						res.add(msg);
					}
				}
			}
		}
		return res;
	}

	public boolean insertRT(KadMessage msg) {
		// retrieving k-bucket
		int key = (int) (Math.log(NodeID.xorDistance(msg.getReply().getId(),this.id.getId()))/Math.log(2));
		ArrayList<KadMessage> nodes = this.routingTable.get(key);
				
		if(nodes == null) {
			nodes = new ArrayList<KadMessage>(this.buckets);
			
			nodes.add(msg);
			msg.addTraversed(this.id);			
			
			this.routingTable.put(key, nodes);
			this.neigs++;
			
			return true;
		}
		else if(nodes.size() < this.buckets && !nodes.contains(msg)) {
			nodes.add(msg);
			msg.addTraversed(this.id);

			this.neigs++;
			return true;
		}
		return false;
	}
	
	public void printRT() {
		System.out.println("RT of "+this.id.getId());
		for(int i=0; i<this.routingTable.size(); i++) {
			System.out.print(i+": ");
			ArrayList<KadMessage> n = this.routingTable.get(i);
			if(n != null) {
				for(KadMessage msg : n) {
					System.out.print(msg.getReply().getId()+" -> ");
				}
				System.out.print("/");
			}
			System.out.println();
		}
		System.out.println("------------------");
	}
	
}