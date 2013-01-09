package edu.umd.cs.pugh.chat;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.Presence;
import com.google.appengine.api.xmpp.PresenceType;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class XMPPSubscribeServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // In the handler for _ah/xmpp/presence/available
        XMPPService xmppService = XMPPServiceFactory.getXMPPService();
        Presence presence = xmppService.parsePresence(req);

        Message msg = new MessageBuilder()
        	.withBody("Welcome to Native Language.")
        	.build();

        SendResponse status = xmppService.sendMessage(msg);

    }
}