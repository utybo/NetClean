package netclean.chat.server.commands;

import netclean.SerialUtils;
import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.packets.servertoclient.UserConnectedNotification;
import netclean.chat.packets.servertoclient.UserList;
import netclean.chat.server.ChatServer;
import netclean.chat.server.ChatUser;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;

public class AuthCommand implements Command
{
    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        if(sentBy.isAuth())
        {
            sentBy.getPeer().send(ChatServer.objectToByteArray(new Message("You are already logged in as '" + sentBy.getName() + "'!", null, MessageType.ERROR)));
        }
        else
        {
            String[] splits = commandDesc.split(" ");
            String uname = splits[0];
            if(splits.length < 2 || uname.isEmpty())
            {
                MessagingUtils.sendSystemMessage(sentBy, "Incorrect syntax : '/auth <username> <password>", MessageType.ERROR);
                return;
            }
            try
            {
                byte[] hash = ChatUser.sha256(commandDesc.substring(commandDesc.indexOf(' ', commandDesc.indexOf(' ' + 1)) + 1));
                ChatUser user = ChatServer.getData().match(uname, hash);
                if(user != null)
                {
                    if(ChatServer.isConnected(uname))
                    {
                        MessagingUtils.sendSystemMessage(sentBy, "You are already connected from another location.", MessageType.ERROR);
                        return;
                    }
                    sentBy.auth(user);
                    MessagingUtils.sendSystemMessage(sentBy, "You were successfully logged in as " + uname + "!", MessageType.SUCCESS);
                    String welcomeMessage = "";
                    switch(user.getPermLevel())
                    {
                    case PermissionLevels.BANNED:
                        welcomeMessage = "You are banned from this server. You cannot do anything.";
                        break;
                    case PermissionLevels.GHOST:
                        welcomeMessage = "You are a ghost. You cannot talk, but you are invisible to others";
                        break;
                    case PermissionLevels.VIEWER:
                        welcomeMessage = "You are a simple viewer. You cannot talk.";
                        break;
                    case PermissionLevels.RESTRICTED:
                        welcomeMessage = "You are a restricted user. You can talk, but cannot directly interact with others using /whisp. You cannot use some other commands.";
                        break;
                    case PermissionLevels.TALKER:
                        welcomeMessage = "You are a regular user. Welcome back!";
                        break;
                    case PermissionLevels.VIP:
                        welcomeMessage = "You are a VIP, you can talk during restricted times. Welcome back!";
                        break;
                    case PermissionLevels.ELDER:
                        welcomeMessage = "You are an elder user. You have limited access to moderation commands. Welcome back!";
                        break;
                    case PermissionLevels.MOD:
                        welcomeMessage = "You are a moderator with full access to moderating commands. Welcome back!";
                        break;
                    case PermissionLevels.ADMIN:
                        welcomeMessage = "Hi there, admin! *hugs* \\^w^/";
                        break;
                    }
                    if(!welcomeMessage.isEmpty())
                    {
                        MessagingUtils.sendSystemMessage(sentBy, welcomeMessage, MessageType.NOTIFICATION);
                    }
                    if(sentBy.getUser().getPermLevel() > PermissionLevels.GHOST)
                    {
                        synchronized(ChatServer.usersLock)
                        {
                            for(UserConnection uc : ChatServer.users)
                            {
                                if(uc.isAuth())
                                {
                                    uc.getPeer().send(SerialUtils.objectToByteArray(new UserConnectedNotification(sentBy.getDisplayName())));
                                }
                            }
                        }
                    }
                    sentBy.getPeer().send(ChatServer.objectToByteArray(new UserList(ChatServer.userList())));
                }
                else
                    MessagingUtils.sendSystemMessage(sentBy, "Incorrect username or password. Have you created an account with '/new <username> <password>'?", MessageType.ERROR);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
                MessagingUtils.sendSystemMessage(sentBy, "Incorrect syntax : '/auth <username> <password>", MessageType.ERROR);
            }
        }

    }

    @Override
    public boolean requiresAuth()
    {
        return false;
    }

    @Override
    public int minimumPermLevel()
    {
        return 0; // We don't really care as this is for unauth people
    }

}
