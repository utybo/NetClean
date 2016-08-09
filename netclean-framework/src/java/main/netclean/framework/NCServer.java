package netclean.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * A server. Each server will run 1 thread for accepting connections, and 2
 * threads per peer (one for input, one for output).
 * 
 * @author utybo
 *
 */
public class NCServer
{
    /**
     * The port this server runs on
     */
    private final int port;

    /**
     * The actual socket
     */
    private ServerSocket serverSocket;

    /**
     * If this server is running [! UNIMPLEMENTED !]
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

    public synchronized void stop() throws IOException
    {
        acceptThread.interrupt();
        running = false;
        serverSocket.close();
    }

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
