import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OAuthInfoDialog extends JDialog implements ActionListener {
	
	private JPanel p;
	
	private JLabel welcomeLabel;
	
	private JButton signInButton;
	
	private FormField[] fields;
	final int I_CONSUMER_KEY = 0,
	          I_CONSUMER_SECRET = 1,
	          I_ACCESS_TOKEN = 2,
	          I_ACCESS_TOKEN_SECRET = 3;
	
	OAuthInfoDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		final int textFieldCols = 50;
		
		SpringLayout OAuthPaneLayout = new SpringLayout();
		JPanel tokenPane = new JPanel(OAuthPaneLayout);
		
		// Add content
		fields = new FormField[4];
		
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new FormField();
			fields[i].field = new JTextField(textFieldCols);
		}
		
		fields[I_CONSUMER_KEY].label = new JLabel("Consumer Key: ");
		fields[I_CONSUMER_SECRET].label = new JLabel("Consumer Secret: ");
		fields[I_ACCESS_TOKEN].label = new JLabel("Access Token: ");
		fields[I_ACCESS_TOKEN_SECRET].label = new JLabel("Access Token Secret: ");
		
		for (int i = 0; i < fields.length; i++) {
			tokenPane.add(fields[i].label);
			tokenPane.add(fields[i].field);
		}
		
		SpringUtilities.makeCompactGrid(tokenPane, 4, 2, 5, 5, 5, 5);
		
		JPanel OAuthDialogPane = new JPanel();
		OAuthDialogPane.setLayout(new BoxLayout(OAuthDialogPane, BoxLayout.Y_AXIS));
		
		welcomeLabel = new JLabel("Welcome to World\u2019s Worst Client");
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		OAuthDialogPane.add(welcomeLabel);
		
		tokenPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		OAuthDialogPane.add(tokenPane);
		
		signInButton = new JButton("Sign in to Twitter");
		signInButton.addActionListener(this);
		signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		OAuthDialogPane.add(signInButton);
		
		this.setContentPane(OAuthDialogPane);
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == signInButton) {
			//captureOAuthTokens();
			setVisible(false);
		}
	}
	
	public OAuthTokens getOAuthTokens() {
		OAuthTokens tokens = new OAuthTokens();
		
		tokens.consumerKey = fields[I_CONSUMER_KEY].field.getText();
		tokens.consumerSecret = fields[I_CONSUMER_SECRET].field.getText();
		tokens.accessToken = fields[I_ACCESS_TOKEN].field.getText();
		tokens.accessTokenSecret = fields[I_ACCESS_TOKEN_SECRET].field.getText();
		
		return tokens;
	}
	
}
