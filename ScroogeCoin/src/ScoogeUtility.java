public class ScoogeUtility {

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
	}
	

}
