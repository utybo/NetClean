package netclean.chat.server.commands;

import java.io.IOException;
import java.util.regex.Pattern;

import netclean.chat.common.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.ChatUser;
import netclean.chat.server.UserConnection;
import netclean.chat.server.commands.exception.CommandException;
import netclean.chat.server.commands.exception.ExecutionException;
import netclean.chat.server.commands.exception.WrongUsageException;

public class NewCommand implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy, CommandContext context) throws WrongUsageException, ExecutionException, CommandException
    {
        if(sentBy.isAuth())
        {
            throw new CommandException("You are already authenticated as " + sentBy.getName(), context);
        }
        else
        {
            String[] splits = commandDesc.split(" ");
            String uname = splits[0];
            if(splits.length < 2 || uname.isEmpty())
            {
                throw new WrongUsageException(context);
            }
            if(ChatServer.getData().exists(uname))
            {
                throw new CommandException("An user has already registered that name!", context);
            }
            Pattern p = Pattern.compile("[^a-zA-Z0-9]");
            boolean incorrect = p.matcher(uname).find();
            if(incorrect)
            {
                throw new CommandException("Your username can only contain regular letters (a-z A-Z) and numbers (0-9)!", context);
            }
            try
            {
                String pw = commandDesc.substring(commandDesc.indexOf(' ', commandDesc.indexOf(' ' + 1)) + 1);
                if(pw.isEmpty())
                {
                    throw new CommandException("Your password cannot be empty", context);
                }
                if(pw.length() < 8)
                {
                    throw new CommandException("Your password is too weak, it must be at least 8 characters long", context);
                }
                byte[] hash = ChatUser.sha256(pw);
                ChatUser user = ChatServer.getData().create(uname, hash);
                if(user != null)
                {
                    MessagingUtils.sendSystemMessage(sentBy, "Your account was created with the name '" + uname + "'! You can now log in.", MessageType.SUCCESS, context);
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
                    throw new ExecutionException("There was an error while processing your request. Please try again.", context);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                throw new WrongUsageException(context);
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

    @Override
    public String getPreferredCommand()
    {
        return "new";
    }

    @Override
    public String getSyntax()
    {
        return "/new <username> <password>";
    }

    @Override
    public String getShortHelp()
    {
        return "Creates an account";
    }

}
