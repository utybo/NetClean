package netclean.framework;

public interface ServerListener
{
    /**
     * Called whenever an exception is thrown by the server or a client.
     * 
     * @param e
     *            The exception thrown
     * @param onClient
     *            The client, or <code>null</code> if the error was thrown by the server
     */
    public void exceptionThrown(Exception e, NCPeer onClient);

    public void newClient(NCPeer client);
    
    public default boolean supportsListenerCreation()
    {
        return false;
    }
    
    public default PeerListener createPeerListener(NCPeer peer)
    {
        return null;
    }

}
