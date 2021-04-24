package org.yuezhikong.toytomcat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import org.yuezhikong.toytomcat.catalina.*;
import org.yuezhikong.toytomcat.http.Request;
import org.yuezhikong.toytomcat.http.Response;
import org.yuezhikong.toytomcat.util.Constant;
import org.yuezhikong.toytomcat.util.ServerXMLUtil;
import org.yuezhikong.toytomcat.util.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Bootstrap {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
