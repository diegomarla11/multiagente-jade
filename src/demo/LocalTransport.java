package demo;

import jade.core.Profile;
import jade.imtp.leap.ICP;
import jade.imtp.leap.ICPException;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.imtp.leap.TransportProtocol;
import jade.mtp.TransportAddress;

/** JADE routes local messages internally. This single-container demo has no remote peers. */
public class LocalTransport implements ICP {
    public TransportAddress activate(Listener listener, String address, Profile profile) {
        return getProtocol().buildAddress("127.0.0.1", "1099", null, null);
    }

    public TransportProtocol getProtocol() {
        return JICPProtocol.getInstance();
    }

    public byte[] deliverCommand(TransportAddress address, byte[] command, boolean flush) throws ICPException {
        throw new ICPException("The demo only supports agents in its own container.");
    }

    public void deactivate() {}
}
