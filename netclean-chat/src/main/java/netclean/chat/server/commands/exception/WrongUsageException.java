package netclean.chat.server.commands.exception;

import netclean.chat.server.commands.CommandContext;

public class WrongUsageException extends CommandException
{

    public WrongUsageException(CommandContext context)
    {
        super(context);
    }

    public WrongUsageException(String message, CommandContext context)
    {
        super(message, context);
    }

    public WrongUsageException(String message, Throwable cause, CommandContext context)
    {
        super(message, cause, context);
    }

    public WrongUsageException(Throwable cause, CommandContext context)
    {
        super(cause, context);
    }

}
