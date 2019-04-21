import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Node implements KademliaAPI{
	public HashMap<Integer, ArrayList<NodeID>> routingTable;
	private NodeID id;
	private int buckets;
	private int maxRTsize;
	public int neigs;
	
	//idSize in bits
	 public Node(int idSize, int tabSize, int k) {
		 this.id = new NodeID(idSize);
		 this.buckets = k;
		 this.routingTable = new HashMap<Integer, ArrayList<NodeID>>();
		 this.maxRTsize = tabSize;
		 this.neigs = 0;
	 }
	 
	 public NodeID getId() {
		 return this.id;
	 }
	 
	 // looking for id, asking to node in "visit"
	 public void recursiveNodeSearch(NodeID searching, HashSet<NodeID> tocontact) {
		 if(tocontact.isEmpty()) return;
		 Iterator<NodeID> it = tocontact.iterator();
		 HashSet<NodeID> acc = new HashSet<NodeID>();
		 while(it.hasNext()) {
			 NodeID n = it.next();
			 
			 if(NodeID.xorDistance(this.id.getId(), n.getId()) == 0) continue;
			 
			 if(!this.insertRT(n) || searching.equals(n)) continue;
			 
			 Node node = Coordinator.nodesRepo.get(n);
			 acc.addAll(node.find_node(this.getId(), searching));
		 }
		 acc.removeAll(tocontact);
		 recursiveNodeSearch(searching, acc);
	 }
	 
	@Override
	public HashSet<NodeID> find_node(NodeID sender, NodeID request) {
		ArrayList<NodeID> neig = this.getNeighbours(request); // invertire
		this.insertRT(sender);
		if(neig == null) return new HashSet<NodeID>();
		return new HashSet<NodeID>(neig);
	}

	private ArrayList<NodeID> getNeighbours(NodeID request) {
		int dist = NodeID.xorDistance(request.getId(),this.id.getId());
		int bucket = (int) (Math.log(dist)/Math.log(2));
		
		ArrayList<NodeID> neigs = routingTable.get(bucket);
		ArrayList<NodeID> res = new ArrayList<NodeID>(this.buckets);
		
		int first = bucket;
		
		if(neigs != null) {
			for(NodeID id : neigs)
				res.add(id);
			while(res.size() != this.buckets) {
				bucket = (bucket+1) %this.maxRTsize;
				if (first==bucket) break;
				ArrayList<NodeID> next = routingTable.get(bucket);
				if(next != null) {
					for(NodeID id : next) {
						res.add(id);
					}
				}
			}
		}
		else { 
			while(res.size() != this.buckets) {
				bucket = (bucket+1) %this.maxRTsize;
				if (first==bucket) break;
				ArrayList<NodeID> next = routingTable.get(bucket);
				if(next != null) {
					for(NodeID id : next) {
						res.add(id);
					}
				}
			}
		}
		return res;
	}

	public boolean insertRT(NodeID id) {
		// retrieving k-bucket
		int key = (int) (Math.log(NodeID.xorDistance(id.getId(),this.id.getId()))/Math.log(2));
		ArrayList<NodeID> nodes = this.routingTable.get(key);
		
		if(nodes == null) {
			nodes = new ArrayList<NodeID>(this.buckets);
			nodes.add(id);
						
			this.routingTable.put(key, nodes);
			this.neigs++;
			return true;
		}
		else if(nodes.size() < this.buckets && !nodes.contains(id)) {
			nodes.add(id);
			this.neigs++;
			return true;
		}
		return false;
	}
	
	public void printRT() {
		System.out.println("RT of "+this.id.getId());
		for(int i=0; i<this.routingTable.size(); i++) {
			System.out.print(i+": ");
			ArrayList<NodeID> n = this.routingTable.get(i);
			if(n != null) {
				for(NodeID id : n) {
					System.out.print(id.getId()+" -> ");
				}
			}
			System.out.println();
		}
		System.out.println("------------------");
	}
	
}