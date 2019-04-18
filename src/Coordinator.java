import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class Coordinator {
	
	private static ArrayList<Node> nodesList;
	private static int idSize; // bit of a node id
	private static int nodes;
	private static Random generator = new Random();
	
	//We can assume distance between 2 ids fits in 32 bits
	public static int xorDistance(byte[] id1, byte[] id2) {
		byte[] res = new byte[id1.length];
		for(int i=0; i<id1.length; i++) {
			res[i] = (byte) (id1[i]^id2[i]);
		}
		return (int)res[0];
	}
	
	// assumes bitnum > 8
	public static byte[] idGenerator(int bitnum) throws NoSuchAlgorithmException {
		byte[] tmp = new byte[bitnum/8];
		generator.nextBytes(tmp);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(tmp);
		return Arrays.copyOfRange(encodedhash, 0, (bitnum/8));
	}
	
	// assumption: min is a power of two
	// id is generated at a distance between min and the next power of two-1
	public static byte[] idAtDistance(byte[] id, long min) {
		int iMin = (int) (Math.log(min)/Math.log(2));
	    
	    BitSet bsId = BitSet.valueOf(id);
	    BitSet bsNewId = new BitSet(id.length*8);
	    
	    bsNewId.set(iMin, !bsId.get(iMin));
	    for(int i = iMin+1; i<id.length*8; i++) {
	    	bsNewId.set(i, bsId.get(i));
	    }
	  
	    Random rand = new Random();
	    for(int i=0; i<iMin; i++) {
	    	bsNewId.set(i,rand.nextBoolean());
	    }
	    
	    return bsNewId.toByteArray();
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		if(args.length != 2) {
			//errore, termina
		} 
		idSize = Integer.parseInt(args[0]);
		nodes = Integer.parseInt(args[1]);
		
		nodesList = new ArrayList<Node>(nodes);
		
		// generate id
		byte[] id1;
		byte[] id2;
		id1=idGenerator(idSize);
		while(true) {
			
			id2=Coordinator.idAtDistance(id1, 8);
			System.out.println(xorDistance(id1, id2));
		}
	}
}

