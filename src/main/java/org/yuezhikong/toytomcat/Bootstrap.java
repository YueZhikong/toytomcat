package org.yuezhikong.toytomcat;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import org.yuezhikong.toytomcat.catalina.Context;
import org.yuezhikong.toytomcat.http.Request;
import org.yuezhikong.toytomcat.http.Response;
import org.yuezhikong.toytomcat.util.Constant;
import org.yuezhikong.toytomcat.util.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 月氏空
 */
public class Bootstrap {
    public static Map<String, Context> contextMap = new HashMap<>();
    public static void main(String[] args) {
        try {
            logJVM();

            scanContextsOnWebAppsFolder();

            int port = 18080;
            if (!NetUtil.isUsableLocalPort(port)){
                System.out.println(port+"端口已经被占用了，排查并关闭本端口");
                return;
            }
            ServerSocket ss = new ServerSocket(port);
            while (true){
                Socket s = ss.accept();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s);
                            Response response = new Response();
                            String uri = request.getUri();
                            if (null==uri){
                                return;
                            }
                            System.out.println("uri:"+uri);

                            Context context = request.getContext();
                            if ("/".equals(uri)){
                                String html = "Hello DIY Tomcat from how2j.cn";
                                response.getWriter().println(html);
                            }
                            else {
                                String fileName = StrUtil.removePrefix(uri,"/");
                                File file = FileUtil.file(Constant.rootFolder,fileName);
                                if (file.exists()){
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getWriter().println(fileContent);
                                    if(fileName.equals("timeConsume.html")){
                                        ThreadUtil.sleep(1000);
                                    }
                                }
                                else {
                                    response.getWriter().println("File Not Found");
                                }
                            }
                            handle200(s,response);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanContextsOnWebAppsFolder(){
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder:folders){
            if (!folder.isDirectory())
                continue;
            loadContext(folder);
        }
    }
    private static void loadContext(File folder){
        String path = folder.getName();
        if ("ROOT".equals(path)){
            path = "/";
        }
        else {
            path = "/"+path;
        }
        String docBase = folder.getAbsolutePath();
        Context context = new Context(path,docBase);

        contextMap.put(context.getPath(),context);
    }
    private static void logJVM(){
        Map<String,String> infos = new LinkedHashMap<>();
        infos.put("Server version", "How2J DiyTomcat/1.0.1");
        infos.put("Server built", "2020-04-08 10:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        for (Map.Entry<String,String> entry:infos.entrySet()){
            LogFactory.get().info(entry.getKey()+":\t\t"+entry.getValue());
        }
    }
    private static void handle200(Socket s,Response response)throws IOException{
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText,contentType);
        byte[] head = headText.getBytes();
        byte[] body = response.getBody();
        byte[] responseBytes = new byte[head.length+body.length];
        ArrayUtil.copy(head,0,responseBytes,0,head.length);
        ArrayUtil.copy(body,0,responseBytes,head.length,body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();
    }
}
