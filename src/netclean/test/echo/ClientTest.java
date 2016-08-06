package netclean.test.echo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import netclean.NCClient;
import netclean.NCPeer;
import netclean.PeerListener;
import netclean.test.Console;

public class ClientTest
{
    private static NCClient client;
    public static void main(String[] args)
    {
        new Console("Client console");
        try
        {
            client = new NCClient("localhost", 25555, new PeerListener()
            {

                @Override
                public void onDataReceived(byte[] received, NCPeer peer)
                {
                    try
                    {
                        System.out.println("RECEIVED : " + new String(received, "UTF-8"));
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionInterrupted(NCPeer on)
                {
                    System.out.println("Connection interrupted");
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

        while(true)
        {
            String s = JOptionPane.showInputDialog("Send to server : ");
            if(s != null)
            {
                try
                {
                    client.send(s.getBytes("UTF-8"));
                }
                catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    client.stop();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
