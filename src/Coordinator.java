import java.io.ObjectInputStream.GetField;
import java.rmi.RemoteException;
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
		tables = tablesize; //size of routing tables
		buckets = bucketnum;
		nodesRepo = new HashMap<NodeID, Node>();
	}
	
	public static Node randomNode() {
		Random r = new Random();
		return nodes.get(r.nextInt(nodes.size()));
	}
	
	public static void main(String[] args) throws RemoteException {
		if(args.length != 4) {
			System.err.println("Errore argomenti");
			return;
		}
		Coordinator c = new Coordinator(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		// Creating first node of the network
		Node first = new Node(bits, tables, buckets);
		nodes.add(first);
		nodesRepo.put(first.getId(), first);
		
		for(int i=0; i<netDim-1; i++) {
			Node n = new Node(bits, tables, buckets);
			nodes.add(n);
			nodesRepo.put(n.getId(), n);
			
			Node bootstrap = randomNode();
			Random r = new Random();
			
			for(int j=0; j<tables; j++) {
				int idNum = r.nextInt(buckets)+1;
				
				for(int k = 0; k<idNum; k++) {
					NodeID id = NodeID.randomId(n.getId().getId(), (int) Math.pow(2, i));
					n.recursiveNodeSearch(id, bootstrap.find_node(n.getId(), id));
				}
				
			}
			
			
		}
		// Per n-1 volte
			// genera un nodo p casualmente
			// scegli casualmente un bootstrap node b
			// genera un certo numero di ID associati ai range di distanza dei bucket della routing table di p
				// per ogni ID chiama la funzione b.find_node()
				// aggiorna la routing table di p
	}
}
