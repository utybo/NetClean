package netclean.chat.server.commands.context;

import netclean.chat.server.commands.CommandContext;

public interface TrackingContext extends CommandContext
{
    public long getTrackingId();
}
