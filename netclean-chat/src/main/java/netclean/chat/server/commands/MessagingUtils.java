package netclean.chat.server.commands;

import netclean.chat.common.packets.servertoclient.Message;
import netclean.chat.common.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.context.TrackingContext;

public class MessagingUtils
{
    public static void sendSystemMessage(UserConnection toWho, String message, MessageType type, CommandContext context)
    {
        Message m = null;
        if(context instanceof TrackingContext)
            m = new Message(message, null, type, ((TrackingContext)context).getTrackingId());
        else
            m = new Message(message, null, type);
        toWho.getPeer().send(ChatServer.objectToByteArray(m));
    }
}
