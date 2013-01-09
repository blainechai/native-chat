package edu.umd.cs.pugh.chat;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.xmpp.JID;

@PersistenceCapable
public class Status {

    public static String getAccount(JID jid) {
        String id = jid.getId();
        int i = id.indexOf("/");
        if (i < 0) return id;
        return id.substring(0,i);
    }

    @PrimaryKey
    String id;

    public Status(String id) {
        this.id = id;
    }

    public Status() {
    }

    @Persistent
	String friend;
    @Persistent
    int languageCode = 0;
    
    public void setLanguage(String answer) {
    	if (answer.substring(0, 10).equals("speak engl")) {
    		languageCode = 0;
    	} else if (answer.substring(0, 10).equals("speak span")) {
    		languageCode = 1;
        } else if (answer.substring(0, 10).equals("speak fren")) {
    		languageCode = 2;
        } else if (answer.substring(0, 10).equals("speak taga")) {
    		languageCode = 3;
        }
    }
    
    public int getLanguageCode() {
    	return languageCode;
    }
    
    public void setFriend(String friend) {
    	this.friend = friend;
    }
    
    public String getFriend() {
    	return friend;
    }
    
    public String getId() {
    	return id;
    }

    static Status getStatus(PersistenceManager pm, JID id) {
        String account = getAccount(id);
        try {
            return pm.getObjectById(Status.class, account);
        } catch (javax.jdo.JDOFatalUserException e) {
            Status status = new Status(account);
            pm.makePersistent(status);
            return status;
        } catch (javax.jdo.JDOObjectNotFoundException ex) {
            Status status = new Status(account);
            pm.makePersistent(status);
            return status;
        }
    }

}
