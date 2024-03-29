package org.yuezhikong.toytomcat.util;
import static org.yuezhikong.toytomcat.util.Constant.webXmlFile;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yuezhikong.toytomcat.catalina.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WebXMLUtil {
    private static Map<String,String> mimeTypeMapping = new HashMap<>();

    public static synchronized String getMimeType(String extName){
        if (mimeTypeMapping.isEmpty()){
            initMimeType();
        }

        String mimeType = mimeTypeMapping.get(extName);

        if (null == mimeType){
            return "text/html";
        }
        return mimeType;
    }
    private static void initMimeType(){
        String xml = FileUtil.readUtf8String(webXmlFile);
        Document d = Jsoup.parse(xml);

        Elements es = d.select("mime-mapping");
        for (Element e:es){
            String extName = e.select("extension").first().text();
            String mimeType = e.select("mime-type").first().text();
            mimeTypeMapping.put(extName,mimeType);
        }
    }

    public static String getWelcomeFile(Context context){
        String xml = FileUtil.readUtf8String(webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("welcome-file");
        for (Element e:es){
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(),welcomeFileName);
            if (f.exists()){
                return f.getName();
            }
        }
        return "index.html";
    }
}
