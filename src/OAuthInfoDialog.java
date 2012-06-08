import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Dialog that gets OAuth tokens from the user.
public class OAuthInfoDialog extends JDialog implements ActionListener {
	
	// Welcome message at the top of the dialog.
	private JLabel welcomeLabel;
	
	// Button that closes the dialog.
	private JButton signInButton;
	
	// The 4 fields that will store OAuth tokens.
	private FormField[] fields;
	// Indices of the keys in the fields array.
	// I was going to use an enum, but they don't work the way I want. Can't cast to ints.
	final int I_CONSUMER_KEY = 0,
	          I_CONSUMER_SECRET = 1,
	          I_ACCESS_TOKEN = 2,
	          I_ACCESS_TOKEN_SECRET = 3;
	
	/**
	 * Construct the OAuthInfoDialog by setting everything up. Duh.
	 * @param owner Owner of this dialog.
	 * @param title Title of this dialog.
	 * @param modal Modalness of the dialog.
	 */
	OAuthInfoDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		// Number of columns for each text field's preferred width.
		final int textFieldCols = 50;
		
		// Layout manager for the entire pane.
		SpringLayout OAuthPaneLayout = new SpringLayout();
		// Pane for the fields in the dialog. It'll be put into another JPanel that includes welcomeLabel and signInButton.
		JPanel tokenPane = new JPanel(OAuthPaneLayout);
		
		// Initialize the array of fields.
		fields = new FormField[4];
		
		for (int i = 0; i < fields.length; i++) {
			// Construct each FormField object.
			fields[i] = new FormField();
			// Construct each JTextField object in each FormField object.
			fields[i].field = new JTextField(textFieldCols);
		}
		
		// Set the JLables of each FormField object.
		fields[I_CONSUMER_KEY].label = new JLabel("Consumer Key: ");
		fields[I_CONSUMER_SECRET].label = new JLabel("Consumer Secret: ");
		fields[I_ACCESS_TOKEN].label = new JLabel("Access Token: ");
		fields[I_ACCESS_TOKEN_SECRET].label = new JLabel("Access Token Secret: ");
		
		// Add labels and fields to the dialog JPane.
		for (int i = 0; i < fields.length; i++) {
			tokenPane.add(fields[i].label);
			tokenPane.add(fields[i].field);
		}
		// Use a utility that I found on Oracle's website to set up SpringLayout for me.
		SpringUtilities.makeCompactGrid(tokenPane, 4, 2, 5, 5, 5, 5);
		
		// JPanel to contain all the dialog's contents. Contain ALL the contents!
		JPanel OAuthDialogPane = new JPanel();
		// Construct a BoxLayout with Comoponents stacked vertically.
		OAuthDialogPane.setLayout(new BoxLayout(OAuthDialogPane, BoxLayout.Y_AXIS));
		
		// Create the label at the top of the dialog.
		welcomeLabel = new JLabel("Welcome to World\u2019s Worst Client");
		// Set the x-alignment to the center of the dialog.
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Add the label to the dialog.
		OAuthDialogPane.add(welcomeLabel);
		
		// Set alignment & add the JPanel that contains OAuth token fields.
		tokenPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		OAuthDialogPane.add(tokenPane);
		
		// Create, set alignment of, and add the sign in button.
		signInButton = new JButton("Sign in to Twitter");
		// This class will be notified when somebody does something to the button.
		signInButton.addActionListener(this);
		signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		OAuthDialogPane.add(signInButton);
		
		// Add the JPanel that contains all the contents to the JDialog (this class).
		this.setContentPane(OAuthDialogPane);
	}
	
	public void actionPerformed(ActionEvent event) {
		// Who did the notification come from?
		Object source = event.getSource();
		// If it came from the signInButton
		if (source == signInButton) {
			// Close the dialog.
			setVisible(false);
		}
	}
	
	/**
	 * Get the OAuth tokens currently entered into this dialog.
	 * @return The OAuth tokens currently entered into this dialog.
	 */
	public OAuthTokens getOAuthTokens() {
		OAuthTokens tokens = new OAuthTokens();
		
		tokens.consumerKey = fields[I_CONSUMER_KEY].field.getText();
		tokens.consumerSecret = fields[I_CONSUMER_SECRET].field.getText();
		tokens.accessToken = fields[I_ACCESS_TOKEN].field.getText();
		tokens.accessTokenSecret = fields[I_ACCESS_TOKEN_SECRET].field.getText();
		
		return tokens;
	}
	
}
