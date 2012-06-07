
public class OAuthTokens {
	public String consumerKey, 
	              consumerSecret, 
	              accessToken, 
	              accessTokenSecret;
	
	public OAuthTokens() {
		consumerKey = consumerSecret = accessToken = accessTokenSecret = null;
	}
	
	public boolean isComplete() {
		return !(consumerKey == null || 
				 consumerSecret == null || 
				 accessToken == null || 
				 accessTokenSecret == null);
	}
}
