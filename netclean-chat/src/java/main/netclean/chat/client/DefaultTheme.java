package netclean.chat.client;

import java.awt.Color;

import netclean.chat.packets.servertoclient.MessageType;

public class DefaultTheme extends Theme
{

    @Override
    public MessageStyle getStyle(MessageType type)
    {
        switch(type)
        {
        case ALERT:
            return new MessageStyle(true, false, false, Color.RED, Color.WHITE);
        case ERROR:
            return new MessageStyle(false, false, false, Color.RED, null);
        case INFO:
            return new MessageStyle(false, true, false, Color.GRAY, null);
        case MESSAGE:
            return new MessageStyle();
        case SPECIAL:
            return new MessageStyle(false, false, false, Color.CYAN, null);
        case SUCCESS:
            return new MessageStyle(false, false, false, Color.GREEN, null);
        case WARNING:
            return new MessageStyle(false, false, false, Color.YELLOW, null);
        case WHISP:
            return new MessageStyle(false, true, false, new Color(200, 0, 200), null);
        case NOTIFICATION:
            return new MessageStyle(false, false, false, new Color(175, 175, 255), null);
        default:
            return new MessageStyle();

        }
    }

}
