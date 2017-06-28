package my;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

public class SignatureGenerator {

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
