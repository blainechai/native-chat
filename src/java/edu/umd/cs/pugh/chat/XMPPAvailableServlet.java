package edu.umd.cs.pugh.chat;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.Presence;
import com.google.appengine.api.xmpp.PresenceType;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class XMPPAvailableServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // In the handler for _ah/xmpp/presence/available
        XMPPService xmppService = XMPPServiceFactory.getXMPPService();
        Presence presence = xmppService.parsePresence(req);
        JID jId = presence.getFromJid();

        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Status st = Status.getStatus(pm, jId);

            xmppService.sendPresence(jId, PresenceType.AVAILABLE, presence.getPresenceShow(), "Thinking");
            Message msg = new MessageBuilder().withRecipientJids(jId).withBody("Commands \n "
            		+ "Type \"speak spanish\" or \"speak english\" or \"speak french\"  or \"speak tagalog\" to set your language\n"
            		+ "Type \"chat with [gmail address]\" to chat with a friend\n"
            		+ "Type \"stop chatting\" to stop chatting\n")

                    .build();

            SendResponse status = xmppService.sendMessage(msg);
        } finally {
            pm.close();
        }

    }
}