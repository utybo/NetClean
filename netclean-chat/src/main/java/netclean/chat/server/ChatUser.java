package netclean.chat.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ChatUser
{
    private volatile String username;
    private volatile byte[] passHash;

    private volatile int permLevel = ChatServer.getDefaultPermLevel();

    public ChatUser(String username, byte[] pw)
    {
        this.username = username;
        passHash = pw;
    }

    public String getUsername()
    {
        return username;
    }

    public byte[] getPwHash()
    {
        return Arrays.copyOf(passHash, passHash.length);
    }

    public static byte[] sha256(String s)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(s.getBytes("UTF-8"));
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public void setPermLevel(int level)
    {
        permLevel = level;
    }

    public int getPermLevel()
    {
        return permLevel;
    }
}
