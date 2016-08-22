package netclean.chat.common;

public interface PermissionLevels
{
    /**
     * A banned person cannot execute any command, including /r
     */
    public static final int BANNED = -2;

    /**
     * A ghost is a viewer who does not trigger connected and disconnected info,
     * and is not included in the '/r users' list
     */
    public static final int GHOST = -1;

    /**
     * A viewer can view conversations but cannot interact.
     */
    public static final int VIEWER = 0;

    /**
     * A restricted talker cannot use /w or /me
     */
    public static final int RESTRICTED = 1;

    /**
     * A talker can view conversations and interact with people during
     * unrestricted time
     */
    public static final int TALKER = 2;

    /**
     * A VIP is a talker who can talk during unrestricted time
     */
    public static final int VIP = 3;

    /**
     * An Elder can /kick others and has extra props. (+VIP advantages)
     */
    public static final int ELDER = 4;

    /**
     * A mod can manage other users permissions, from BANNED to MOD, can ban
     * people (/ban), launch restricted times (/restrict on) plus everything
     * Elders can do
     */
    public static final int MOD = 5;

    /**
     * An admin can do anything
     */
    public static final int ADMIN = 6;

    public static String getPrefix(int i)
    {
        String prefix = "";
        switch(i)
        {
        case PermissionLevels.BANNED:
            prefix = "|Banned|";
            break;
        case PermissionLevels.GHOST:
            prefix = "|Ghost|";
            break;
        case PermissionLevels.VIEWER:
            prefix = "(Viewer) ";
            break;
        case PermissionLevels.RESTRICTED:
            prefix = "(Restricted) ";
            break;
        case PermissionLevels.TALKER:
            prefix = "";
            break;
        case PermissionLevels.VIP:
            prefix = "+";
            break;
        case PermissionLevels.ELDER:
            prefix = "%";
            break;
        case PermissionLevels.MOD:
            prefix = "@";
            break;
        case PermissionLevels.ADMIN:
            prefix = "¤";
            break;
        }
        return prefix;
    }

    public static String getName(int i)
    {
        String name = "";
        switch(i)
        {
        case PermissionLevels.BANNED:
            name = "Banned";
            break;
        case PermissionLevels.GHOST:
            name = "Ghost";
            break;
        case PermissionLevels.VIEWER:
            name = "Viewer";
            break;
        case PermissionLevels.RESTRICTED:
            name = "Restricted user";
            break;
        case PermissionLevels.TALKER:
            name = "Basic user";
            break;
        case PermissionLevels.VIP:
            name = "VIP";
            break;
        case PermissionLevels.ELDER:
            name = "Elder";
            break;
        case PermissionLevels.MOD:
            name = "Moderator";
            break;
        case PermissionLevels.ADMIN:
            name = "Admin";
            break;
        }
        return name;
    }
}
