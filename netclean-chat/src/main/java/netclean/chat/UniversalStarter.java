package netclean.chat;

import netclean.chat.client.ChatClient;
import netclean.chat.server.ChatServer;

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
