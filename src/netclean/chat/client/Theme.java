package netclean.chat.client;

import netclean.chat.packets.servertoclient.MessageType;

public abstract class Theme
{
    public abstract MessageStyle getStyle(MessageType type);
}
