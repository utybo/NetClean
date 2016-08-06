package netclean;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NCClient extends NCPeer
{
    public NCClient(String host, int port, PeerListener pl) throws UnknownHostException, IOException
    {
        super(new Socket(host, port));
        setListener(pl);
        super.start();
    }
}
