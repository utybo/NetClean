package netclean.chat.packets.clienttoserver;

import java.io.Serializable;

public class CommandOTA implements Serializable
{
    public final String commandName, commandBody;

    public CommandOTA(String commandName, String commandBody)
    {
        this.commandName = commandName;
        this.commandBody = commandBody;
    }
}
