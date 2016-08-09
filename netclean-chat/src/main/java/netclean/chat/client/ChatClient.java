package netclean.chat.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import netclean.chat.packets.clienttoserver.CommandOTA;
import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.packets.servertoclient.UserConnectedNotification;
import netclean.chat.packets.servertoclient.UserDisconnectedNotification;
import netclean.chat.packets.servertoclient.UserList;
import netclean.chat.packets.servertoclient.WhispMessage;
import netclean.framework.NCClient;
import netclean.framework.NCPeer;
import netclean.framework.PeerListener;

public class ChatClient
{
    static NCClient client;
    static ClientGUI gui;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        }
        catch(UnsupportedLookAndFeelException e1)
        {
            e1.printStackTrace();
        }
        try
        {
            gui = new ClientGUI();
            gui.setVisible(true);

            ConnectDialog cd = new ConnectDialog(gui);
            cd.setVisible(true);

            client = new NCClient(cd.txtLocalhost.getText(), (int)cd.textField_1.getValue(), new PeerListener()
            {
                @Override
                public void onDataReceived(byte[] received, NCPeer peer)
                {
                    try
                    {
                        Object o = byteArrayToObject(received);

                        if(o instanceof Message)
                        {
                            Message m = (Message)o;
                            if(m.author != null)
                            {
                                gui.append("[" + m.author + "] : " + m.message, m.type);
                            }
                            else
                                gui.append(m.message, m.type);
                        }
                        if(o instanceof UserConnectedNotification)
                        {
                            UserConnectedNotification udn = (UserConnectedNotification)o;
                            gui.append("--- " + udn.name + " is now connected", MessageType.INFO);
                        }
                        if(o instanceof UserDisconnectedNotification)
                        {
                            UserDisconnectedNotification udn = (UserDisconnectedNotification)o;
                            gui.append("--- " + udn.name + " is now disconnected", MessageType.INFO);
                        }
                        if(o instanceof UserList)
                        {
                            UserList ul = (UserList)o;
                            gui.updateList(new ArrayList<>(Arrays.asList(ul.connectedUsers)));
                        }
                        if(o instanceof WhispMessage)
                        {
                            WhispMessage wm = (WhispMessage)o;
                            gui.append("* " + wm.from + " => " + wm.to + " : " + wm.message, MessageType.WHISP);
                        }
                    }
                    catch(ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConnectionInterrupted(NCPeer ncPeer)
                {
                    gui.append("You were disconnected. Quitting in 5 seconds...", MessageType.ALERT);
                    try
                    {
                        Thread.sleep(5000L);
                    }
                    catch(InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.exit(0);
                    }
                    System.exit(0);
                }
            });

            gui.setTitle("NetClean Chat | Connected to " + cd.txtLocalhost.getText());
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Could not connect to the server");
            System.exit(0);
        }
    }

    static void send(String s)
    {
        if(s.startsWith("/"))
        {
            String head = s.split(" ")[0].substring(1);
            String body = "";
            if(s.split(" ").length > 1)
            {
                body = s.substring(s.indexOf(' ') + 1);
            }
            client.send(objectToByteArray(new CommandOTA(head, body)));
        }
        else
        {
            client.send(objectToByteArray(new CommandOTA("send", s)));
        }
    }

    public static byte[] objectToByteArray(Object o)
    {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos))
        {
            oos.writeObject(o);
            oos.flush();
            return baos.toByteArray();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Object byteArrayToObject(byte[] received) throws ClassNotFoundException
    {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(received); ObjectInputStream ois = new ObjectInputStream(bais))
        {
            return ois.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
