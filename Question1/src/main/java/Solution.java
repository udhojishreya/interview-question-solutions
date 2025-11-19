import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Solution {

	/*
	 * Initializing static resources TYPE_0 - create request constant TYPE_1 - reset
	 * request constant logger - Logger log4j2 instance
	 */
	private static final int TYPE_0 = 0;
	private static final int TYPE_1 = 1;
	private static final Logger logger = LoggerFactory.getLogger(Solution.class);

	/*
	 * The function accepts following parameters: 1. INTEGER expiryLimit 2.
	 * 2D_INTEGER_ARRAY commands Returns the number of tokens active at the maximum
	 * time parameter from all commands. TokenManager managers token creation,
	 * update, activation & expiration.
	 */
	public static int numberOfTokens(int expiryLimit, List<List<Integer>> commands) {
		TokenManager manager = new TokenManager(expiryLimit);
		int maxTime = executeCommands(manager, commands);
		logger.debug("MaxTime=" + maxTime);
		Integer noOfTokens = manager.calculateActiveTokens(maxTime);
		manager.resetTokenManager();
		return noOfTokens;
	}

	/*
	 * This method performs validation & execution of commands. Calculates & returns
	 * the maximum time from all requests.
	 */
	private static int executeCommands(TokenManager manager, List<List<Integer>> commands) {
		logger.debug("ExpiryLimit=" + manager.getExpiryLimit());
		int maxTime = 0;
		for (List<Integer> cmd : commands) {
			// Validation to check that we are receiving exactly 3 parameters - type, id,
			// time
			if (cmd.size() != 3)
				logger.error("Invalid Input : Expected 3 Command Parameters. Actual=" + cmd);
			else {
				maxTime = executeCommand(manager, cmd, maxTime);
			}
		}
		return maxTime;
	}

	/*
	 * This method performs execution of a single command.
	 */
	private static int executeCommand(TokenManager manager, List<Integer> cmd, int maxTime) {
		/*
		 * Initialize local fields type represents create/reset request id represents
		 * token identifier time represents creation/reset time
		 */
		Integer type = cmd.get(0);
		Integer id = cmd.get(1);
		Integer time = cmd.get(2);

		// Validation check for input data in ascending order of Time units
		// Note - If a past time unit received, request is ignored.
		if (time < maxTime) {
			logger.error("Request time not in valid order!");
			return maxTime;
		}

		switch (type) {
		case TYPE_0 -> {
			manager.createToken(id, time);
		}
		case TYPE_1 -> {
			manager.resetToken(id, time);
		}
		// Invalid request type validation
		default -> {
			logger.error("Invalid Request Type Exception : Expected 0 for create token or 1 for reset token. Actual="
					+ type);
		}
		}
		// Since timeunit is processed in ascending order, no comparison needed.
		// maxTime is the time unit of the last command
		maxTime = time;
		return maxTime;
	}

}
