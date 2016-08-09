package netclean.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * A server. Each server will run 1 thread for accepting connections, and 2
 * threads per peer (one for input, one for output).
 * <p>
 * The server does not handle peers - it merely accepts connections. It does
 * <b>not</b> keep track of connected users.
 * <p>
 * Depending on how you wish to build your application, you can either provide a
 * default {@link PeerListener} that will be used for <i>all</i> the peers, or
 * handle the peers on a case-by-case model using
 * {@link ServerListener#supportsListenerCreation()} and
 * {@link ServerListener#createPeerListener(NCPeer)}. If you use the second
 * case, you must provide a <code>null</code> default PeerListener.
 * 
 * @author utybo
 * @see NCPeer
 */
public class NCServer
{
    /**
     * The port this server will run on
     */
    private final int port;

    /**
     * The actual socket
     */
    private ServerSocket serverSocket;

    /**
     * If this server is running. <font color=red>[! UNTESTED/UNIMPLEMENTED !]</font>
     */
    @SuppressWarnings("unused")
    private volatile boolean running;

    /**
     * The thread that accepts all the connections
     */
    private Thread acceptThread;

    /**
     * The ServerListener
     */
    private ServerListener listener;

    /**
     * The default peer listener, if the ServerListener does not support
     * PeerListener creation
     */
    private PeerListener defaultPeerListener;

    /**
     * Thread for accepting peers
     * 
     * @author utybo
     *
     */
    private class AcceptServerThread implements Runnable
    {
        @Override
        public void run()
        {
            while(!serverSocket.isClosed())
            {
                try
                {
                    Socket clientSocket = serverSocket.accept();
                    addClient(clientSocket);
                }
                catch(IOException e)
                {
                    listener.exceptionThrown(e, null);
                    continue;
                }
            }
        }

    }

    /**
     * Initializes the server with the port. This does NOT start the server, you
     * need to call {@link #start()}
     * 
     * @param port
     */
    public NCServer(int port)
    {
        this.port = port;
    }

    /**
     * Starts the server
     * 
     * @param listener
     *            The listener
     * @param defaultPeerListener
     *            The default peer listener. This will be used if
     *            {@link ServerListener#supportsListenerCreation()} returns
     *            false
     * @throws IOException
     */
    public synchronized void start(ServerListener listener, PeerListener defaultPeerListener) throws IOException
    {
        this.listener = listener;
        if(listener.supportsListenerCreation())
        {
            if(defaultPeerListener != null)
                throw new IllegalStateException("If the ServerListener SUPPORTS listener creation, the default PeerListener MUST be null");
        }
        else
        {
            this.defaultPeerListener = Objects.requireNonNull(defaultPeerListener, "If the ServerListener does NOT support listener creation, the default PeerListener MUST NOT be null");
        }
        running = true;

        if(serverSocket == null)
        {
            serverSocket = new ServerSocket(port);
        }

        acceptThread = new Thread(new AcceptServerThread());
        acceptThread.start();
    }

    /**
     * Stops the server.
     * <p>
     * All the {@link NCPeer} will crash, properly cleaning up and calling their
     * respective {@link PeerListener#onConnectionInterrupted(NCPeer)}. The
     * crash handling methods may also be triggered - they should be ignored as
     * soon as this method is called.
     * 
     * @throws IOException
     */
    public synchronized void stop() throws IOException
    {
        acceptThread.interrupt();
        running = false;
        serverSocket.close();
    }

    /**
     * Executes a new thread to handle the creation of peers and listeners. This
     * is done on a new thread to avoid not being able to accept new
     * connections.
     * 
     * @param clientSocket
     * @throws IOException
     */
    private void addClient(Socket clientSocket) throws IOException
    {
        new Thread(() ->
        {
            try
            {
                NCPeer peer = new NCPeer(clientSocket);
                peer.setListener(defaultPeerListener == null ? listener.createPeerListener(peer) : defaultPeerListener);
                peer.start();
                listener.newClient(peer);
            }
            catch(IOException e)
            {
                listener.exceptionThrown(e, null);
            }
        }).start();;
    }

}
