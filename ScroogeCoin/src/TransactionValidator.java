
import java.security.PublicKey;
import java.util.logging.Logger;


public class TransactionValidator {
	
	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	private Transaction tx;
	private UTXOPool utxoPool;
	
	public TransactionValidator(Transaction tx, UTXOPool utxoPool) {
		super();
		this.tx = tx;
		this.utxoPool = utxoPool;
	}
	
	public boolean validateOutputs () {
		return true;
	}
	
	public boolean allOutputsInPool () {
    	for (int i = 0; i < tx.getOutputs().size(); i++) {
			Transaction.Output output = tx.getOutput(i);
			UTXO utxo = new UTXO(tx.getHash(), i);
			if (!(utxoPool.contains(utxo))) {
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
		for (Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			Transaction.Output claimedOutput = utxoPool.getTxOutput(utxo);
			PublicKey pubKey = claimedOutput.address;
			byte [] message = input.prevTxHash;
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
		// TODO Auto-generated method stub
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
			inputValues += utxoPool.getTxOutput(new UTXO(in.prevTxHash, in.outputIndex)).value;
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
