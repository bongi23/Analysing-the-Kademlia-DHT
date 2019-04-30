import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	public static void network2csv(String fileName) throws IOException {
			    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			    writer.write("nodo1,nodo2");
			    writer.newLine();
			    for(Node n : nodes) {
					HashMap<Integer, ArrayList<KadMessage>> routingTable = n.getRoutingTable();
					for(ArrayList<KadMessage> buckets : routingTable.values()) {
						for(KadMessage msg : buckets) {
							writer.write(n.getId()+","+msg.getReply());
							writer.newLine();
						}
					}
				}
			    writer.close();
			}
	
	public static void main(String[] args) throws IOException {
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
	
		Node first = new Node(bits, buckets);
		nodes.add(first);
		nodesRepo.put(first.getId(), first);
		for(int i=0; i<netDim-1; i++) {
			System.out.println("Inserting node "+i);
			
			Node bootstrap = randomNode();
			Node joining = new Node(bits, buckets); // node joining the net
			
			while(nodesRepo.get(joining.getId()) != null)
				joining = new Node(bits, buckets);
			
			nodes.add(joining);
			nodesRepo.put(joining.getId(), joining);
			
			joining.insertRT(new KadMessage(bootstrap.getId()));
			
			for(int j=0; j<tables; j++) {					
				for(int k = 0; k<buckets; k++) {
					NodeID searching = NodeID.randomId(joining.getId().getId(), (int) Math.pow(2, j));
					if(searching.equals(bootstrap.getId())) continue;
					joining.recursiveNodeSearch(searching, bootstrap.find_node(joining.getId(), searching),
							new HashSet<KadMessage>());
				}
				
			}
			
		}
		network2csv("./"+numberOfNodes+"_"+(idSize)+"_"+bucketsSize+"improved.csv");
	}
}
