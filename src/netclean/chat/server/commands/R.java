package netclean.chat.server.commands;

import netclean.chat.packets.servertoclient.UserList;
import netclean.chat.server.ChatServer;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;

public class R implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy)
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

}
