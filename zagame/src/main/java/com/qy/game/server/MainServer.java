package com.qy.game.server;

import com.qy.game.remote.iservice.IService;
import com.qy.game.remote.iservice.impl.IServiceImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ResourceBundle;

public class MainServer implements ServletContextListener {

    private static IService server = null;

    public static void main(String[] args) {

        MainServer ms = new MainServer();
        ms.startServer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

//        startServer();
//        System.out.println("已开启远程服务");
        // 开启定时任务
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleWithFixedDelay(new GameTask(), 0, 1, TimeUnit.MINUTES);
    }

    /**
     * 开启远程服务
     */
    private void startServer() {

        try {
            //获取配置文件config.properties
            String server_ip = ResourceBundle.getBundle("config").getString(
                    "server_ip");
            String server_port = ResourceBundle.getBundle("config").getString(
                    "server_port");

            System.setProperty("java.rmi.server.hostname", server_ip);

            server = new IServiceImpl();

            //System.setSecurityManager(new java.rmi.RMISecurityManager());

            //端口绑定
            LocateRegistry.createRegistry(Integer.valueOf(server_port));
            Naming.rebind("rmi://" + server_ip
                    + ":" + server_port + "/sysService", server);

            System.out.println(">>>>>>远程RMIServer绑定【" + server_ip + ":" + server_port + "】成功！");

        } catch (RemoteException e) {

            System.out.println("创建远程对象发生异常！");

            e.printStackTrace();

        } catch (MalformedURLException e) {

            System.out.println("重复绑定发生异常！");

            e.printStackTrace();

        }

    }
}
