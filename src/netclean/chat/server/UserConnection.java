package netclean.chat.server;

import netclean.NCPeer;
import netclean.PeerListener;
import netclean.chat.packets.clienttoserver.CommandOTA;
import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.packets.servertoclient.UserDisconnectedNotification;
import netclean.chat.server.commands.Command;
import netclean.chat.server.commands.MessagingUtils;

public class UserConnection implements PeerListener
{
    private NCPeer peer;
    private volatile ChatUser user;

    public UserConnection(NCPeer peer)
    {
        this.peer = peer;
    }

    @Override
    public void onDataReceived(byte[] received, NCPeer peer)
    {
        try
        {
            Object o = ChatServer.byteArrayToObject(received);
            if(o instanceof CommandOTA)
            {
                CommandOTA c = (CommandOTA)o;
                Command com = ChatServer.commandsRegistry.get(c.commandName);
                if(com != null)
                {
                    if(com.requiresAuth())
                    {
                        if(isAuth())
                        {
                            if(com.minimumPermLevel() <= user.getPermLevel())
                            {
                                com.exec(c.commandBody, this);
                            }
                            else
                            {
                                MessagingUtils.sendSystemMessage(this, "You do not have the required permission level to execute this command.", MessageType.ERROR);
                            }
                        }
                        else
                        {
                            MessagingUtils.sendSystemMessage(this, "You need to be logged in to use this command. Use '/auth <username> <password>' to log in or '/new <username> <password>' to create an account.", MessageType.ERROR);
                        }
                    }
                    else
                    {
                        com.exec(c.commandBody, this);
                    }
                }
                else
                {
                    peer.send(ChatServer.objectToByteArray(new Message("Invalid command : '" + c.commandName + "'.", null, MessageType.ERROR)));
                }
            }
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionInterrupted(NCPeer ncPeer)
    {
        synchronized(ChatServer.usersLock)
        {
            boolean b = ChatServer.users.remove(this);
            if(b && user != null && user.getPermLevel() > PermissionLevels.GHOST)
            {
                for(UserConnection uc : ChatServer.users)
                {
                    if(uc.isAuth() && uc.getUser().getPermLevel() > PermissionLevels.BANNED)
                        uc.getPeer().send(ChatServer.objectToByteArray(new UserDisconnectedNotification(getDisplayName())));
                }
            }
        }
    }

    public NCPeer getPeer()
    {
        return peer;
    }

    public boolean isAuth()
    {
        return user != null;
    }

    public void auth(ChatUser user)
    {
        this.user = user;
    }

    public String getName()
    {
        return user.getUsername();
    }

    public ChatUser getUser()
    {
        return user;
    }

    public String getDisplayName()
    {
        return PermissionLevels.getPrefix(user.getPermLevel()) + user.getUsername();
    }

}
