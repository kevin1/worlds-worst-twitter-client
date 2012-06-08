import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class TweetDraftsFrontend extends JFrame implements ActionListener, ListSelectionListener {
	
	public static void main(String[] args) {
		new TweetDraftsFrontend();
	}
	
	// Make a backend to interface with. Haha. Interface. Because this is a frontend.
	TweetDraftsBackend td;
	// JList to store the list of drafts.
	JList tweetList; // TODO: scrolling http://java.sun.com/products/jfc/tsc/tech_topics/jlist_1/jlist.html
	// Index of the current tweet selected in the JList tweetList.
	int i_tweetListSelected = -1;
	// 
	DefaultListModel tweetListModel;
	
	// A text field for composing / editing the current tweet selected in tweetList.
	JTextArea tweetField;
	// Send the selected tweet.
	JButton sendTweet;
	// Text to put on the JButton sendTweet.
	final String sendTweetText = "Tweet";
	// Contain the JTextArea tweetField and the JButton sendTweet.
	JPanel tweetEditor;
	// Contain the JList tweetList and the JPanel tweetEditor.
	JSplitPane tweetSplit;
	// Button for making a new tweet.
	JButton newTweet;
	// Contain all the Components for this window: JButton newTweet and the JSplitPane tweetSplit.
	JPanel pane;
	
	public TweetDraftsFrontend() {
		super();
		
		Debug.isDebug = true;
		
		// Construct the backend.
		td = new TweetDraftsBackend();
		// Try telling the backend to load data from configuration data.
		if (td.loadDataFromDisk()) {
			// Config loading was successful. Do nothing.
		}
		else {
			// Incomplete config data were found. Prompt the user for data.
			// Dialog to accept data from the user.
			OAuthInfoDialog d = new OAuthInfoDialog(this, "Set OAuth Tokens", true);
			// Calculate the size of the dialog.
			d.pack();
			// Show the dialog to the user.
			// This method blocks further execution of the program until dialog closed.
			d.setVisible(true);
			// Grab data from the dialog.
			OAuthTokens tokens = d.getOAuthTokens();
			// Check token validity.
			if (tokens == null) {
				// TODO: exit
			}
			else {
				// Save the tokens.
				td.setOAuthTokens(tokens);
				// This is the first run of the application. Store a welcome tweet.
				td.storeStatus("Welcome to World\u2019s Worst Client");
			}
		}
		
		// Set a title for the main window.
		setTitle("World\u2019s Worst Twitter Client");
		// Set the location for the main window.
		setBounds(100, 100, 600, 300); // x location, y location, width, height
		// Terminate the application when the window closes.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Use the BorderLayout layout manager for the entire window's JPanel.
		pane = new JPanel(new BorderLayout());
		// Add the JPanel to the window.
		this.add(pane);
		
		// Use a BorderLayout layout manager for the tweet editor JPanel.
		tweetEditor = new JPanel(new BorderLayout());
		
		// Construct a text field for editing tweets.
		tweetField = new JTextArea();
		// Add the tweetField to the tweetEditor. Take up as much space as we can.
		tweetEditor.add(tweetField, BorderLayout.CENTER);
		
		// New JButton for sending tweets with the text sendTweetText.
		sendTweet = new JButton(sendTweetText);
		// When someone does things to the sendTweet button, this class will be notified.
		sendTweet.addActionListener(this);
		// Add sendTweet button to the bottom of tweetEditor.
		tweetEditor.add(sendTweet, BorderLayout.PAGE_END);
		
		// New JButton for making new tweets with text New Tweet.
		newTweet = new JButton("New Tweet");
		// When someone does things to the newTweet button, this class will be notified.
		newTweet.addActionListener(this);
		// Set a keyboard shortcut for the button.
		newTweet.setMnemonic('N'); // TODO: doesn't work the way I want. This sets alt-n, not cmd/ctrl-n.
		// Add the newTweet button to the top of the JPanel that contains all the contents.
		pane.add(newTweet, BorderLayout.PAGE_START);
		
		// Construct a new JList.
		tweetList = new JList();
		// Only allow the user to select one list item at a time.
		tweetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// List will stack items vertically.
		tweetList.setLayoutOrientation(JList.VERTICAL);
		// When someone does things to the tweetList list, this class will be notified.
		tweetList.addListSelectionListener(this);
		
		// New JSplitPane:
		tweetSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, // side-by-side layout 
				true, // recalculate the layout continuously when the user drags the split to resize
				tweetList, // component on the left 
				tweetEditor //component on the right
		);
		// Don't let the user resize the split pane.
		tweetSplit.setOneTouchExpandable(false);
		// 200px divider location from the left.
		tweetSplit.setDividerLocation(200);
		// tweetSplit will take up as much space in the center as it can.
		pane.add(tweetSplit, BorderLayout.CENTER);
		
		// Refresh the UI from the backend before showing it.
		reloadData();
		// Show the UI to the user.
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		// Where did the notification come from?
		Object source = event.getSource();
		// Came from newTweet button.
		if (source == newTweet) {
			// If a tweet was selected before the user clicked newTweet, save it.
			if (i_tweetListSelected >= 0) {
				saveCurrentTweet();
			}
			// Make a new tweet in the backend.
			td.storeStatus("d kevinchen test");
			// Refresh the UI from backend's data to reflect the new tweet.
			reloadData();
			// Select the new tweet, which will always be at the end of the list.
			tweetList.setSelectedIndex(td.getNumStoredStatuses() - 1);
		}
		else if (source == sendTweet) {
			// TODO: this is supposed to disable the button while posting. It doens't work.
			sendTweet.setText("Posting tweet\u2026");
			sendTweet.setEnabled(false);
			// Save the current tweet before sending, in case the user changed it.
			saveCurrentTweet();
			// Tell backend to store it.
			td.postStoredStatus(i_tweetListSelected);
			// Sending is done. Restore the button's original text.
			sendTweet.setText(sendTweetText);
			// Refresh the UI from backend's data.
			reloadData();
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		Debug.println("valueChanged()");
		// Where did the notification come from?
		JList list = (JList)e.getSource();
		
		// Previous index and new index were the same.
		if (i_tweetListSelected == list.getMinSelectionIndex()) {
			// Do nothing.
			return;
		}
		// Something was previously selected in the list.
		if (i_tweetListSelected >= 0) {
			// Save the tweet in case the user edited it.
			saveCurrentTweet();
		}
		
		// Negative selection index returned means the selection was empty.
		if (list.getMinSelectionIndex() < 0) {
			Debug.println("Selection was empty");
			i_tweetListSelected = -1;
		}
		// Selection was not empty.
		else {
			// Update this class's internal data about which index is selected.
			// only care about the minimum because selection mode only lets us select one at a time
			i_tweetListSelected = list.getMinSelectionIndex();
			Debug.println("list: selected index " + i_tweetListSelected);
		}
		
		// Refresh the UI from backend's data.
		reloadData();
	}
	
	/**
	 * Save the currently selected tweet to the backend.
	 */
	public void saveCurrentTweet() {
		td.setStoredStatus(i_tweetListSelected, tweetField.getText());
	}
	
	/**
	 * Reload the data from the backend.
	 */
	public void reloadData() {
		int numStatuses = td.getNumStoredStatuses();
		// If currently selected an index bigger than the biggest index.
		if (i_tweetListSelected >= numStatuses) {
			// Select the last element in the list.
			i_tweetListSelected = numStatuses - 1;
		}
		// If something is currently selected.
		if (i_tweetListSelected >= 0) {
			// Set the text field's contents to the currently selected tweet.
			tweetField.setText(td.getStoredStatus(i_tweetListSelected));
			// A tweet is selected. OK to let the user send this tweet.
			sendTweet.setEnabled(true);
		}
		// Nothing is currently selected.
		else {
			// Set the text field to an empty string.
			// TODO: should disable text field to prevent data loss if the user enters something.
			tweetField.setText("");
			// No tweet is selected. Don't let the user send tweet.
			sendTweet.setEnabled(false);
		}
		// Recalculate the layout
		tweetEditor.validate();
		
		// TODO: remove repeated code from constructor
		// Remove the current tweetList.
		tweetSplit.remove(tweetList);
		// Make a new tweetList from the backend's data.
		tweetList = new JList(td.getStoredStatusesPreview(25));
		// Set up all sorts of crazy options.
		tweetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tweetList.setLayoutOrientation(JList.VERTICAL);
		tweetList.addListSelectionListener(this);
		// Select the appropriate tweet in the list.
		tweetList.setSelectedIndex(i_tweetListSelected);
		// Add the new tweetList to the tweetSplit.
		tweetSplit.add(tweetList, 0);
		// More crazy options.
		tweetSplit.setDividerLocation(200);
	}
	
}
