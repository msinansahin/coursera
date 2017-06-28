import java.security.NoSuchAlgorithmException;

public class ScroogeCoinMain {

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
				"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
//        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
//        System.out.println(keyPair.getPublic().toString());
//
//        keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
//        System.out.println(keyPair.getPublic().toString());
//        System.out.println(keyPair.getPrivate().getEncoded());

		System.out.println(String.format("%nValid"));
		callValid();
		
		System.out.println(String.format("%nInvalid"));
		
		callInvalid();
	}

	private static void callInvalid() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction[] txs = ScoogeCoinTestData.createSampleTranscationsWithInputInvalid(utxoPool);
        TxHandler txHandler = new TxHandler(utxoPool);
		txHandler.handleTxs(txs);	
	}

	private static void callValid() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction[] txs = ScoogeCoinTestData.createSampleTranscationsWithInputValid(utxoPool);
        TxHandler txHandler = new TxHandler(utxoPool);
		txHandler.handleTxs(txs);		
	}
}
