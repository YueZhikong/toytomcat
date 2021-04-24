package org.yuezhikong.toytomcat.catalina;

import org.yuezhikong.toytomcat.util.ServerXMLUtil;

public class Service {
    private String name;
    private Engine engine;
    private Server server;
    public Service(Server server) {
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
        this.server = server;
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }
}