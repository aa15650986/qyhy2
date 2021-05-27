package com.qy.game.server;//package com.qy.game.server;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.TimerTask;
//
//import com.qy.game.model.GameServer;
//
///**
// * 游戏定时器
// * @author lhp
// *
// */
//public class GameTimer {
//
//
//    static class GameTask extends TimerTask{
//
//    	private List<GameServer> serverList = new ArrayList<GameServer>();
//
//    	@Override
//		public void run(){
//
//    		for(GameServer gameServer: LVSServer.gameServers){
//
//    			// 游戏服务器连接故障
//    			if(!gameServer.isConnection()){
//    				gameServer.setReconnCount(gameServer.getReconnCount()-1);
//    			}else{
//    				gameServer.setReconnCount(3);
//    			}
//
//    			//
//    			if(gameServer.getReconnCount()<=0){
//
//    				serverList.add(gameServer);
//    				//System.out.println("服务器："+gameServer.getName()+"已掉线！！");
//    			}
//
//    			gameServer.setConnection(false);
//    		}
//
//    		// 移除失去连接的服务器
//    		if(serverList.size()>0){
//    			LVSServer.gameServers.removeAll(serverList);
//    			serverList.clear();
//    		}
//        }
//    }
//}
