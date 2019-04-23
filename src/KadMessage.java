import java.util.HashSet;

public class KadMessage implements Comparable<KadMessage> {
	NodeID reply;
	HashSet<NodeID> traversed;
	
	public KadMessage(NodeID reply) {
		this.reply = reply;
		this.traversed = null;
	}


	public void addTraversed(NodeID id) {
		if(this.traversed == null)
			this.traversed = new HashSet<NodeID>();
		this.traversed.add(id);
		
	}

	
	public NodeID getReply() {
		return this.reply;
	}

	public HashSet<NodeID> getTraversed() {
		if(this.traversed == null)
			this.traversed = new HashSet<NodeID>();
		return this.traversed;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.reply.equals(((KadMessage) o).getReply());
	}
	
	@Override
	public int hashCode() {
		return this.reply.hashCode();
	}


	@Override
	public int compareTo(KadMessage o) {
		return this.reply.compareTo(o.reply);
	}

}
