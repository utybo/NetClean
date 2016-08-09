package netclean.chat.server.commands;

import java.io.IOException;

import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.exception.CommandException;
import netclean.chat.server.commands.exception.ExecutionException;

public class DisconnectCommand implements Command
{
    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context) throws CommandException
    {
        try
        {
            sentBy.getPeer().stop();
        }
        catch(IOException e)
        {
            throw new ExecutionException("Could not disconnect you from the server.", e, context);
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
