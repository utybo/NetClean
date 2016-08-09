package netclean.chat.client;

import java.awt.Color;

public class MessageStyle
{
    private boolean bold, italic, underlined;
    private Color foreground, background;
    
    public MessageStyle()
    {
        this(false, false, false, null, null);
    }

    public MessageStyle(boolean bold, boolean italic, boolean underlined, Color foreground, Color background)
    {
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.foreground = foreground;
        this.background = background;
    }

    public boolean isBold()
    {
        return bold;
    }

    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    public boolean isItalic()
    {
        return italic;
    }

    public void setItalic(boolean italic)
    {
        this.italic = italic;
    }

    public boolean isUnderlined()
    {
        return underlined;
    }

    public void setUnderlined(boolean underlined)
    {
        this.underlined = underlined;
    }

    public Color getForeground()
    {
        return foreground;
    }

    public void setForeground(Color foreground)
    {
        this.foreground = foreground;
    }

    public Color getBackground()
    {
        return background;
    }

    public void setBackground(Color background)
    {
        this.background = background;
    }

}
