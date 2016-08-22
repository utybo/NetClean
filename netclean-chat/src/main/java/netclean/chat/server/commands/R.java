package netclean.chat.server.commands;

import netclean.chat.common.PermissionLevels;
import netclean.chat.common.packets.servertoclient.UserList;
import netclean.chat.server.ChatServer;
import netclean.chat.server.UserConnection;

public class R implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context)
    {
        if(sentBy.isAuth())
        {
            if("users".equals(commandDesc))
                sentBy.getPeer().send(ChatServer.objectToByteArray(new UserList(ChatServer.userList())));
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
        return PermissionLevels.GHOST;
    }

    @Override
    public String getPreferredCommand()
    {
        return "r";
    }

    @Override
    public String getSyntax()
    {
        return "/r <module>";
    }

    @Override
    public String getShortHelp()
    {
        return "Utility command which re-sends some information";
    }

}
