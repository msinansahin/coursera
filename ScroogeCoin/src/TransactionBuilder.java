
public class TransactionBuilder {

	private Transaction tx;
	
	public TransactionBuilder() {
		tx = new Transaction();
	}
	
	public TransactionBuilder(Transaction transaction) {
		tx = new Transaction(transaction);
	}
	
    public TransactionBuilder setHash(byte[] h) {
        this.tx.setHash(h);
        return this;
    }
    
    public Transaction build () {
    	return tx;
    }

	public TransactionBuilder addInput(byte[] hash, int outputIndex) {
		this.tx.addInput(hash, outputIndex);
		return this;
	}

	public TransactionBuilder addSignature(byte[] signature) {
		tx.addSignature(signature, tx.getInputs().size() - 1);
		return this;
	}

	class InputBuilder {
		
		private TransactionBuilder builder;
		
		public InputBuilder(TransactionBuilder builder) {
			super();
			this.builder = builder;
		}

		public TransactionBuilder addSignature(byte[] signature) {
			tx.addSignature(signature, tx.getInputs().size() - 1);
			return builder;
		}
	}
}
