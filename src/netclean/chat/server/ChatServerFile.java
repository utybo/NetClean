package netclean.chat.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

public class ChatServerFile
{
    private ArrayList<ChatUser> users = new ArrayList<>();

    public ChatUser create(String uname, byte[] hash)
    {
        for(ChatUser cu : users)
        {
            if(cu.getUsername().equals(uname))
            {
                return null;
            }
        }
        ChatUser cu = new ChatUser(uname, hash);
        if(users.size() == 0)
            cu.setPermLevel(PermissionLevels.ADMIN);
        users.add(cu);
        return cu;
    }

    public ChatUser match(String uname, byte[] hash)
    {
        ChatUser user = null;
        for(ChatUser cu : users)
        {
            if(cu.getUsername().equals(uname))
            {
                user = cu;
                break;
            }
        }
        if(user == null)
            return null;
        if(Arrays.equals(hash, user.getPwHash()))
            return user;
        else
            return null;
    }

    public boolean exists(String uname)
    {
        for(ChatUser cu : users)
        {
            if(cu.getUsername().equals(uname))
                return true;
        }
        return false;
    }

    private transient File file;

    public void setFile(File f)
    {
        file = f;
    }
    
    public synchronized void save() throws IOException
    {
        FileWriter fw = new FileWriter(file);
        new Gson().toJson(this, fw);
        fw.flush();
        fw.close();
    }
}
