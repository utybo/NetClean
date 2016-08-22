package netclean.chat;

import netclean.chat.client.ChatClient;
import netclean.chat.server.ChatServer;

/**
 * Universal starter for NetClean Chat. If the program was launched without
 * arguments, launch ChatClient. If there are arguments, launch ChatServer.
 */
public class UniversalStarter
{

    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            ChatClient.main(args);
        }
        else
        {
            ChatServer.main(args);
        }
    }

}
