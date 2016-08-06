package netclean.chat.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import netclean.NCClient;
import netclean.NCPeer;
import netclean.PeerListener;
import netclean.SerialUtils;
import netclean.chat.packets.clienttoserver.CommandOTA;
import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.UserConnectedNotification;
import netclean.chat.packets.servertoclient.UserDisconnectedNotification;

public class ServerWatcher implements PeerListener
{
    private NCClient client;
    private BufferedWriter writer;

    public ServerWatcher(BufferedWriter w, String uname, String pw, String host, int port) throws UnknownHostException, IOException
    {
        writer = w;

        client = new NCClient(host, port, this);
        client.send(SerialUtils.objectToByteArray(new CommandOTA("auth", uname + " " + pw)));
    }

    @Override
    public void onDataReceived(byte[] received, NCPeer peer)
    {
        try
        {
            Object o = SerialUtils.byteArrayToObject(received);
            if(o instanceof Message)
            {
                Message m = (Message)o;
                if(m.author != null)
                {
                    writer.write("[" + m.author + "] " + m.message);
                }
                else
                {
                    writer.write("! SYSTEM : " + m.message);
                }
            }
            else if(o instanceof UserConnectedNotification)
            {
                UserConnectedNotification ucn = (UserConnectedNotification)o;
                writer.write("--- Connected : " + ucn.name);
            }
            else if(o instanceof UserDisconnectedNotification)
            {
                UserDisconnectedNotification udn = (UserDisconnectedNotification)o;
                writer.write("--- Disconnected : " + udn.name);
            }
            else
            {
                writer.write("(Ignored : " + o.getClass().getName() + " packet)");
            }
            writer.newLine();
            writer.flush();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionInterrupted(NCPeer ncPeer)
    {
        try
        {
            writer.write("--- CONNECTION INTERRUPTED ---");
            writer.newLine();
            writer.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
