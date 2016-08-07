package netclean.chat.server.watcher;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.ChatUser;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.Command;
import netclean.chat.server.commands.MessagingUtils;

public class ServerWatcherCommand implements Command
{

    @Override
    public synchronized void exec(String commandDesc, UserConnection sentBy)
    {
        MessagingUtils.sendSystemMessage(sentBy, "ServerWatcher has received your command. It will now be treated in a separate thread.", MessageType.NOTIFICATION);
        new Thread(() ->
        {
            try
            {
                if(commandDesc.equals("start"))
                {
                    log("Starting process initialized.", sentBy);
                    // Disconnect previous bot
                    if(ChatServer.isConnected(ServerWatcher.name))
                    {
                        log("ServerWatcher was previously connected. Disconnecting.", sentBy);
                        synchronized(ChatServer.usersLock)
                        {
                            for(UserConnection uc : ChatServer.users)
                            {
                                if(uc.getName().equals(ServerWatcher.name))
                                {
                                    uc.getPeer().stop();
                                    log("Succesfully disconnected the previous bot.", sentBy);
                                    break;
                                }
                            }
                        }
                    }
                    
                    // Remove previous bots from list
                    log("Deleting previous ServerWatcher auth data.", sentBy);
                    ChatServer.getData().delete(ServerWatcher.name);

                    log("Generating password", sentBy);
                    String pw = new BigInteger(130, new SecureRandom()).toString(32);
                    log("Creating account", sentBy);
                    ChatUser cu = ChatServer.getData().create(ServerWatcher.name, ChatUser.sha256(pw));
                    if(cu == null)
                    {
                        MessagingUtils.sendSystemMessage(sentBy, "Account creation failed.", MessageType.ERROR);
                        return;
                    }
                    log("Setting Ghost perm level...", sentBy);
                    cu.setPermLevel(PermissionLevels.GHOST);

                    log("Connecting...", sentBy);
                    new ServerWatcher(new BufferedWriter(new OutputStreamWriter(System.out)), ServerWatcher.name, pw, "localhost", ChatServer.getPort());
                    log("All done!", sentBy);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                MessagingUtils.sendSystemMessage(sentBy, "Error during the processing (" + e.getClass().getSimpleName() + ")", MessageType.ERROR);
            }
        }).start();;
    }

    private synchronized void log(String s, UserConnection sentBy)
    {
        MessagingUtils.sendSystemMessage(sentBy, s, MessageType.INFO);
        System.out.println(s);
    }

    @Override
    public boolean requiresAuth()
    {
        return true;
    }

    @Override
    public int minimumPermLevel()
    {
        return PermissionLevels.ADMIN;
    }

    @Override
    public String getPreferredCommand()
    {
        return "watcher";
    }

    @Override
    public String getSyntax()
    {
        return "/watcher start|stop";
    }

    @Override
    public String getShortHelp()
    {
        return "Starts or stop the ServerWatcher";
    }

}
