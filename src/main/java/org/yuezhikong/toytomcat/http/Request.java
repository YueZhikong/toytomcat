package org.yuezhikong.toytomcat.http;

import cn.hutool.core.util.StrUtil;
import org.yuezhikong.toytomcat.Bootstrap;
import org.yuezhikong.toytomcat.catalina.Context;
import org.yuezhikong.toytomcat.catalina.Engine;
import org.yuezhikong.toytomcat.catalina.Host;
import org.yuezhikong.toytomcat.catalina.Service;
import org.yuezhikong.toytomcat.util.MiniBrowser;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class Request extends BaseRequest {

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Service service;
    public Request(Socket socket,Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        if(!"/".equals(context.getPath()))
            uri = StrUtil.removePrefix(uri, context.getPath());

    }

    private void parseContext() {
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(path);
        if (null == context){
            context = engine.getDefaultHost().getContext("/");
        }
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is,false);
        requestString = new String(bytes, "utf-8");
    }

    private void parseUri() {
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    public Context getContext() {
        return context;
    }


    public String getUri() {
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }


}
