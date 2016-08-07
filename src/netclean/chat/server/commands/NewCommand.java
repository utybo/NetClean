package netclean.chat.server.commands;

import java.io.IOException;
import java.util.regex.Pattern;

import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.ChatUser;
import netclean.chat.server.UserConnection;

public class NewCommand implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        if(sentBy.isAuth())
        {
            MessagingUtils.sendSystemMessage(sentBy, "You are already authenticated as " + sentBy.getName(), MessageType.ERROR);
        }
        else
        {
            String[] splits = commandDesc.split(" ");
            String uname = splits[0];
            if(splits.length < 2 || uname.isEmpty())
            {
                MessagingUtils.sendSystemMessage(sentBy, "Incorrect syntax : '/new <username> <password>", MessageType.ERROR);
                return;
            }
            if(ChatServer.getData().exists(uname))
            {
                MessagingUtils.sendSystemMessage(sentBy, "An user has already registered that name!", MessageType.ERROR);
                return;
            }
            Pattern p = Pattern.compile("[^a-zA-Z0-9]");
            boolean incorrect = p.matcher(uname).find();
            if(incorrect)
            {
                MessagingUtils.sendSystemMessage(sentBy, "Your username can only contain regular letters (a-z A-Z) and numbers (0-9)!", MessageType.ERROR);
                return;
            }
            try
            {
                String pw = commandDesc.substring(commandDesc.indexOf(' ', commandDesc.indexOf(' ' + 1)) + 1);
                if(pw.isEmpty())
                {
                    MessagingUtils.sendSystemMessage(sentBy, "Your password cannot be empty", MessageType.ERROR);
                    return;
                }
                if(pw.length() < 8)
                {
                    MessagingUtils.sendSystemMessage(sentBy, "Your password is too weak, it must be at least 8 characters long", MessageType.ERROR);
                    return;
                }
                byte[] hash = ChatUser.sha256(pw);
                ChatUser user = ChatServer.getData().create(uname, hash);
                if(user != null)
                {
                    MessagingUtils.sendSystemMessage(sentBy, "Your account was created with the name '" + uname + "'! You can now log in.", MessageType.SUCCESS);
                    new Thread(() ->
                    {
                        try
                        {
                            ChatServer.getData().save();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }).start();
                }
                else
                    MessagingUtils.sendSystemMessage(sentBy, "There was an error while processing your request. Please try again.", MessageType.ERROR);
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
        return 0;
    }

}
