import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MaliciousNode implements Node {

	Set<Transaction> pendingTransactions;
	double p_graph, 
		p_malicious, 
		p_txDistribution;
	int numRounds;
	boolean[] followees;
    Map<Integer, Set<Transaction>> receivedTransactionsFromNodes = new HashMap<>();
	private int maliciousBehaviour = 0;
    
    public MaliciousNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
    	this.p_graph = p_graph;
    	this.p_malicious = p_malicious;
    	this.p_txDistribution = p_txDistribution;
    	this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
    	long time = new Date().getTime();
    	if (time % 2 == 0) {
    		return this.pendingTransactions;
    	}
   		return Collections.EMPTY_SET;
   	}

    public void receiveFromFollowees(Set<Candidate> candidates) {
    	CompliantNode.addCandidateToMap(receivedTransactionsFromNodes, candidates, this.followees);
    }
    
    
    
}
