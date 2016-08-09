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
 * implements a Client peer, even though the usage of NCClient is much more
 * recommended for that case.
 * 
 * @author utybo
 *
 */
public class NCPeer
{
    /**
     * Users can set properties on the peer if they do not wish to wrap them in
     * their implementation.
     */
    private HashMap<String, Object> userProperties = new HashMap<>();

    /**
     * The queue for all output data
     */
    private ArrayBlockingQueue<byte[]> outputQueue = new ArrayBlockingQueue<>(500);

    /**
     * The actual socket
     */
    protected Socket socket;

    /**
     * Input stream used by the {@link InputThread}
     */
    private DataInputStream in;

    /**
     * Output stream used by the {@link OutputThread}
     */
    private DataOutputStream out;

    /**
     * A thread wrapping the {@link InputThread}
     */
    private Thread inputThread;

    /**
     * A thread wrapping the {@link OutputThread}
     */
    private Thread outputThread;

    /**
     * The listener that is called on multiple occasions
     */
    protected PeerListener listener;

    /**
     * Basic constructor for a NCPeer. <b>If you wish to build the client side
     * of your implementation, use {@link NCClient} instead.</b>
     * 
     * @param s
     *            The socket this NCPeer will wrap.
     * @throws IOException
     */
    public NCPeer(Socket s) throws IOException
    {
        socket = s;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }

    /**
     * Set this peer's listener
     * 
     * @param listener
     */
    public void setListener(PeerListener listener)
    {
        this.listener = listener;
    }

    /**
     * Start the peer. This does not start the socket, but initiates the
     * threads.
     */
    public void start()
    {
        setupThread();
    }

    /**
     * Create and start the threads.
     */
    private void setupThread()
    {
        inputThread = new Thread(new InputThread());
        outputThread = new Thread(new OutputThread());
        outputThread.start();
        inputThread.start();
    }

    /**
     * Send the byte array to the other end of the peer. The byte array is put
     * in the queue and this method returns immediately.
     * 
     * @param bytes
     */
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

    /**
     * Set a user property
     * 
     * @param identifier
     *            The identifier
     * @param property
     *            The property to set
     */
    public void setUserProperty(String identifier, Object property)
    {
        userProperties.put(identifier, property);
    }

    /**
     * 
     * @param identifier
     *            The identifier used with
     *            {@link #setUserProperty(String, Object)}
     * @return the previously set property
     */
    public Object getUserProperty(String identifier)
    {
        return userProperties.get(identifier);
    }

    /**
     * Stop this peer. Closes both input and output stream and terminates the
     * socket's connection. Data that was not transferred will be lost.
     * 
     * @throws IOException
     */
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
