package netclean.chat.server.commands.exception;

import netclean.chat.server.commands.CommandContext;

public class CommandException extends Exception
{
    private final CommandContext context;

    public CommandException(CommandContext context)
    {
        super();
        this.context = context;
    }

    public CommandException(String message, Throwable cause, CommandContext context)
    {
        super(message, cause);
        this.context = context;
    }

    public CommandException(String message, CommandContext context)
    {
        super(message);
        this.context = context;
    }

    public CommandException(Throwable cause, CommandContext context)
    {
        super(cause);
        this.context = context;
    }

}
