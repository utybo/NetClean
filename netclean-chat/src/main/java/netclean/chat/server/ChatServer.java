package netclean.chat.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.google.gson.Gson;

import netclean.chat.common.PermissionLevels;
import netclean.chat.common.packets.servertoclient.Message;
import netclean.chat.common.packets.servertoclient.MessageType;
import netclean.chat.server.commands.AuthCommand;
import netclean.chat.server.commands.Command;
import netclean.chat.server.commands.DisconnectCommand;
import netclean.chat.server.commands.HelpCommand;
import netclean.chat.server.commands.NewCommand;
import netclean.chat.server.commands.R;
import netclean.chat.server.commands.Send;
import netclean.chat.server.commands.SetPermCommand;
import netclean.chat.server.commands.Whisp;
import netclean.chat.server.watcher.ServerWatcherCommand;
import netclean.framework.NCPeer;
import netclean.framework.NCServer;
import netclean.framework.PeerListener;
import netclean.framework.SerialUtils;
import netclean.framework.ServerListener;

public class ChatServer
{
    public static final Vector<UserConnection> users = new Vector<>();
    public static final Object usersLock = new Object();

    private static int port;

    private static ChatServerFile data;

    public static final HashMap<String, Command> commandsRegistry = new HashMap<>();
    static
    {
        // --- NetClean Chat Basic Commands --- //
        commandsRegistry.put("send", new Send());

        commandsRegistry.put("auth", new AuthCommand());

        commandsRegistry.put("r", new R());

        commandsRegistry.put("w", new Whisp());
        commandsRegistry.put("whisp", new Whisp());
        commandsRegistry.put("msg", new Whisp());
        commandsRegistry.put("whisper", new Whisp());

        commandsRegistry.put("new", new NewCommand());

        commandsRegistry.put("q", new DisconnectCommand());
        commandsRegistry.put("d", new DisconnectCommand());
        commandsRegistry.put("quit", new DisconnectCommand());
        commandsRegistry.put("disconnect", new DisconnectCommand());
        
        commandsRegistry.put("setperm", new SetPermCommand());
        
        commandsRegistry.put("help", new HelpCommand());
        commandsRegistry.put("h", new HelpCommand());

        // --- NetClean Chat ServerWatcher --- //
        commandsRegistry.put("watcher", new ServerWatcherCommand());
    }

    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.err.println("Usage : <port> [server file]");
            return;
        }
        System.out.println("NETCLEAN CHAT -- Server");
        port = Integer.valueOf(args[0]);
        if(args.length < 2)
        {
            System.out.println("!! ChatServer is running in FILE-LESS MODE. NOTHING WILL BE PERSISTENT.");
            data = new ChatServerFile();
        }
        else
        {
            System.out.println("Loading file : " + args[1]);
            File f = new File(args[1]);
            try
            {
                if(f.exists())
                {
                    System.out.println("File already exists, loading...");
                    data = new Gson().fromJson(new FileReader(f), ChatServerFile.class);
                    data.setFile(f);
                }
                if(data == null)
                {
                    System.out.println("File does not exist, creating...");
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                    data = new ChatServerFile();
                    data.setFile(f);
                    data.save();
                }
            }
            catch(Exception e)
            {
                System.out.println("Error during server file loading");
                e.printStackTrace();
                return;
            }
        }

        System.out.println("Starting server...");
        NCServer server = new NCServer(port);
        try
        {
            server.start(new ServerListener()
            {
                @Override
                public void newClient(NCPeer client)
                {
                    client.send(SerialUtils.objectToByteArray(new Message("Welcome to the server! Use '/auth <username> <password>' to log in, or '/new <username> <password>' to create an account!", null, MessageType.SPECIAL)));
                }

                @Override
                public void exceptionThrown(Exception e, NCPeer onClient)
                {
                    e.printStackTrace();
                }

                @Override
                public boolean supportsListenerCreation()
                {
                    return true;
                }

                @Override
                public PeerListener createPeerListener(NCPeer peer)
                {
                    UserConnection pc = new UserConnection(peer);
                    synchronized(usersLock)
                    {
                        users.add(pc);
                    }
                    return pc;
                }
            }, null);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Server started. Use '/watcher start' to fire up the ServerWatcher bot");

        //        // Start watcher
        //        if(!data.exists("[BOT]ServerWatcher"))
        //        {
        //            data.create("[BOT]ServerWatcher", ChatUser.sha256());
        //        }
        //        try
        //        {
        //            ServerWatcher sw = new ServerWatcher(new BufferedWriter(new OutputStreamWriter(System.out)), "[BOT]ServerWatcher", "UberSuperCoolString", "localhost", port);
        //        }
        //        catch(UnknownHostException e)
        //        {
        //            e.printStackTrace();
        //        }
        //        catch(IOException e)
        //        {
        //            e.printStackTrace();
        //        }
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

    public static boolean isConnected(String uname)
    {
        synchronized(usersLock)
        {
            for(UserConnection uc : users)
            {
                if(uc.isAuth() && uname.equals(uc.getName()))
                    return true;
            }
            return false;
        }
    }

    public static String[] userList()
    {
        synchronized(usersLock)
        {
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < users.size(); i++)
            {
                UserConnection uc = users.get(i);
                if(uc.isAuth() && uc.getUser().getPermLevel() > PermissionLevels.GHOST && uc.getDisplayName() != null)
                    list.add(uc.getDisplayName());
            }
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }
    }

    public static ChatServerFile getData()
    {
        return data;
    }

    public static int getDefaultPermLevel()
    {
        return PermissionLevels.TALKER;
    }

    public static int getPort()
    {
        return port;
    }
}
