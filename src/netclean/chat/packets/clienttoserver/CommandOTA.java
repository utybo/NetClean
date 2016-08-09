package netclean.chat.packets.clienttoserver;

import java.io.Serializable;

public class CommandOTA implements Serializable
{
    public final String commandName, commandBody;
    
    public final long trackingId;

    public CommandOTA(String commandName, String commandBody)
    {
        this.commandName = commandName;
        this.commandBody = commandBody;
        this.trackingId = -1;
    }
    
    public CommandOTA(String commandName, String commandBody, int trackingId)
    {
        this.commandName = commandName;
        this.commandBody = commandBody;
        this.trackingId = trackingId;
    }
}
