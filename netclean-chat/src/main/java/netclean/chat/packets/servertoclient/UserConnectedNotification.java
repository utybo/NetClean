package netclean.chat.packets.servertoclient;

import java.io.Serializable;

public class UserConnectedNotification implements Serializable
{
    private static final long serialVersionUID = 1L;

    public final String name;

    public UserConnectedNotification(String name)
    {
        this.name = name;
    }
}
