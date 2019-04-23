import java.util.HashSet;

public interface KademliaAPI {
	
    public HashSet<KadMessage> find_node(NodeID sender, NodeID request);
}