package netclean.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialUtils
{
    public static byte[] objectToByteArray(Object o)
    {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos))
        {
            oos.writeObject(o);
            oos.flush();
            return baos.toByteArray();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Object byteArrayToObject(byte[] received) throws ClassNotFoundException
    {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(received); ObjectInputStream ois = new ObjectInputStream(bais))
        {
            return ois.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
