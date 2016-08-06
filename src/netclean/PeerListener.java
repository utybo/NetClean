package netclean;

import java.io.IOException;

public interface PeerListener
{
    public void onDataReceived(byte[] received, NCPeer peer);

    public void onConnectionInterrupted(NCPeer ncPeer);

    public default void onInputException(IOException e)
    {
        e.printStackTrace();
    }

    public default void onOutputException(IOException e)
    {
        e.printStackTrace();
    }
}