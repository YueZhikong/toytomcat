package org.yuezhikong.toytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import org.yuezhikong.toytomcat.http.Request;
import org.yuezhikong.toytomcat.http.Response;
import org.yuezhikong.toytomcat.util.Constant;
import org.yuezhikong.toytomcat.util.WebXMLUtil;
import org.yuezhikong.toytomcat.webappservlet.HelloServlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
    public void execute(Socket s, Request request, Response response){
        try {
            String uri = request.getUri();
            if(null==uri)
                return;

            Context context = request.getContext();
            String servletClassName = context.getServletClassName(uri);

            if(null!=servletClassName){
                Object servletObject = ReflectUtil.newInstance(servletClassName);
                ReflectUtil.invoke(servletObject, "doGet", request, response);
            }
            else{
                if("/500.html".equals(uri)){
                    throw new Exception("this is a deliberately created exception");
                }
                else{
                    if("/".equals(uri))
                        uri = WebXMLUtil.getWelcomeFile(request.getContext());

                    String fileName = StrUtil.removePrefix(uri, "/");
                    File file = FileUtil.file(context.getDocBase(),fileName);

                    if(file.exists()){
                        String extName = FileUtil.extName(file);
                        String mimeType = WebXMLUtil.getMimeType(extName);
                        response.setContentType(mimeType);

                        byte body[] = FileUtil.readBytes(file);
                        response.setBody(body);

                        if(fileName.equals("timeConsume.html"))
                            ThreadUtil.sleep(1000);
                    }
                    else{
                        handle404(s, uri);
                        return;
                    }
                }
            }
            handle200(s, response);
        } catch (Exception e) {
            LogFactory.get().error(e);
            handle500(s,e);
        }
        finally{
            try {
                if(!s.isClosed())
                    s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
    }

    private void handle404(Socket s, String uri) throws IOException {
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constant.textFormat_404, uri, uri);
        responseText = Constant.response_head_404 + responseText;
        byte[] responseByte = responseText.getBytes("utf-8");
        os.write(responseByte);
    }

    private void handle500(Socket s, Exception e) {
        try {
            OutputStream os = s.getOutputStream();
            StackTraceElement stes[] = e.getStackTrace();
            StringBuffer sb = new StringBuffer();
            sb.append(e.toString());
            sb.append("\r\n");
            for (StackTraceElement ste : stes) {
                sb.append("\t");
                sb.append(ste.toString());
                sb.append("\r\n");
            }

            String msg = e.getMessage();

            if (null != msg && msg.length() > 20)
                msg = msg.substring(0, 19);

            String text = StrUtil.format(Constant.textFormat_500, msg, e.toString(), sb.toString());
            text = Constant.response_head_500 + text;
            byte[] responseBytes = text.getBytes("utf-8");
            os.write(responseBytes);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
