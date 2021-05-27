package com.qy.socket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.qy.jms.ProducerService;
import com.qy.model.PumpDao;
import com.qy.openfeign.service.RedisInfoFeignClientService;
import com.qy.util.BaseSqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameMain {

    private static final Logger log = LoggerFactory.getLogger(GameMain.class);

    @Autowired
    private RedisInfoFeignClientService service;

    public static SocketIOServer server;

    @Autowired
    private ProducerService producerService;


    private Destination destination = new Destination() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    };


    public static boolean scheduledProcessing = false;
    public static boolean isRoomTask = false;

    /**
     * 启动socket服务 并同时监听各个事件
     *
     * @param isGameTask
     */
    public void startServer(boolean isGameTask) {
        long time1 = System.currentTimeMillis();
        Map<String, String> map = service.getScoketInfor();
        System.out.println("============GameMain==================");
        System.out.println("socket:" + map);
        System.out.println("============GameMain==================");
        int localPort = Integer.valueOf((String) map.get("socketPort"));
        log.info("SocketIOServer 端口号{}", localPort);
        //添加监听事件
        this.addEventListener(server);
        if (isGameTask) {
            log.info("============================== 紧接,SocketIO服务完成了监听事件的添加  ==============================");
            log.info("==============================[ SOCKET-IO服务启用成功 ]==============================");
            if (!scheduledProcessing) {
                scheduledProcessing = true;
                this.scheduleDeal();
                log.info("=======================================================");
            }
        }
    }

    /**
     * 关闭socket服务
     */
    public void stopServer() {
        try {
            server.stop();
            Configuration configuration = server.getConfiguration();
            int port = configuration.getPort();
            String host = configuration.getHostname();
            log.info("host===========" + host);
            server = null;
            log.info("==============================[ SOCKET-IO服务[" + host + ":" + port + "]已关闭 ]==============================");
        } catch (Exception e) {
        }
    }


    public SocketIOServer getServer() {
        return server;
    }

    /**
     * 配置socket信息
     *
     * @param localPort
     * @return
     */
    private Configuration serverConfig(int localPort) {
        Configuration config = new Configuration();
        config.setPort(localPort);
        config.setWorkerThreads(2);
        config.setMaxFramePayloadLength(1048576);
        config.setMaxHttpContentLength(1048576);
        log.info("============================== 其次,SocketIO 启用本地服务 [(当前主机内网IP):" + localPort + "(必须开放此端口)] ==============================");
        return config;
    }

    //通知redis 执行方法
    private void delRedisSystem() {

    }

    /**
     * 给各个游戏添加监听事件
     *
     * @param server
     */
    private void addEventListener(SocketIOServer server) {

    }

    private void preSelectRoomSetting() {
        String sql = "select base_info from za_gamerooms where status in(0,1) group by room_no";
        JSONArray array = service.getObjectListBySQL(sql, null);
        log.info("查询上次服务器断开所有游戏未结束的房间数量：" + array.size());
        BaseSqlUtil.updateDataByMore("za_gamerooms", new String[]{"status"}, new String[]{"-1"}, (String[]) null, (Object[]) null, " where `status` in(0,1)");
        if (array != null && array.size() != 0) {
            AtomicInteger count = new AtomicInteger();
            for (int i = 0; i < array.size(); i++) {
                if (array.getJSONObject(i) != null && array.getJSONObject(i).containsKey("base_info")) {
                    JSONObject baseInfo = array.getJSONObject(i).getJSONObject("base_info");
                    if (baseInfo != null && baseInfo.containsKey("gameRoomOther")) {
                        JSONObject gameRoomOther = baseInfo.getJSONObject("gameRoomOther");
                        if (gameRoomOther != null) {
                            JSONObject object = new JSONObject();
                            Set<String> set = gameRoomOther.keySet();
                            for (String key : set) {
                                object.put(key, gameRoomOther.get(key));
                            }
                            object.put("base_info", baseInfo);
                            count.addAndGet(1);
                            object.put("roomCount", count);
                            this.producerService.sendMessage(destination, new PumpDao("circle_game_romm_recover", object));
                        }
                    }
                }

            }
        }
    }


    public void scheduleDeal() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("----------------------------------------  启动服务器删除redis和创建房间信息 开始---------------------------------------- ");
            this.delRedisSystem();
            this.preSelectRoomSetting();
            long endTime = System.currentTimeMillis();
            int robot = service.executeUpdateBySQL("UPDATE robot_info SET status = 0 WHERE status != 0 ", (Object[]) null);
            log.info("----------------------------------------  robot 更新状态数量 [{}]---------------------------------------- ", robot);
            log.info("----------------------------------------  启动服务器删除redis、创建房间信息、游戏战绩 结束---------------------------------------- ");
            log.info("----------------------------------------  用时：" + (endTime - startTime) + "毫秒---------------------------------------- ");
        } catch (Exception e) {
            log.info("scheduleDeal [{}]", e);
        }
    }


    /*class SocketTask extends TimerTask {
        SocketTask() {

        }

        public void run() {
            try {
                if (null == GameMain.server) {
                    GameMain.log.warn("socket检测连接---------------------------异常");
                    GameMain.this.startServer(false);
                } else {
                    GameMain.log.info("socket检测连接---------------------------正常");
                }
            } catch (Exception e) {
                GameMain.log.info("定时检测...SocketIOServer...连接情况[{}]", e);
            }
        }
    }
*/
}
