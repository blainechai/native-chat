package edu.umd.cs.pugh.chat;

import java.io.IOException;

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
public class AppEngineChatServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");
        JID jid = new JID("teammariel@gmail.com");
        String msgBody = "Hello";
        Message msg = new MessageBuilder().withRecipientJids(jid).withBody(msgBody).build();

        PersistenceManager pm = PMF.get().getPersistenceManager();

        Status st = Status.getStatus(pm, jid);

        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        if (xmpp.getPresence(jid).isAvailable()) {
            SendResponse status = xmpp.sendMessage(msg);
            boolean messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
            resp.getWriter().println("Sent: " + messageSent);
        } else {
            resp.getWriter().println("not available");

        }
    }
}
