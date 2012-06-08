
public class OAuthTokens {
	public String consumerKey, 
	              consumerSecret, 
	              accessToken, 
	              accessTokenSecret;
	
	public OAuthTokens() {
		// Set all of them to null so that client classes can write meaningful data to them.
		consumerKey = consumerSecret = accessToken = accessTokenSecret = null;
	}
	
	/**
	 * Make sure all the OAuth tokens are there.
	 * @return true if all the tokens are non-null. false otherwise.
	 */
	public boolean isComplete() {
		return !(consumerKey == null || 
				 consumerSecret == null || 
				 accessToken == null || 
				 accessTokenSecret == null);
	}
}
