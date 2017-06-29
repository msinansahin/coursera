import java.util.Collections;
import java.util.Set;

public class MalDoNothing implements Node {

    public MalDoNothing(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    }

    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
    	return Collections.EMPTY_SET;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
    }
}
