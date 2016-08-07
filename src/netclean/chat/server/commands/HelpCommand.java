package netclean.chat.server.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import netclean.chat.packets.servertoclient.MessageType;
import netclean.chat.server.ChatServer;
import netclean.chat.server.UserConnection;

public class HelpCommand implements Command
{

    @Override
    public void exec(String commandDesc, UserConnection sentBy)
    {
        System.out.println(commandDesc);
        String[] strings = commandDesc.split(" ");
        if(commandDesc.isEmpty())
        {
            Map<String, Command> commands = ChatServer.commandsRegistry;
            Set<String> rawSet = ChatServer.commandsRegistry.keySet();
            List<Command> uniqueCommands = new ArrayList<>();
            Set<String> classNames = new TreeSet<>();
            for(Iterator<String> iterator = rawSet.iterator(); iterator.hasNext();)
            {
                String string = (String)iterator.next();
                if(classNames.contains(commands.get(string).getClass().getName()))
                {
                    continue;
                }
                else
                {
                    if(commands.get(string).minimumPermLevel() <= sentBy.getUser().getPermLevel())
                    {
                        uniqueCommands.add(commands.get(string));
                    }
                    classNames.add(commands.get(string).getClass().getName());
                }
            }
            uniqueCommands.sort(new Comparator<Command>()
            {
                @Override
                public int compare(Command o1, Command o2)
                {
                    return o1.getPreferredCommand().compareTo(o2.getPreferredCommand());
                }
            });

            String output = "";
            for(Command c : uniqueCommands)
            {
                output += c.getPreferredCommand() + " : " + c.getShortHelp() + "\n";
            }
            MessagingUtils.sendSystemMessage(sentBy, output, MessageType.MESSAGE);
        }
        else if(strings.length == 1)
        {
            Command c = ChatServer.commandsRegistry.get(strings[0]);
            if(c == null)
            {
                MessagingUtils.sendSystemMessage(sentBy, "Unknown command : " + strings[0], MessageType.ERROR);
            }
            else
            {
                String s = "";
                s += "Help -- " + c.getPreferredCommand() + "\n";
                s += c.getLongHelp() + "\n";
                s += "Syntax :\n    " + c.getSyntax();
                MessagingUtils.sendSystemMessage(sentBy, s, MessageType.MESSAGE);
            }

        }
        else
        {
            MessagingUtils.sendSystemMessage(sentBy, "Syntax : /help [command]", MessageType.ERROR);
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
        return "help";
    }

    @Override
    public String getSyntax()
    {
        return "/help [command]";
    }

    @Override
    public String getShortHelp()
    {
        return "Returns a list of available commands, or details on one command";
    }

}
