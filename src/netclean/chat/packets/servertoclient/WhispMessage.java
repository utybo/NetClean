package netclean.chat.packets.servertoclient;

import java.io.Serializable;

public class WhispMessage implements Serializable
{
    public final String from, to, message;

    public WhispMessage(String from, String to, String message)
    {
        this.from = from;
        this.to = to;
        this.message = message;
    }
}
