package netclean.chat.server.commands;

import netclean.chat.server.UserConnection;

public interface Command
{
    public void exec(String commandDesc, UserConnection sentBy);
    
    public boolean requiresAuth();
    
    public int minimumPermLevel();
}
