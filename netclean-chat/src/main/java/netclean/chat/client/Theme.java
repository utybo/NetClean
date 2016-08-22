package netclean.chat.client;

import netclean.chat.common.packets.servertoclient.MessageType;

/**
 * A theme, that gives detailed information on the style to use for each message type.
 * @author utybo
 *
 */
public abstract class Theme
{
    public abstract MessageStyle getStyle(MessageType type);
    
    public abstract MessageStyle getSpecialStyle(SpecialStyleType type);
}
