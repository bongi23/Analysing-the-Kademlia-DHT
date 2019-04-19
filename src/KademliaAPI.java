import java.util.HashSet;
import java.util.List;

public interface KademliaAPI {

    public HashSet<NodeID> find_node(NodeID sender, NodeID request);
}