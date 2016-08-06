package netclean.chat.server.commands;

import netclean.SerialUtils;
import netclean.chat.packets.servertoclient.Message;
import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.packets.servertoclient.WhispMessage;
import netclean.chat.server.ChatServer;
import netclean.chat.server.PermissionLevels;
import netclean.chat.server.UserConnection;

public class Whisp implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        if(sentBy.isAuth())
        {
            String toWho = commandDesc.split(" ")[0];
            String message = commandDesc.substring(commandDesc.indexOf(' ') + 1);
            if(message.equals(commandDesc))
            {
                sentBy.getPeer().send(SerialUtils.objectToByteArray(new Message("Syntax error : the syntax for /whisp is '/whisp <to> <message>'", null, MessageType.ERROR)));
            }
            else
            {
                UserConnection sendTo = null;
                boolean b = false;
                synchronized(ChatServer.usersLock)
                {
                    for(UserConnection uc : ChatServer.users)
                    {
                        if(uc.isAuth() && uc.getName().equals(toWho) && uc.getUser().getPermLevel() < PermissionLevels.TALKER)
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
                    MessagingUtils.sendSystemMessage(sentBy, "Could not whisp " + toWho + ". Are you sure they are connected?", MessageType.ERROR);
                }
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

}
