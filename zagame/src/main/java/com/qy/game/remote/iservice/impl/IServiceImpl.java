package com.qy.game.remote.iservice.impl;

import com.qy.game.remote.iservice.IService;
import net.sf.json.JSONObject;

import java.rmi.RemoteException;

public class IServiceImpl implements IService {


    @Override
    public int joinServer(String ip, int port, String serverName) throws RemoteException {
        return 0;
    }

    @Override
    public void heartBeat(String serverName) throws RemoteException {

    }

    @Override
    public void settlementRoomNo(String roomNo) throws RemoteException {

    }

    @Override
    public void settlementRoomNo_PJ(String roomNo) throws RemoteException {

    }

    @Override
    public void updateZaCoinsRec(Long userid, Integer coins, Integer type) throws RemoteException {

    }

    @Override
    public JSONObject pump(String userIds, String roomNo, String gid, String sum, String obj) throws RemoteException {
        return null;
    }

    @Override
    public JSONObject pump_platform(String userIds, String roomNo, String gid, String sum, String obj, String platform) throws RemoteException {
        return null;
    }

    @Override
    public String getPlatform(long id) throws RemoteException {
        return null;
    }
}
