package netclean.chat.packets.servertoclient;

import java.io.Serializable;

public class UserList implements Serializable
{
    public final String[] connectedUsers;
    
    public UserList(String[] connectedUsers)
    {
        this.connectedUsers = connectedUsers;
    }
}
