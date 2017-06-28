import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoogeCoinTestData {

	/**
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static Transaction [] createSampleTranscationsWithInputValid(UTXOPool utxoPool) throws NoSuchAlgorithmException {
		
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		List<Transaction> result = new ArrayList<>();
		
		Transaction parentTx = createTransaction();
		parentTx.addOutput(16, keyPair.getPublic());
		TxHandler.ScoogeUtility.addOutputsToPool(parentTx, utxoPool);
		
		Transaction tx1 = createTransaction(parentTx, 0);
		tx1.addOutput(10, keyPair.getPublic());
		tx1.addOutput(5, keyPair.getPublic());

		TxHandler.ScoogeUtility.signInputs(tx1, keyPair);
		result.add(tx1);
		
        //add all output to pool
		result.forEach(tx -> TxHandler.ScoogeUtility.addOutputsToPool(tx, utxoPool));
		
		return result.toArray(new Transaction[0]);
	}
	
	public static Transaction [] createSampleTranscationsWithInputInvalid(UTXOPool utxoPool) throws NoSuchAlgorithmException {
		
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		List<Transaction> result = new ArrayList<>();
		
		Transaction parentTx = createTransaction();
		parentTx.addOutput(14, keyPair.getPublic());
		TxHandler.ScoogeUtility.addOutputsToPool(parentTx, utxoPool);

		Transaction tx1 = createTransaction(parentTx, 0);
		
		tx1.addOutput(10, keyPair.getPublic());
		tx1.addOutput(6, keyPair.getPublic());
		
		TxHandler.ScoogeUtility.signInputs(tx1, keyPair);
		result.add(tx1);
		
        //add all output to pool
		result.forEach(tx -> TxHandler.ScoogeUtility.addOutputsToPool(tx, utxoPool));
		
		return result.toArray(new Transaction[0]);
	}
	
	/**
	 * creates random hash byte date string as transaction unique id,
	 * and adds input uses prevTx hash and output index
	 * 
	 * @param prevTx transaction of corresponding outputs of inputs 
	 * @param outputIndex
	 * @return
	 */
	public static Transaction createTransaction (Transaction prevTx, int outputIndex) {
		byte [] hash = UUID.randomUUID().toString().getBytes();
		return new TransactionBuilder()
			.setHash(hash)
			.addInput(prevTx.getHash(), outputIndex)
			.build();
	}

	/**
	 * creates random hash byte date string as transaction unique id
	 * 
	 * @return
	 */
	public static Transaction createTransaction () {
		byte [] hash = UUID.randomUUID().toString().getBytes();
		
		return new TransactionBuilder()
			.setHash(hash)
			.build();
		
	}
	
}
