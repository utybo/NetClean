package netclean.chat.packets.servertoclient;

import java.io.Serializable;

import netclean.chat.server.commands.CommandContext;

public class Message implements Serializable
{
    // 1L = initial 
    // 2L = Added type, removed isServerMessage

    private static final long serialVersionUID = 2L;

    public final String message, author;

    public final MessageType type;

    public final long trackingId;

    public Message(String message, String author, MessageType type)
    {
        this(message, author, type, -1);
    }

    public Message(String message, String author, MessageType type, long trackingId)
    {
        this.message = message;
        this.author = author;
        this.type = type;
        this.trackingId = trackingId;
    }
}
