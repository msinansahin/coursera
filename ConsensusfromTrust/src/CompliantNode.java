import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

	Set<Transaction> pendingTransactions;
	double p_graph, 
		p_malicious, 
		p_txDistribution;
	int numRounds;
	boolean[] followees;
    Map<Integer, Set<Transaction>> receivedTransactionsFromNodes = new HashMap<>();
	int roundCounter = 0;
    
    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    	this.p_graph = p_graph;
    	this.p_malicious = p_malicious;
    	this.p_txDistribution = p_txDistribution;
    	this.numRounds = numRounds;
    }

    @Override
	public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
    	this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    	this.pendingTransactions = pendingTransactions == null ? new HashSet<>() : pendingTransactions;
    }

    @Override
	public Set<Transaction> sendToFollowers() {
    	roundCounter++;
   		return arrangeTransactionsToBeSentToFollowers(roundCounter == numRounds);
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
    	addCandidateToMap(receivedTransactionsFromNodes, candidates, this.followees);
    }
    
    private Set<Integer> malNodes = new HashSet<>();
    
    public static void addCandidateToMap (Map<Integer, Set<Transaction>> map, Set<Candidate> candidates, boolean [] followees) {
    	for (Candidate candidate : candidates) {
    		if (!followees[candidate.sender]) {
    			return;
    		}
			if (map.containsKey(candidate.sender)) {
				map.get(candidate.sender).add(candidate.tx);
			} else {
				map.put(candidate.sender, new HashSet<>(Arrays.asList(candidate.tx)));
			}
		}
    }
    
    private Set<Transaction> arrangeTransactionsToBeSentToFollowers(boolean finalRound) {
    	if (finalRound) {
    		
    	}
    	
    	Set<Transaction> result = new HashSet<>(this.pendingTransactions);
    	for (Entry<Integer, Set<Transaction>> entry : receivedTransactionsFromNodes.entrySet()) {
			result.addAll(entry.getValue());
		}
    	return result;
    }
}
