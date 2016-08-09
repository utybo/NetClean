package netclean.chat.server.commands.exception;

import netclean.chat.server.commands.CommandContext;

public class NotEnoughPermissionException extends CommandException
{

    public NotEnoughPermissionException(CommandContext context)
    {
        super(context);
    }

    public NotEnoughPermissionException(String message, CommandContext context)
    {
        super(message, context);
    }

    public NotEnoughPermissionException(String message, Throwable cause, CommandContext context)
    {
        super(message, cause, context);
    }

    public NotEnoughPermissionException(Throwable cause, CommandContext context)
    {
        super(cause, context);
    }

}
