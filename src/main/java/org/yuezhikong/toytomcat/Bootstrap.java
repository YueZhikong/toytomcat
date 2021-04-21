package org.yuezhikong.toytomcat;


import cn.hutool.core.util.NetUtil;
import org.yuezhikong.toytomcat.http.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {
    public static void main(String[] args) {
        try {
            int port = 18080;
            if (!NetUtil.isUsableLocalPort(port)){
                System.out.println(port+"端口已经被占用了，排查并关闭本端口");
                return;
            }
            ServerSocket ss = new ServerSocket(port);
            while (true){
                Socket s = ss.accept();
                Request request = new Request(s);
                System.out.println("浏览器的输入信息："+request.getRequestString());
                OutputStream os = s.getOutputStream();
                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
                String responseString = "Hello DIY Tomcat from how2j.cn";
                responseString = response_head + responseString;
                os.write(responseString.getBytes());
                os.flush();
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
