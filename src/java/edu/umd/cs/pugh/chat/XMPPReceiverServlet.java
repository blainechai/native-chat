package edu.umd.cs.pugh.chat;


import java.io.IOException;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class XMPPReceiverServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		Message message = xmpp.parseMessage(req);

		JID fromJid = message.getFromJid();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Status st = Status.getStatus(pm, fromJid);
			String body = message.getBody();
			boolean hasFriend = false;
			Status st2 = null;

			if (st.getFriend() != null) {
				JID toJid = new JID(st.getFriend());
				st2 = Status.getStatus(pm, toJid);
				hasFriend = true;
			}
			if (body.length() > 9) {
				if (body.substring(0, 6).equals("speak ")) {
					st.setLanguage(body);
					String msgBody = "Language set.";
					sendMessage(xmpp, fromJid, msgBody);
				} else if (body.substring(0, 9).equals("stop chat")) {
					JID toJid = new JID(st.getFriend());
					st2 = Status.getStatus(pm, toJid);
					st.setFriend(null);
					if (st2.getFriend().equals(st.getId()))
						st2.setFriend(null);				
					
					String msgBody = "You are not chatting with anyone now";
					sendMessage(xmpp, toJid, msgBody);
					sendMessage(xmpp, fromJid, msgBody);
				} else if (body.substring(0, 10).equals("chat with ")) {
					st.setFriend(removeHyperlink(body.substring(10)));
					JID toJid = new JID(st.getFriend());
					st2 = Status.getStatus(pm, toJid);
					st2.setFriend(st.getId());
					
					String msgBody = "You are now chatting with " + st.getId();
					sendMessage(xmpp, toJid, msgBody);

					String msgBody2 = "Begin typing to send them a message.";
					sendMessage(xmpp, fromJid, msgBody2);
				} else {
					reply(xmpp, fromJid, st, body, hasFriend, st2);
				}
			} else {
				reply(xmpp, fromJid, st, body, hasFriend, st2);
			}
		} catch (Exception e) {
			sendMessage(xmpp, fromJid, e.getMessage());
		} finally {
			pm.close();
		}

	}

	private void reply(XMPPService xmpp, JID fromJid, Status st, String body,
			boolean hasFriend, Status st2) throws Exception {
		if (hasFriend)
			replyToFriend(xmpp, fromJid, st, st2, body);
		else
			replyToSelf(xmpp, fromJid, st, body);
	}

	private void replyToSelf(XMPPService xmpp, JID fromJid, Status st, String body)
	throws Exception {
		sendMessage(xmpp, fromJid, body);
	}

	private void replyToFriend(XMPPService xmpp, JID fromJid, Status st, Status st2, String body)
	throws Exception {
		JID toJid = new JID(st.getFriend());
		String msgBody = st.getId() + ": " + body;
		sendMessage(xmpp, toJid, translateMessage(msgBody, st.getLanguageCode(), st2.getLanguageCode()));
		sendMessage(xmpp, fromJid, translateMessage(msgBody, st.getLanguageCode(), st2.getLanguageCode()));
	} 


	public String removeHyperlink(String id) {
		int start = 0;
		int end = 4;
		for (int i = 4; i < id.length() + 1; i++) {
			if (id.substring(start, i).equals(".com")) {
				end = i;
				break;
			}
			start++;
		}
		return id.substring(0, end);
	}

	public String translateMessage(String mess, int yourLanguageCode, int theirLanguageCode) throws Exception {
		String translatedText = "";
		if (yourLanguageCode == theirLanguageCode) 
			translatedText = mess;
		else if (yourLanguageCode == 1 && theirLanguageCode == 0){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.SPANISH, Language.ENGLISH);
		} else if (yourLanguageCode == 0 && theirLanguageCode == 1){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.ENGLISH, Language.SPANISH);		
		} else if (yourLanguageCode == 1 && theirLanguageCode == 2){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.SPANISH, Language.FRENCH);		
		} else if (yourLanguageCode == 2 && theirLanguageCode == 1){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.FRENCH, Language.SPANISH);		
		} else if (yourLanguageCode == 0 && theirLanguageCode == 2){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.ENGLISH, Language.FRENCH);		
		} else if (yourLanguageCode == 2 && theirLanguageCode == 0){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.FRENCH, Language.ENGLISH);		
		} else if (yourLanguageCode == 0 && theirLanguageCode == 3){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.ENGLISH, Language.TAGALOG);		
		} else if (yourLanguageCode == 1 && theirLanguageCode == 3){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.SPANISH, Language.TAGALOG);		
		} else if (yourLanguageCode == 2 && theirLanguageCode == 3){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.FRENCH, Language.TAGALOG);		
		} else if (yourLanguageCode == 3 && theirLanguageCode == 2){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.TAGALOG, Language.FRENCH);		
		} else if (yourLanguageCode == 3 && theirLanguageCode == 1){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.TAGALOG, Language.SPANISH);		
		} else if (yourLanguageCode == 3 && theirLanguageCode == 0){
			Translate.setHttpReferrer("http://marielmnop.tumblr.com/");
			translatedText = Translate.execute(mess, Language.TAGALOG, Language.ENGLISH);			
		} else {
			translatedText = mess;
		}		
		return translatedText;
	}

	public void sendMessage(XMPPService xmpp, JID recipient, String body) {
		Message msg = new MessageBuilder().withRecipientJids(recipient).withBody(body).build();
		SendResponse status = xmpp.sendMessage(msg);
	}

}
