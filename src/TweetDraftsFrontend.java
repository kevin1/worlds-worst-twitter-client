// -Dawt.useSystemAAFontSettings=on -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class TweetDraftsFrontend extends JFrame implements ActionListener, ListSelectionListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TweetDraftsFrontend();
	}
	
	static TweetDraftsBackend td;
	// dynamic JList http://java.sun.com/products/jfc/tsc/tech_topics/jlist_1/jlist.html
	JList tweetList; // TODO: scrolling http://java.sun.com/products/jfc/tsc/tech_topics/jlist_1/jlist.html
	int i_tweetListSelected = -1;
	DefaultListModel tweetListModel;
	
	JTextArea tweetField;
	JButton sendTweet;
	final String sendTweetText = "Tweet";
	// Layout managers: http://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
	JPanel tweetEditor;
	JSplitPane tweetSplit;
	JPanel pane;
	JButton newTweet;
	
	public TweetDraftsFrontend() {
		super();
		
		Debug.isDebug = true;
		
		td = new TweetDraftsBackend();
		if (td.loadDataFromDisk()) {
			// config autoloaded
		}
		else {
			/*JDialog getOAuth = new JDialog(this, "Welcome to World\u2019s Worst Client", true);
			getOAuth.add(new JTextArea());
			getOAuth.setLayout(new BorderLayout()); // TODO: change*/
			//OAuthInfoDialog getOAuth = new OAuthInfoDialog(this, "Welcome to World\u2019s Worst Client", true);
			//getOAuth.pack();
			// TODO: prompt for OAuth tokens
			OAuthInfoDialog d = new OAuthInfoDialog(this, "Set OAuth Tokens", true);
			d.pack();
			d.setVisible(true); // blocks until dialog closed
			// TODO: get data
			OAuthTokens tokens = d.getOAuthTokens();
			if (tokens == null) {
				// TODO: exit
			}
			else {
				td.setOAuthTokens(tokens);
				td.storeStatus("Welcome to World\u2019s Worst Client");
			}
		}
		
		setTitle("World\u2019s Worst Twitter Client");
		setBounds(100, 100, 600, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container con = this.getContentPane();
		pane = new JPanel(new BorderLayout());
		con.add(pane);
		
		tweetEditor = new JPanel(new BorderLayout());
		
		tweetField = new JTextArea(/*4, 50*/);
		tweetEditor.add(tweetField, BorderLayout.CENTER);
		
		sendTweet = new JButton(sendTweetText);
		sendTweet.addActionListener(this);
		tweetEditor.add(sendTweet, BorderLayout.PAGE_END);
		
		newTweet = new JButton("New Tweet");
		newTweet.addActionListener(this);
		newTweet.setMnemonic('N'); // TODO: doesn't work.
		pane.add(newTweet, BorderLayout.PAGE_START);
		
		//tweetListModel = new DefaultListModel();
		//tweetList = new JList(tweetListModel);
		tweetList = new JList();
		tweetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tweetList.setLayoutOrientation(JList.VERTICAL);
		tweetList.addListSelectionListener(this);
		
		tweetSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tweetList, tweetEditor);
		tweetSplit.setOneTouchExpandable(false);
		tweetSplit.setDividerLocation(200);
		pane.add(tweetSplit, BorderLayout.CENTER);
		
		reloadData();
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == newTweet) {
			if (i_tweetListSelected >= 0) {
				saveCurrentTweet();
			}
			td.storeStatus("d kevinchen test");
			reloadData();
			tweetList.setSelectedIndex(td.getNumStoredStatuses() - 1);
		}
		else if (source == sendTweet) {
			// TODO: doesn't work
			sendTweet.setText("Posting tweet\u2026");
			sendTweet.setEnabled(false);
			saveCurrentTweet();
			td.postStoredStatus(i_tweetListSelected);
			sendTweet.setText(sendTweetText);
			reloadData();
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		Debug.println("valueChanged()");
		JList list = (JList)e.getSource();
		// TODO: this doesn't work
		if (i_tweetListSelected == list.getMinSelectionIndex()) {
			return;
		}
		if (i_tweetListSelected >= 0) {
			saveCurrentTweet();
		}
		
		if (list.getMinSelectionIndex() < 0) {
			Debug.println("Selection was empty");
			i_tweetListSelected = -1;
		}
		else {
			// only care about the minimum because selection mode only lets us select one at a time
			i_tweetListSelected = list.getMinSelectionIndex();
			Debug.println("list: selected index " + i_tweetListSelected);
		}
		
		reloadData();
	}
	
	public void saveCurrentTweet() {
		td.setStoredStatus(i_tweetListSelected, tweetField.getText());
	}
	
	public void reloadData() {
		int numStatuses = td.getNumStoredStatuses();
		if (i_tweetListSelected >= numStatuses) {
			i_tweetListSelected = numStatuses - 1;
		}
		if (i_tweetListSelected >= 0) {
			tweetField.setText(td.getStoredStatus(i_tweetListSelected));
			sendTweet.setEnabled(true);
		}
		else {
			tweetField.setText("");
			sendTweet.setEnabled(false);
		}
		// Recalculate the layout
		tweetEditor.validate();
		
		// TODO: reload the list
		// TODO: remove repeated code from constructor
		tweetSplit.remove(tweetList);
		tweetList = new JList(td.getStoredStatusesPreview(25));
		tweetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tweetList.setLayoutOrientation(JList.VERTICAL);
		tweetList.addListSelectionListener(this);
		tweetList.setSelectedIndex(i_tweetListSelected);
		tweetSplit.add(tweetList, 0);
		tweetSplit.setDividerLocation(200);
	}
	
}
