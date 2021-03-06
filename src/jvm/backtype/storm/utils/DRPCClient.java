package backtype.storm.utils;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.generated.DRPCRequest;
import backtype.storm.generated.DistributedRPC;
import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransport;

public class DRPCClient implements DistributedRPC.Iface {
    private TTransport conn;
    private DistributedRPC.Client client;
    private String host;
    private int port;    

    public DRPCClient(String host, int port) {
        try {
            this.host = host;
            this.port = port;
            connect();
        } catch(TException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void connect() throws TException {
        conn = new TFramedTransport(new TSocket(host, port));
        client = new DistributedRPC.Client(new TBinaryProtocol(conn));
        conn.open();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }   
    
    public String execute(String func, String args) throws TException, DRPCExecutionException {
        try {
            if(client==null) connect();
            return client.execute(func, args);
        } catch(TException e) {
            client = null;
            throw e;
        } catch(DRPCExecutionException e) {
            client = null;
            throw e;
        }
    }

    public void result(String id, String result) throws TException {
        try {
            if(client==null) connect();
            client.result(id, result);
        } catch(TException e) {
            client = null;
            throw e;
        }
    }

    public DRPCRequest fetchRequest(String func) throws TException {
        try {
            if(client==null) connect();
            return client.fetchRequest(func);
        } catch(TException e) {
            client = null;
            throw e;
        }
    }    

    public void failRequest(String id) throws TException {
        try {
            if(client==null) connect();
            client.failRequest(id);
        } catch(TException e) {
            client = null;
            throw e;
        }
    }

    public void close() {
        conn.close();
    }
}
