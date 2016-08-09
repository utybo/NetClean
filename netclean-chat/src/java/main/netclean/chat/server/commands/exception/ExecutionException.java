package netclean.chat.server.commands.exception;

import netclean.chat.server.commands.CommandContext;

public class ExecutionException extends CommandException
{

    public ExecutionException(CommandContext context)
    {
        super(context);
    }

    public ExecutionException(String message, CommandContext context)
    {
        super(message, context);
    }

    public ExecutionException(String message, Throwable cause, CommandContext context)
    {
        super(message, cause, context);
    }

    public ExecutionException(Throwable cause, CommandContext context)
    {
        super(cause, context);
    }

}
