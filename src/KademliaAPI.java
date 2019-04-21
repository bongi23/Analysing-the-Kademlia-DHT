import java.util.HashSet;

public interface KademliaAPI {
	
    public HashSet<NodeID> find_node(NodeID sender, NodeID request);
}