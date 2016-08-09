package netclean.chat.server.commands;

import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.packets.servertoclient.WhispMessage;
import netclean.chat.server.ChatServer;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.exception.CommandException;
import netclean.chat.server.commands.exception.WrongUsageException;

public class Whisp implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context) throws WrongUsageException, CommandException
    {
        if(sentBy.isAuth())
        {
            if(commandDesc.split(" ").length < 2)
            {
                throw new WrongUsageException(context);
            }
            String toWho = commandDesc.split(" ")[0];
            String message = commandDesc.substring(commandDesc.indexOf(' ') + 1);

            boolean b = false;
            synchronized(ChatServer.usersLock)
            {
                for(UserConnection uc : ChatServer.users)
                {
                    if(uc.isAuth() && uc.getName().equals(toWho) && uc.getUser().getPermLevel() >= PermissionLevels.TALKER)
                    {
                        byte[] bytes = ChatServer.objectToByteArray(new WhispMessage(sentBy.getName(), toWho, message));
                        uc.getPeer().send(bytes);
                        sentBy.getPeer().send(bytes);
                        return;
                    }
                }
            }
            if(b == false)
            {
                throw new CommandException("Could not whisp " + toWho + ". Are you sure they are connected and can receive whisps?", context);
            }

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
        return PermissionLevels.TALKER;
    }

    @Override
    public String getPreferredCommand()
    {
        return "whisp";
    }

    @Override
    public String getSyntax()
    {
        return "/whisp <username> <message>";
    }

    @Override
    public String getShortHelp()
    {
        return "Sends a private message";
    }

}
