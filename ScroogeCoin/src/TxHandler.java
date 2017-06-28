import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TxHandler {
	
	Logger logger = Logger.getLogger("TxHandler");

	UTXOPool utxoPool;
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
    	this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    	TransactionValidator validator = new TransactionValidator(tx, utxoPool);
    	return validator.allOutputsInPool()
    			& validator.allSignaturesOfInputsValid()
    			& validator.allUtxoUnique()
    			& validator.allOutputsValueNonnegative()
    			& validator.sumOfInputsValuesGreaterThanOutputValues();
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	List<Transaction> result = new ArrayList<>();
    	int index = 0;
    	for (Transaction tx : possibleTxs) {
    		UTXO utxo = new UTXO(tx.getHash(), index);
    		logger.info(String.format("Validating :: %s", ScoogeUtility.toString(tx)));
			if (isValidTx(tx)) {
				result.add(tx);
				utxoPool.addUTXO(utxo, tx.getOutput(index));
	    		logger.info(String.format("Validated :: %s", ScoogeUtility.toString(tx)));
			} else {
				utxoPool.removeUTXO(utxo);
	    		logger.warning(String.format("Not Validated :: %s", ScoogeUtility.toString(tx)));
			}
		}
    	return result.toArray(new Transaction[]{});
    }
    
}
