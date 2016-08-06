package netclean.chat.server.commands;

import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.UserConnection;

public class MessagingUtils
{
    public static void sendSystemMessage(UserConnection toWho, String message, MessageType type)
    {
        toWho.getPeer().send(ChatServer.objectToByteArray(new Message(message, null, type)));
    }
}
