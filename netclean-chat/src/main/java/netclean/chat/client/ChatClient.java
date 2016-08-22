package netclean.chat.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import netclean.chat.common.packets.clienttoserver.CommandOTA;
import netclean.chat.common.packets.servertoclient.Message;
import netclean.chat.common.packets.servertoclient.MessageType;
import netclean.chat.common.packets.servertoclient.UserConnectedNotification;
import netclean.chat.common.packets.servertoclient.UserDisconnectedNotification;
import netclean.chat.common.packets.servertoclient.UserList;
import netclean.chat.common.packets.servertoclient.WhispMessage;
import netclean.framework.NCClient;
import netclean.framework.NCPeer;
import netclean.framework.PeerListener;
import netclean.framework.SerialUtils;

/**
 * Main class for NetClean Chat Client
 * 
 * @author matthieu
 *
 */
public class ChatClient
{
    static NCClient client;
    static ClientGUI gui;

    /**
     * Launch the client
     * 
     * @param args
     *            Arguments (should be an empty array)
     */
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
                        Object o = SerialUtils.byteArrayToObject(received);

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
                            peer.send(SerialUtils.objectToByteArray(new CommandOTA("r", "users")));
                        }
                        if(o instanceof UserDisconnectedNotification)
                        {
                            UserDisconnectedNotification udn = (UserDisconnectedNotification)o;
                            gui.append("--- " + udn.name + " is now disconnected", MessageType.INFO);
                            peer.send(SerialUtils.objectToByteArray(new CommandOTA("r", "users")));
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

    /**
     * Utility method for sending methods ("/..." ==> CommandOTA packet ==> send
     * to server)
     * 
     * @param s
     */
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
            client.send(SerialUtils.objectToByteArray(new CommandOTA(head, body)));
        }
        else
        {
            client.send(SerialUtils.objectToByteArray(new CommandOTA("send", s)));
        }
    }
}
