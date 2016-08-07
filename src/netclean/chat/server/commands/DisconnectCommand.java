package netclean.chat.server.commands;

import java.io.IOException;

import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.UserConnection;

public class DisconnectCommand implements Command
{
    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        try
        {
            sentBy.getPeer().stop();
        }
        catch(IOException e)
        {
            MessagingUtils.sendSystemMessage(sentBy, "Could not disconnect you from the server.", MessageType.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public boolean requiresAuth()
    {
        return false;
    }

    @Override
    public int minimumPermLevel()
    {
        return 0;
    }

    @Override
    public String getPreferredCommand()
    {
        return "disconnect";
    }

    @Override
    public String getSyntax()
    {
        return "/disconnect";
    }

    @Override
    public String getShortHelp()
    {
        return "Stops the current server<-->client session, disconnecting the peer";
    }

}
