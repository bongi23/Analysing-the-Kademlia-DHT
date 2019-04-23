import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Random;

public class NodeID implements Comparable<NodeID>, Serializable{
	private static final long serialVersionUID = 1L;
	
	private BitSet id;
	private static int size; // in bits
	
	public NodeID(int s) {
		size = s;
		
		try {
			this.id = randomId(size);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public NodeID(BitSet b) {
		this.id = b;
	}
	
	public int getSize() {
		return size;
	}
	
	public BitSet getId() {
		return this.id;
	}
	
	//We can assume distance between 2 ids fits in 32 bits
	public static int xorDistance(BitSet id1, BitSet id2) {
		BitSet res = new BitSet();
		res.or(id1);
		res.xor(id2);
		
		BigInteger d = new BigInteger(1, reverse(res.toByteArray()));
		
		return d.intValue(); 
	}
	
	private static byte[] reverse(byte[] bs) {
		byte[] res = new byte[bs.length];
		int j = 0;
		for(int i=bs.length-1; i >= 0; i--) {
			res[j] = bs[i];
			j++;
		}
		
		return res;
	}

	// assumption: min is a power of two
	// id is generated at a distance between min and the next power of two-1
	public static NodeID randomId(BitSet id, long minDistance) {
		int iMin = (int) (Math.log(minDistance)/Math.log(2));
	    
	    BitSet bsNewId = new BitSet(id.size());
	    
	    bsNewId.set(iMin, !id.get(iMin));
	    for(int i = iMin+1; i<size; i++)
	    	bsNewId.set(i, id.get(i));
	  
	    Random rand = new Random();
	    for(int i=0; i<iMin; i++)
	    	bsNewId.set(i,rand.nextBoolean());
	    
	    return new NodeID(bsNewId);
	}
	
	public static BitSet randomId(int size) throws NoSuchAlgorithmException {
		int d = size/8 > 0 ? size/8 : 1;
		byte[] tmp = new byte[d];
		
		Random generator = new Random();
		generator.nextBytes(tmp);
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(tmp);
		
		BitSet bs = BitSet.valueOf(encodedhash);
		bs.clear(size, bs.size());
		
		return bs;
	}
	
	@Override
	public boolean equals(Object id) {
		return this.id.equals(((NodeID)id).id);
	}
	
	@Override
	public int hashCode() {
		BigInteger a = new BigInteger(1, this.id.toByteArray());
		return a.intValue();
	}
	
	@Override
	public int compareTo(NodeID id) {
		BigInteger a = new BigInteger(1, this.id.toByteArray());
		BigInteger b = new BigInteger(1, id.id.toByteArray());
		return a.compareTo(b);
	}
}
