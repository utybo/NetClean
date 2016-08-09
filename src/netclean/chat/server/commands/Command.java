package netclean.chat.server.commands;

import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.exception.CommandException;
import netclean.chat.server.commands.exception.NotEnoughPermissionException;
import netclean.chat.server.commands.exception.WrongUsageException;

public interface Command
{
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context) throws WrongUsageException, NotEnoughPermissionException, CommandException;
    
    public boolean requiresAuth();
    
    public int minimumPermLevel();
    
    public String getPreferredCommand();
    
    public String getSyntax();
    
    public String getShortHelp();
    
    public default String getLongHelp()
    {
        return getShortHelp();
    }
}
