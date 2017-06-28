import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import my.SignatureGenerator;

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
		ScoogeUtility.addOutputsToPool(parentTx, utxoPool);
		
		Transaction tx1 = createTransaction(parentTx, 0);
		tx1.addOutput(10, keyPair.getPublic());
		tx1.addOutput(5, keyPair.getPublic());
		
		for (Transaction.Input input : tx1.getInputs()) {
			input.addSignature(SignatureGenerator.getSignature(1, input.prevTxHash, keyPair.getPrivate()));
		}
		result.add(tx1);
		
        //add all output to pool
		result.forEach(tx -> ScoogeUtility.addOutputsToPool(tx, utxoPool));
		
		return result.toArray(new Transaction[0]);
	}
	
	public static Transaction [] createSampleTranscationsWithInputInvalid(UTXOPool utxoPool) throws NoSuchAlgorithmException {
		
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		List<Transaction> result = new ArrayList<>();
		
		Transaction parentTx = createTransaction();
		parentTx.addOutput(14, keyPair.getPublic());
		ScoogeUtility.addOutputsToPool(parentTx, utxoPool);

		Transaction tx1 = createTransaction(parentTx, 0);
		
		tx1.addOutput(10, keyPair.getPublic());
		tx1.addOutput(6, keyPair.getPublic());
		
		for (Transaction.Input input : tx1.getInputs()) {
			input.addSignature(SignatureGenerator.getSignature(1, input.prevTxHash, keyPair.getPrivate()));
		}
		result.add(tx1);
		
        //add all output to pool
		result.forEach(tx -> ScoogeUtility.addOutputsToPool(tx, utxoPool));
		
		return result.toArray(new Transaction[0]);
	}
	
	/**
	 * creates random hash byte date string as transaction unique id,
	 * and adds input uses prevTx hash and output index
	 * 
	 * @param prevTx
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
