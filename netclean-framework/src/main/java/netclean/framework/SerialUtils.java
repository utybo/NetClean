package netclean.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility methods for using Serialization
 * 
 * @author utybo
 *
 */
public class SerialUtils
{
    /**
     * Turns an object into a byte array
     * 
     * @param o
     * @return
     */
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

    /**
     * Turns a byte array into an object
     * @param received
     * @return
     * @throws ClassNotFoundException
     */
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
