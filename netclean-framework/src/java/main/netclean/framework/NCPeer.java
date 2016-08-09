package netclean.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A peer. This can be one of the server's peer or the client's peer. This class
 * implements a Client peer, even though the usage of NCClient is more
 * recommended for that case, just for clarity.
 * 
 * @author utybo
 *
 */
public class NCPeer
{
    private HashMap<String, Object> userProperties = new HashMap<>();

    private ArrayBlockingQueue<byte[]> outputQueue = new ArrayBlockingQueue<>(500);

    protected Socket socket;

    private DataInputStream in;

    private DataOutputStream out;

    private Thread inputThread;

    private Thread outputThread;

    protected PeerListener listener;

    public NCPeer(Socket s) throws IOException
    {
        socket = s;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }

    public void setListener(PeerListener listener)
    {
        this.listener = listener;
    }
    
    public void start()
    {
        setupThread();
    }

    private void setupThread()
    {
        inputThread = new Thread(new InputThread());
        outputThread = new Thread(new OutputThread());
        outputThread.start();
        inputThread.start();
    }

    public void send(byte[] bytes)
    {
        try
        {
            outputQueue.put(bytes);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void setUserProperty(String identifier, Object property)
    {
        userProperties.put(identifier, property);
    }

    public Object getUserProperty(String identifier)
    {
        return userProperties.get(identifier);
    }

    public void stop() throws IOException
    {
        try
        {
            out.close();
        }
        catch(Exception e)
        {}
        try
        {
            in.close();
        }
        catch(Exception e)
        {}
        socket.close();
        listener.onConnectionInterrupted(this);
    }

    private class InputThread implements Runnable
    {
        @Override
        public void run()
        {
            while(!socket.isClosed() && !socket.isInputShutdown())
            {
                try
                {
                    int length = in.readInt();
                    byte[] bytes = new byte[length];
                    in.readFully(bytes, 0, length);
                    listener.onDataReceived(bytes, NCPeer.this);
                }
                catch(EOFException e)
                {
                    // The connection was terminated
                    listener.onConnectionInterrupted(NCPeer.this);
                    break;
                }
                catch(IOException e)
                {
                    listener.onInputException(e);
                    break;
                }

            }
        }
    }

    private class OutputThread implements Runnable
    {
        @Override
        public void run()
        {
            while(!socket.isClosed() && !socket.isOutputShutdown())
            {
                try
                {
                    byte[] data = outputQueue.take();
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();
                }
                catch(InterruptedException e)
                {
                    // Should not happen 
                }
                catch(EOFException e)
                {
                    // The connection was terminated
                    listener.onConnectionInterrupted(NCPeer.this);
                    break;
                }
                catch(IOException e)
                {
                    listener.onOutputException(e);
                    break;
                }
            }
        }

    }
}
