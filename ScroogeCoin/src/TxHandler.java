import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TxHandler {
	
	//Logger logger = Logger.getLogger("TxHandler");
	static class Logger {

		public void info(String format) {
			// TODO Auto-generated method stub
			
		}

		public void warning(String format) {
			// TODO Auto-generated method stub
			
		}
		
	}
	Logger logger = new Logger();  

	UTXOPool utxoPool;
	
	/**
	 * // FIXME this pool has items which are also in utxoPool, fix this  
	 */
	UTXOPool doubleCheckPool = new UTXOPool();
	
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
    	
    	if (utxoPool == null) {
    		this.utxoPool = new UTXOPool();
    	}
    	ScoogeUtility.addOutputsToPool(tx, utxoPool);
    	
    	TransactionValidator validator = new TransactionValidator(tx, utxoPool);
    	return /*validator.validateOutputs()
    			&*/ validator.allOutputsInPool()
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
    	if (utxoPool == null) {
    		utxoPool = new UTXOPool();
    	}
    	for (Transaction tx : possibleTxs) {
    		ScoogeUtility.addOutputsToPool(tx, utxoPool);
		}
    	int index = 0;
    	
    	for (Transaction tx : possibleTxs) {
    		UTXO utxo = new UTXO(tx.getHash(), index);
    		logger.info(String.format("Validating :: %s", ScoogeUtility.toString(tx)));
    		if (isNotDoubleSpend(tx) && isValidTx(tx)) {
				result.add(tx);
				utxoPool.addUTXO(utxo, tx.getOutput(index));
	    		logger.info(String.format("Validated :: %s", ScoogeUtility.toString(tx)));
			} else if (tx.getInputs() != null && tx.getInputs().size() != 0) {
				utxoPool.removeUTXO(utxo);
	    		logger.warning(String.format("Not Validated :: %s", ScoogeUtility.toString(tx)));
			}
		}
    	return result.toArray(new Transaction[]{});
    }
    
    /**
     * 
     * @param tx
     * @return If corresponding outputs of tx's inputs not in other outputs of tx's inputs
     */
    private boolean isNotDoubleSpend(Transaction tx) {
    	for (Transaction.Input input : tx.getInputs()) {
			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			if (doubleCheckPool.contains(utxo)) {
				return false;
			}
			doubleCheckPool.addUTXO(utxo, doubleCheckPool.getTxOutput(utxo));
		}
		return true;
	}

	public static class TransactionValidator {
    	
    	//private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    	private static Logger logger = new Logger();

    	private Transaction tx;
    	private UTXOPool utxo_pool;
    	
    	public TransactionValidator(Transaction tx, UTXOPool utxoPool) {
    		super();
    		this.tx = tx;
    		this.utxo_pool = utxoPool;
    	}
    	
    	public boolean validateOutputs () {
    		if (tx.getOutputs() == null || tx.getOutputs().size() == 0) {
    			return false;
    		}
    		return true;
    	}
    	
    	public boolean allOutputsInPool () {
        	for (int i = 0; i < tx.getOutputs().size(); i++) {
    			Transaction.Output output = tx.getOutput(i);
    			UTXO utxo = new UTXO(tx.getHash(), i);
    			if (!(utxo_pool.contains(utxo))) {
    				logger.warning(String.format("%s %s not in utxoPool",
    						ScoogeUtility.toString(tx), 
    						ScoogeUtility.toString(output)));
    				return false;
    			}
    			logger.info(String.format("%s %s in utxoPools",
    					ScoogeUtility.toString(tx), 
    					ScoogeUtility.toString(output)));
    		}
        	return true;
    	}

    	public boolean allSignaturesOfInputsValid() {
    		int index = 0;
    		for (Transaction.Input input : tx.getInputs()) {
                UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
    			Transaction.Output claimedOutput = utxo_pool.getTxOutput(utxo);
    			if (claimedOutput == null) {
    				return false;
    			}
    			PublicKey pubKey = claimedOutput.address;
    			byte [] message = tx.getRawDataToSign(index++);
    			byte [] signature = input.signature;
    			if (!Crypto.verifySignature(pubKey, message, signature)) {
    				logger.warning(String.format("%s %s not verified signature",
    						ScoogeUtility.toString(tx), 
    						ScoogeUtility.toString(input)));
    				return false;
    			}
    			logger.info(String.format("%s %s verified signature",
    					ScoogeUtility.toString(tx), 
    					ScoogeUtility.toString(input)));
    		}
    		return true;
    	}

    	public boolean allUtxoUnique() {
    		Set<UTXO> set = new HashSet<>();
    		for (Transaction.Input input : tx.getInputs()) {
    			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
				if (set.contains(utxo)) {
    				return false;
    			}
    			set.add(utxo);
    		}
    		return true;
    	}

    	public boolean allOutputsValueNonnegative() {
    		for (int i = 0; i < tx.getOutputs().size(); i++) {
    			Transaction.Output output = tx.getOutputs().get(i);
    			if (output.value <= 0.0) {
    				logger.warning(String.format("%s %s value is negative",
    						ScoogeUtility.toString(tx), 
    						ScoogeUtility.toString(output)));
    				return false;
    			}
    			logger.info(String.format("%s %s value is OK",
    					ScoogeUtility.toString(tx), 
    					ScoogeUtility.toString(output)));
    		}
    		return true;
    	}

    	public boolean sumOfInputsValuesGreaterThanOutputValues() {
    		double outputValues = 0.0,
    				inputValues = 0.0;
    		for (Transaction.Input in: tx.getInputs()) {
    			Transaction.Output output = utxo_pool.getTxOutput(new UTXO(in.prevTxHash, in.outputIndex));
    			if (output == null) {
    				continue;
    			}
    			inputValues += output.value;
    		}
    		for (Transaction.Output out : tx.getOutputs()){
    			outputValues += out.value;
    		}
    		boolean result = inputValues >= outputValues;
    		if (!result) {
    			logger.warning(String.format("%s [%s, %s] input values > outout values",
    					ScoogeUtility.toString(tx),
    					Double.toString(inputValues),
    					Double.toString(outputValues)
    					));
    		}
    		return result;
    	}
    	
    }
    
    public static class SignatureGenerator {

    	private static Map<Integer, Signature> map = new HashMap<>();
    	
    	public static byte [] getSignature (int user, byte [] dataToBeSigned, PrivateKey privateKey) {
    		try {
    			Signature sig;
    			if (!map.containsKey(user)) {
    				sig = Signature.getInstance("SHA256withRSA");
    				map.put(user, sig);
    			} else {
    				sig = map.get(user);
    			}
    			sig.initSign(privateKey);
    			sig.update(dataToBeSigned);
    			return sig.sign();
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
    	
    }


    public static class ScoogeUtility {

        public static String toString (Transaction tx) {
        	return String.format("Tx[## %s ##]", new String(tx.getHash()).hashCode());
        }

        public static String toString (Transaction.Output output) {
        	return String.format("Output[## %s ##]", output.value);
        }

    	public static Object toString(Transaction.Input input) {
        	return String.format("Input[## %s ##]", input.outputIndex);
    	}
    	
    	public static void addOutputsToPool (Transaction tx, UTXOPool utxoPool) {
        	for (int i = 0; i < tx.getOutputs().size(); i++) {
        		Transaction.Output output = tx.getOutput(i);
        		utxoPool.addUTXO(new UTXO(tx.getHash(), i), output);
    		}
        	for (int i = 0; i < tx.getInputs().size(); i++) {
				Transaction.Input input = tx.getInput(i);
				UTXO ut = new UTXO(input.prevTxHash, i);
				utxoPool.addUTXO(ut, utxoPool.getTxOutput(ut));
			}
    	}
    	
    	public static void signInputs (Transaction tx, KeyPair keyPair) {
    		int index = 0;
    		for (Transaction.Input input : tx.getInputs()) {
    			input.addSignature(TxHandler.SignatureGenerator.getSignature(1, tx.getRawDataToSign(index++), 
    					keyPair.getPrivate()));
    		}
    	}
    	
    }
    
}
