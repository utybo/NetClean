package netclean.chat.server.commands;

import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;

public class Send implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context)
    {
        synchronized(ChatServer.usersLock)
        {
            for(UserConnection uc : ChatServer.users)
            {
                if(uc.isAuth() && uc.getUser().getPermLevel() > PermissionLevels.BANNED)
                    uc.getPeer().send(ChatServer.objectToByteArray(new Message(commandDesc, sentBy.getDisplayName(), MessageType.MESSAGE)));
            }
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
