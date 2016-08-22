package netclean.chat.server.commands;

import netclean.chat.common.PermissionLevels;
import netclean.chat.common.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.ChatUser;
import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.exception.CommandException;
import netclean.chat.server.commands.exception.WrongUsageException;

public class SetPermCommand implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context) throws WrongUsageException, CommandException
    {
        System.out.println(commandDesc);
        String[] strings = commandDesc.split(" ");

        if(strings.length == 1 && strings[0].equals("help"))
        {
            String st = "Permission Levels :\n";
            st += "-2 : Banned (use /ban instead of /setperm)\n";
            st += "-1 : Ghost (invisible Viewer)\n";
            st += "0  : Viewer (cannot talk)\n";
            st += "1  : Restricted (can only use '/send' for basic messages)\n";
            st += "2  : Talker (basic user)\n";
            st += "3  : VIP (can talk during restricted time)\n";
            st += "4  : Elder (can /kick people + VIP advantages)\n";
            st += "5  : Moderator (has full moderation power)\n";
            st += "6  : Admin (has full administration power)\n";
            st += "\n--- End ---";
            MessagingUtils.sendSystemMessage(sentBy, st, MessageType.MESSAGE, context);
        }
        else if(strings.length >= 2)
        {
            int lvl = Integer.valueOf(strings[0]);
            if(sentBy.getUser().getPermLevel() == PermissionLevels.ADMIN || (sentBy.getUser().getPermLevel() == PermissionLevels.MOD && lvl <= PermissionLevels.ELDER))
            {
                for(int i = 1; i < strings.length; i++)
                {
                    String username = strings[i];
                    ChatUser cu = ChatServer.getData().getUser(username);
                    if(cu != null)
                    {
                        cu.setPermLevel(lvl);
                        MessagingUtils.sendSystemMessage(sentBy, "Successfully set the permission level of user " + username + " to " + PermissionLevels.getName(lvl), MessageType.NOTIFICATION, context);
                        if(ChatServer.isConnected(username))
                        {
                            synchronized(ChatServer.usersLock)
                            {
                                for(UserConnection uc : ChatServer.users)
                                {
                                    if(uc.getUser().getUsername().equals(username))
                                    {
                                        // Hide the context for security reasons
                                        MessagingUtils.sendSystemMessage(uc, "Your permission level was set to " + PermissionLevels.getName(lvl), MessageType.NOTIFICATION, null);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        throw new CommandException("User " + username + " does not exist", context);
                    }
                }
            }
        }
        else
        {
            throw new WrongUsageException(context);
        }
    }

    @Override
    public boolean requiresAuth()
    {
        return true;
    }

    @Override
    public int minimumPermLevel()
    {
        return PermissionLevels.MOD;
    }

    @Override
    public String getPreferredCommand()
    {
        return "setperm";
    }

    @Override
    public String getSyntax()
    {
        return "/setperm help OR /setperm <permlevel> <user> [user] [user]...";
    }

    @Override
    public String getShortHelp()
    {
        return "A tool for managing user permission levels";
    }

}
