package org.yuezhikong.toytomcat.http;

import cn.hutool.core.util.StrUtil;
import org.yuezhikong.toytomcat.Bootstrap;
import org.yuezhikong.toytomcat.catalina.Context;
import org.yuezhikong.toytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author 月氏空
 */
public class Request {
    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    public Request(Socket socket)throws IOException{
        this.socket = socket;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)){
            return;
        }
        parseUri();
    }
    private void parseContext(){
        String path = StrUtil.subBetween(uri,"/","/");
        if (null == path){
            path = "/";
        }
        else {
            path = "/"+path;
        }
        context = Bootstrap.contextMap.get(path);
        if (null == context) {
            context = Bootstrap.contextMap.get("/");
        }
    }
    private void parseHttpRequest() throws IOException{
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, StandardCharsets.UTF_8);
    }

    private void parseUri(){
        String temp;

        temp = StrUtil.subBetween(requestString," "," ");

        if (!StrUtil.contains(temp,'?')){
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp,'?',false);
        uri = temp;
    }

    public String getRequestString() {
        return requestString;
    }

    public String getUri() {
        return uri;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
