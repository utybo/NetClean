package netclean.chat.packets.servertoclient;

import java.io.Serializable;

public class UserDisconnectedNotification implements Serializable
{
    private static final long serialVersionUID = 1L;

    public final String name;

    public UserDisconnectedNotification(String name)
    {
        this.name = name;
    }
}
