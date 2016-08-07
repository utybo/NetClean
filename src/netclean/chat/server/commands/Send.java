package netclean.chat.server.commands;

import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;

public class Send implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        if(sentBy.isAuth())
        {
            synchronized(ChatServer.usersLock)
            {
                for(UserConnection uc : ChatServer.users)
                {
                    if(uc.isAuth())
                        uc.getPeer().send(ChatServer.objectToByteArray(new Message(commandDesc, sentBy.getDisplayName(), MessageType.MESSAGE)));
                }
            }
        }
        else
        {
            sentBy.getPeer().send(ChatServer.objectToByteArray(new Message("You are not authenticated! Use '/auth <username>' to connect to this server", null, MessageType.ERROR)));
        }
    }

    @Override
    public boolean requiresAuth()
    {
        return true;
    }

    @Override
    public int minimumPermLevel()
    {
        return PermissionLevels.TALKER;
    }

    @Override
    public String getPreferredCommand()
    {
        return "send";
    }

    @Override
    public String getSyntax()
    {
        return "/send message";
    }

    @Override
    public String getShortHelp()
    {
        return "Sends a message";
    }

}
