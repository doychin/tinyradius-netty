package com.globalreachtech.tinyradius.legacy;

import com.globalreachtech.tinyradius.packet.AccountingRequest;
import com.globalreachtech.tinyradius.packet.RadiusPacket;
import com.globalreachtech.tinyradius.util.RadiusEndpoint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Test proxy server.
 * Listens on localhost:1812 and localhost:1813. Proxies every access clientRequest
 * to localhost:10000 and every accounting clientRequest to localhost:10001.
 * You can use TestClient to ask this TestProxy and TestServer
 * with the parameters 10000 and 10001 as the target server.
 * Uses "testing123" as the shared secret for the communication with the
 * target server (localhost:10000/localhost:10001) and "proxytest" as the
 * shared secret for the communication with connecting clients.
 */
public class TestProxy extends RadiusProxy {

    public RadiusEndpoint getProxyServer(RadiusPacket packet,
                                         RadiusEndpoint client) {
        // always proxy
        try {
            InetAddress address = InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
            int port = 10000;
            if (packet instanceof AccountingRequest)
                port = 10001;
            return new RadiusEndpoint(new InetSocketAddress(address, port), "testing123");
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            return null;
        }
    }

    public String getSharedSecret(InetSocketAddress client) {
        if (client.getPort() == 10000 || client.getPort() == 10001)
            return "testing123";
        else if (client.getAddress().getHostAddress().equals("127.0.0.1"))
            return "proxytest";
        else
            return null;
    }

    public String getUserPassword(String userName) {
        // not used because every clientRequest is proxied
        return null;
    }

    public static void main(String[] args) {
        new TestProxy().start(true, true, true);
    }

}
