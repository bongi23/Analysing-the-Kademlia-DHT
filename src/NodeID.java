import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class NodeID implements Comparable<NodeID>, Serializable{
	private static final long serialVersionUID = 1L;
	
	private byte[] id;
	private int size; // in bits
	
	public NodeID(int size) {
		this.size = size;
		
		try {
			this.id = randomId(size);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public NodeID(byte[] b) {
		this.id = b;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public byte[] getId() {
		return this.id.clone();
	}
	
	//We can assume distance between 2 ids fits in 32 bits
	public int xorDistance(byte[] id2) {
		byte[] res = new byte[this.id.length];
		for(int i=0; i<this.id.length; i++) {
			res[i] = (byte) (this.id[i]^id2[i]);
		}
		return (int)res[0];
	}
	
	//We can assume distance between 2 ids fits in 32 bits
	public static int xorDistance(byte[] id1, byte[] id2) {
		byte[] res = new byte[id1.length];
		for(int i=0; i<id1.length; i++) {
			res[i] = (byte) (id1[i]^id2[i]);
		}
		return (int)res[0];
	}
	
	// assumption: min is a power of two
	// id is generated at a distance between min and the next power of two-1
	public static NodeID randomId(byte[] id, long minDistance) {
		int iMin = (int) (Math.log(minDistance)/Math.log(2));
	    
	    BitSet bsId = BitSet.valueOf(id);
	    BitSet bsNewId = new BitSet(id.length*8);
	    
	    bsNewId.set(iMin, !bsId.get(iMin));
	    for(int i = iMin+1; i<id.length*8; i++)
	    	bsNewId.set(i, bsId.get(i));
	  
	    Random rand = new Random();
	    for(int i=0; i<iMin; i++)
	    	bsNewId.set(i,rand.nextBoolean());
	    
	    return new NodeID(bsNewId.toByteArray());
	}
	
	public static byte[] randomId(int size) throws NoSuchAlgorithmException {
		byte[] tmp = new byte[size/8];
		
		Random generator = new Random();
		generator.nextBytes(tmp);
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(tmp);
		
		return Arrays.copyOfRange(encodedhash, 0, (size/8));
	}
	
	public boolean equals(NodeID id) {
		return this.xorDistance(id.getId()) == 0;
	}

	@Override
	public int compareTo(NodeID id) {
		return this.xorDistance(id.getId());
	}
}
