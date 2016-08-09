package netclean.chat.packets.servertoclient;

import java.io.Serializable;

public class UserList implements Serializable
{
    private static final long serialVersionUID = 5778298832335371779L;

    public final String[] connectedUsers;

    public UserList(String[] connectedUsers)
    {
        this.connectedUsers = connectedUsers;
    }
}
