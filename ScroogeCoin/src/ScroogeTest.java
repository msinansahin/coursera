import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;


public class ScroogeTest {

	@Test
	public void testValid() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction[] txs = ScoogeCoinTestData.createSampleTranscationsWithInputValid(utxoPool);
        TxHandler txHandler = new TxHandler(utxoPool);
		Transaction [] result = txHandler.handleTxs(txs);
		assertEquals(1, result.length);
	}
	
	@Test
	public void testInvalid() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction[] txs = ScoogeCoinTestData.createSampleTranscationsWithInputInvalid(utxoPool);
        TxHandler txHandler = new TxHandler(utxoPool);
		Transaction [] result = txHandler.handleTxs(txs);
		assertEquals(0, result.length);
	}
	
	@Test
	public void testInvalidOutoutValueIsNegative() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction[] txs = ScoogeCoinTestData.createSampleTranscationsWithInputValid(utxoPool);
        txs[0].getOutput(0).value = -1.0;
        TxHandler txHandler = new TxHandler(utxoPool);
		Transaction [] result = txHandler.handleTxs(txs);
		assertEquals(0, result.length);
	}
	
	//@Test
	public void testContainingSignaturesOfIncorrectData() {
        UTXOPool utxoPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(utxoPool);
        Transaction tx = new Transaction();
		assertFalse(txHandler.isValidTx(tx));
	}
	
	@Test
	public void testContainingNonnegativeOutputValue() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction tx = ScoogeCoinTestData.createTransaction();
        KeyPair keypair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        tx.addOutput(1, keypair.getPublic());
        TxHandler.TransactionValidator validator = new TxHandler.TransactionValidator(tx, utxoPool);
		assertTrue(validator.allOutputsValueNonnegative());
	}
	
	@Test
	public void testContainingNegativeOutputValue() throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction tx = ScoogeCoinTestData.createTransaction();
        KeyPair keypair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        tx.addOutput(-1, keypair.getPublic());
        TxHandler.TransactionValidator validator = new TxHandler.TransactionValidator(tx, utxoPool);
		assertFalse(validator.allOutputsValueNonnegative());
	}
	
	@Test
	public void testUtxoOccursMultipleTimes () throws NoSuchAlgorithmException {
        UTXOPool utxoPool = new UTXOPool();
        Transaction prevTransaction = ScoogeCoinTestData.createTransaction();
        KeyPair keypair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        prevTransaction.addOutput(12, keypair.getPublic());
        Transaction tx = ScoogeCoinTestData.createTransaction();
        tx.addInput(prevTransaction.getHash(), 0);
        tx.addInput(prevTransaction.getHash(), 0);
        TxHandler.TransactionValidator validator = new TxHandler.TransactionValidator(tx, utxoPool);
		assertFalse(validator.allUtxoUnique());
	}
	
	/**
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void testDoubleSpendsExists () throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Transaction prevTransaction = ScoogeCoinTestData.createTransaction();
        prevTransaction.addOutput(6, keyPair.getPublic());
        
        Transaction tx1 = ScoogeCoinTestData.createTransaction(prevTransaction, 0);
        tx1.addOutput(5, keyPair.getPublic());
        
        Transaction tx2 = ScoogeCoinTestData.createTransaction(prevTransaction, 0);
        tx2.addOutput(5, keyPair.getPublic());
        
		TxHandler.ScoogeUtility.signInputs(tx1, keyPair);
		TxHandler.ScoogeUtility.signInputs(tx2, keyPair);
        
        UTXOPool utxoPool = new UTXOPool();
        
        TxHandler.ScoogeUtility.addOutputsToPool(prevTransaction, utxoPool);
        TxHandler.ScoogeUtility.addOutputsToPool(tx1, utxoPool);
        TxHandler.ScoogeUtility.addOutputsToPool(tx2, utxoPool);
        
		TxHandler txHandler = new TxHandler(utxoPool);
		
		TxHandler.TransactionValidator tx1Validator = new TxHandler.TransactionValidator(tx1, utxoPool);
				
		assertTrue(tx1Validator.allSignaturesOfInputsValid());
		assertTrue(tx1Validator.allOutputsInPool());
		assertTrue(tx1Validator.allOutputsValueNonnegative());
		assertTrue(tx1Validator.allUtxoUnique());
		assertTrue(tx1Validator.sumOfInputsValuesGreaterThanOutputValues());
		
		assertEquals(txHandler.handleTxs(new Transaction[] {tx1, tx2}).length, 1);
	}
	
	@Test
	public void testDoubleSpendsNotExists () throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Transaction prevTransaction = ScoogeCoinTestData.createTransaction();
        prevTransaction.addOutput(6, keyPair.getPublic());
        prevTransaction.addOutput(5, keyPair.getPublic());
        
        Transaction tx1 = ScoogeCoinTestData.createTransaction(prevTransaction, 0);
        tx1.addOutput(5, keyPair.getPublic());
        
        Transaction tx2 = ScoogeCoinTestData.createTransaction(prevTransaction, 1);
        tx2.addOutput(5, keyPair.getPublic());
        
		TxHandler.ScoogeUtility.signInputs(tx1, keyPair);
		TxHandler.ScoogeUtility.signInputs(tx2, keyPair);
        
        UTXOPool utxoPool = new UTXOPool();
        
        TxHandler.ScoogeUtility.addOutputsToPool(prevTransaction, utxoPool);
        TxHandler.ScoogeUtility.addOutputsToPool(tx1, utxoPool);
        TxHandler.ScoogeUtility.addOutputsToPool(tx2, utxoPool);
        
		TxHandler txHandler = new TxHandler(utxoPool);
		
		TxHandler.TransactionValidator tx1Validator = new TxHandler.TransactionValidator(tx1, utxoPool);
				
		assertTrue(tx1Validator.allSignaturesOfInputsValid());
		assertTrue(tx1Validator.allOutputsInPool());
		assertTrue(tx1Validator.allOutputsValueNonnegative());
		assertTrue(tx1Validator.allUtxoUnique());
		assertTrue(tx1Validator.sumOfInputsValuesGreaterThanOutputValues());
		
		assertEquals(txHandler.handleTxs(new Transaction[] {tx1, tx2}).length, 2);
	}

}
