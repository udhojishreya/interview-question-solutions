import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class manages token creation, updation, reset, expiration.
 */
public class TokenManager {

	// Expiry limit
	private int expiryLimit;
	// Stores all valid tokens with key as id, value as expiry time
	private Map<Integer, Integer> tokenMap;
	// Stores expired token IDs
	private Set<Integer> expiredTokens;
	// Logger instance
	private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

	// Parameterized constructor - expiryLimit to be provided to instantiate
	public TokenManager(int expiryLimit) {
		this.expiryLimit = expiryLimit;
		tokenMap = new HashMap<Integer, Integer>();
		expiredTokens = new HashSet<Integer>();
	}

	// Returns expiry limit
	public int getExpiryLimit() {
		return expiryLimit;
	}

	// Creates or updates token in tokenMap
	private void updateToken(int tokenId, int time) {
		tokenMap.put(tokenId, (time + expiryLimit));
	}

	// Checks if token is active at currentTime
	private boolean isTokenActive(int tokenId, int currentTime) {
		if (tokenMap.containsKey(tokenId) && currentTime <= tokenMap.get(tokenId))
			return true;
		return false;
	}

	/*
	 * Responsible for cleaning up tokens. Checks all valid tokens present in
	 * tokenMap & gathers all expired tokens at currentTime into expiredTokens set.
	 * Removes all expired tokens from tokenMap & returns the count of active
	 * tokens. Time Complexity - O(n) : can use a different data structure for
	 * improvements.
	 */
	public int calculateActiveTokens(int currentTime) {
		Iterator<Integer> itr = tokenMap.keySet().iterator();
		while (itr.hasNext()) {
			int token = itr.next();
			if (isTokenActive(token, currentTime)) {
				logger.debug("Token:" + token + " is active");
			} else {
				expiredTokens.add(token);
				itr.remove();
				logger.debug("Token:" + token + " expired");
			}
		}
		return tokenMap.size();
	}

	/*
	 * Reset token requests - Validation for non-existent tokens - token reset if
	 * token has not expired
	 */
	public boolean resetToken(int id, int time) {
		if (isTokenActive(id, time)) {
			updateToken(id, time);
			logger.info("Token=" + id + " reset at time=" + time);
			return true;
		} else {
			logger.info("Cannot reset token=" + id + " : Token expired/does not exist.");
			return false;
		}
	}

	/*
	 * Create token requests Validation for token already exists/expired. New token
	 * created if validation successful.
	 */
	public boolean createToken(int id, int time) {
		if (tokenExists(id)) {
			logger.info("Cannot create token=" + id + " : Token ID already exists/expired.");
			return false;
		} else {
			updateToken(id, time);
			logger.info("Token=" + id + " created at time=" + time);
			return true;
		}

	}

	// Checks if token already exists (active or expired)
	private boolean tokenExists(int tokenId) {
		return tokenMap.containsKey(tokenId) || expiredTokens.contains(tokenId);
	}

	// Cleans the tokens before the program/call ends
	public void resetTokenManager() {
		tokenMap.clear();
		expiredTokens.clear();
	}
}
