package netclean.framework;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A subclass of {@link NCPeer} that was made to make Socket creation easy and
 * painless
 * 
 * @author utybo
 *
 */
public class NCClient extends NCPeer
{
    public NCClient(String host, int port, PeerListener pl) throws UnknownHostException, IOException
    {
        super(new Socket(host, port));
        setListener(pl);
        super.start();
    }
}
