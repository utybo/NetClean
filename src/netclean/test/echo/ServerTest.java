package netclean.test.echo;

import java.io.IOException;
import java.net.UnknownHostException;

import netclean.NCPeer;
import netclean.NCServer;
import netclean.PeerListener;
import netclean.ServerListener;
import netclean.test.Console;

public class ServerTest
{
    private static volatile int id = 0;

    public static void main(String[] args)
    {
        new Console("Server console");
        try
        {
            log("E<>C<>H<>O server");
            NCServer server = new NCServer(25555);
            server.start(new ServerListener()
            {

                @Override
                public void exceptionThrown(Exception e, NCPeer onClient)
                {
                    System.err.println("Error on client " + onClient.getUserProperty("id").toString());
                    e.printStackTrace();
                }

                @Override
                public void newClient(NCPeer client)
                {
                    id++;
                    client.setUserProperty("id", id);
                    System.out.println("New peer : " + id);
                }
            }, new PeerListener()
            {
                @Override
                public void onDataReceived(byte[] received, NCPeer peer)
                {
                    System.out.println("Peer " + peer.getUserProperty("id") + " : Data received and sent back");
                    peer.send(received);
                }

                @Override
                public void onConnectionInterrupted(NCPeer on)
                {
                    System.out.println("Peer " + on.getUserProperty("id") + " : Connection terminated");
                }
            });
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void log(String string)
    {
        System.out.println(string);
    }
}
