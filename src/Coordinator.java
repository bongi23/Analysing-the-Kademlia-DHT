import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Coordinator {
	
	static ArrayList<Node> nodes;
	static HashMap<NodeID, Node> nodesRepo;
	static int netDim;
	static int bits;
	static int tables;
	static int buckets;
	
	
	public Coordinator(int n, int bitsize, int tablesize, int bucketnum) {
		nodes = new ArrayList<Node>(n);
		netDim = n;
		bits = bitsize;
		tables = tablesize; //maxsize of routing tables
		buckets = bucketnum;
		nodesRepo = new HashMap<NodeID, Node>();
	}
	
	public static Node randomNode() {
		Random r = new Random();
		return nodes.get(r.nextInt(nodes.size()));
	}
	
	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Error! Provide 4 arguments: #nodes, #bits_of_id, #k_buckets, dim_of_buckets");
			return;
		}
		int numberOfNodes = Integer.parseInt(args[0]);
		int idSize = Integer.parseInt(args[1]);	 		// in bits
		int routingTableMaxDim = idSize;
		int bucketsSize = Integer.parseInt(args[2]);
		
		new Coordinator(numberOfNodes, idSize,
				routingTableMaxDim, bucketsSize);
		// Creating first node of the network
		
		Node first = new Node(bits, tables, buckets);
		nodes.add(first);
		nodesRepo.put(first.getId(), first);
		
		for(int i=0; i<netDim-1; i++) {
			System.out.println("Inserting node "+i);
			
			Node bootstrap = randomNode();
			Node joining = new Node(bits, tables, buckets); // node joining the net
			
			while(nodesRepo.get(joining.getId()) != null)
				joining = new Node(bits, tables, buckets);
			
			nodes.add(joining);
			nodesRepo.put(joining.getId(), joining);
			
			joining.insertRT(new KadMessage(bootstrap.getId()));
						
			for(int j=0; j<tables; j++) {
				//System.out.println("Bucket "+j);
				//if(nodes.size()-1 == joining.neigs) break;
				
				for(int k = 0; k<buckets; k++) {
					//System.out.println("Searching "+k);
					//if(nodes.size()-1 == joining.neigs) break;
					NodeID searching = NodeID.randomId(joining.getId().getId(), (int) Math.pow(2, j));
					if(searching.equals(bootstrap.getId())) continue;
					joining.recursiveNodeSearch(searching, bootstrap.find_node(joining.getId(), searching));
				}
				
			}
			
		}
		for(Node n: nodes)
			n.printRT();
	}
}
