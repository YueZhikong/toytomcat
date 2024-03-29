package org.yuezhikong.toytomcat.catalina;

import org.yuezhikong.toytomcat.util.ServerXMLUtil;

import java.util.List;

public class Engine {
    private String defaultHost;
    private List<Host> hosts;
    private Service service;
    public Engine(Service service) {
        this.service = service;
        this.defaultHost = ServerXMLUtil.getEngineDefaultHost();
        this.hosts = ServerXMLUtil.getHosts(this);
        checkDefault();
    }

    private void checkDefault(){
        if (null==getDefaultHost()){
            throw new RuntimeException("the defaultHost" + defaultHost + " does not exist!");
        }
    }

    public Host getDefaultHost(){
        for (Host host:hosts){
            if (host.getName().equals(defaultHost)){
                return host;
            }
        }
        return null;
    }
}
