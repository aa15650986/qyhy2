package com.qy.game.server;

import com.qy.game.service.GameService;
import com.qy.game.service.GlobalService;
import com.qy.game.service.UserService;
import com.qy.game.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/global")
public class GlobalServer {

    @Resource
    private GlobalService globalService;
    @Resource
    private UserService userService;
    @Resource
    private GameService gameService;
    public JSONArray zongzj = new JSONArray();

    /**
     * IP地址检测
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserIPAddress")
    public void getUserIPAddress(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        JSONObject obj = new JSONObject();
        obj.put("ip", ip);
        Dto.returnJosnMsg(response, obj);
    }

    /**
     * 根据IP获取玩家距离
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getDistanceByIP")
    public void getDistanceByIP(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String ip = request.getParameter("ip");
        String ip1 = request.getParameter("ip1");

        JSONObject obj = new JSONObject();
        if (ip != null && ip1 != null) {
            obj.put("code", 1);
            long distance = DisTanceUtil.getDistanceByIP(ip, ip1);
            System.out.println("距离：" + distance);
            String data = "100m以内";
            if (distance > 1000) {
                data = distance / 1000 + "km";
            } else if (distance > 0) {
                data = distance + "m";
            }
            obj.put("data", data);
        } else {
            obj.put("code", 0);
            obj.put("msg", "参数错误！");
        }
        Dto.returnJosnMsg(response, obj);
    }

    /**
     * 根据位置经纬度坐标获取距离
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getDistanceByPoint")
    public void getDistanceByPoint(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String x1 = request.getParameter("x1");
        String y1 = request.getParameter("y1");
        String x2 = request.getParameter("x2");
        String y2 = request.getParameter("y2");

        JSONObject obj = new JSONObject();
        if (x1 != null && y1 != null && x2 != null && y2 != null) {
            obj.put("code", 1);
            long distance = DisTanceUtil.getDistance(Double.valueOf(x1), Double.valueOf(y1), Double.valueOf(x2), Double.valueOf(y2));
            String data = "100m以内";
            if (distance > 1000) {
                data = distance / 1000 + "km";
            } else if (distance > 0) {
                data = distance + "m";
            }
            obj.put("data", data);
        } else {
            obj.put("code", 0);
            obj.put("msg", "参数错误！");
        }
        Dto.returnJosnMsg(response, obj);
    }

    /**
     * 获取微信分享内容
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wxShare")
    public void wxShare(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int gid = Integer.valueOf(request.getParameter("gid"));
        int type = Integer.valueOf(request.getParameter("type"));
        String room_no = request.getParameter("room_no");
        String platform = request.getParameter("platform");
        String id = request.getParameter("id");
        System.out.println("微信分享gid：" + gid);
        System.out.println("微信分享type：" + type);
        System.out.println("微信分享room_no：" + room_no);
        System.out.println("微信分享platform：" + platform);
        System.out.println("微信分享id：" + id);
        if (platform == null) {
            platform = "0";
        }

        // 获取分享信息
        JSONObject data = globalService.getWXShare(gid, type, platform);
        System.out.println("微信分享data：" + data);

        JSONObject obj = new JSONObject();
        if (data != null) {

            obj.put("code", 1);
            if (!Dto.stringIsNULL(room_no)) {


                JSONObject room = gameService.getRoomInfo(gid, room_no);
                JSONObject baseinfo = room.getJSONObject("base_info");

                String roomName = "房间类型";
                switch (room.getInt("roomtype")) {
                    case 0:
                        roomName = "房卡";
                        break;
                    case 1:
                        roomName = "金币";
                        if (room.getInt("level") == -1) {
                            roomName = "竞技";
                        }
                        break;
                    case 2:
                        roomName = "代开房卡";
                        break;
                    case 3:
                        roomName = "元宝";
                        break;
                    default:
                        break;
                }

                String gameName = "游戏";
                if (gid == 1) {
                    gameName = "斗牛";
                } else if (gid == 2) {
                    gameName = "斗地主";
                } else if (gid == 3) {
                    gameName = "麻将";
                } else if (gid == 4) {
                    gameName = "十三水";
                } else if (gid == 5) {
                    gameName = "牌九";
                } else if (gid == 6) {
                    gameName = "炸金花";
                } else if (gid == 7) {
                    gameName = "百家乐";
                } else if (gid == 8) {
                    gameName = "抢红包";
                } else if (gid == 10) {
                    gameName = "比大小";
                } else if (gid == 12) {
                    gameName = "南安麻将";
                }

                if (gid == 1) {
                    if (type == 1) {
                        String roomNo = room_no;
                        String playerCount = baseinfo.getString("player");
                        String types = baseinfo.containsKey("type") ? baseinfo.getString("type") : "";
                        String turn = baseinfo.containsKey("turn") ? baseinfo.getJSONObject("turn").getString("turn") : "";
                        ;
                        String paytype = baseinfo.containsKey("paytype") ? baseinfo.getString("paytype") : "";
                        String di = baseinfo.containsKey("yuanbao") ? baseinfo.getString("yuanbao") : "";
                        String lc = baseinfo.containsKey("leaveYB") ? baseinfo.getString("leaveYB") : "";
                        String rc = baseinfo.containsKey("enterYB") ? baseinfo.getString("enterYB") : "";
                        // 倍数
                        JSONArray bs = new JSONArray();
                        if (baseinfo.containsKey("baseNum")) {
                            bs = baseinfo.getJSONArray("baseNum");
                        }
                        String bs1 = "";
                        for (int i = 0; i < bs.size(); i++) {
                            JSONObject jsonObject = bs.getJSONObject(i);
                            bs1 += jsonObject.getString("name");
                            if (i < bs.size() - 1) {
                                bs1 += "/";
                            }
                        }
                        if (types.equals("0")) {
                            types = "霸王庄";
                        } else if (types.equals("1")) {
                            types = "轮庄";
                        } else if (types.equals("2")) {
                            types = "抢庄";
                        } else if (types.equals("3")) {
                            types = "明牌抢庄";
                        } else if (types.equals("5")) {
                            types = "通比牛牛";
                        } else if (types.equals("6")) {
                            types = "坐庄模式";
                        }


                        String text = data.getString("text").replace(",底:", baseinfo.containsKey("yuanbao") ? ",底:" : "")
                                .replace(",入:", baseinfo.containsKey("leaveYB") ? ",入:" : "")
                                .replace(",离:", baseinfo.containsKey("enterYB") ? ",离:" : "")
                                .replace(",[paytype]", room.getInt("roomtype") == 0 ? ",[paytype]" : "")
                                .replace("[roomno]", roomNo)
                                .replace("[playercount]", playerCount)
                                .replace("[di]", di)
                                .replace("[lc]", lc)
                                .replace("[rc]", rc)
                                .replace("[roomName]", roomName)
                                .replace("[gameName]", gameName)
                                .replace("[baseNum]", bs1)
                                .replace("[type]", types).replace("[turn]", turn).replace("[paytype]", ("0").equals(paytype) ? "房主支付" : "房费AA");
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", roomNo);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    } else if (type == 2) {
                        System.out.println("进入牛牛分享战绩方法");
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", room_no);
                            data.put("title", title);
                        }
                        data.put("text", "点击这里查看牛牛战绩...");
                        //添加战绩分享html
                        if (data.containsKey("url") && !Dto.stringIsNULL(data.getString("url"))) {
                            data.element("url", data.getString("url") + "?room_id=" + room.getString("id"));
                        }
                    }
                }

                if (gid == 2) {
                    if (type == 1) {
                        String roomNo = room_no;
                        String baseNum = baseinfo.getString("baseNum");
                        String multiple = baseinfo.getString("multiple");
                        String turn = baseinfo.getJSONObject("turn").getString("turn");
                        String text = data.getString("text").replace("[roomno]", roomNo)
                                .replace("[baseNum]", baseNum)
                                .replace("[multiple]", multiple)
                                .replace("[roomName]", roomName)
                                .replace("[gameName]", gameName)
                                .replace("[turn]", turn);
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", roomNo);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    }
                }

                if (gid == 3 || gid == 12) {
                    if (type == 1) {
                        String roomtype = "";
                        if (baseinfo.containsKey("turn")) {
                            if (baseinfo.getJSONObject("turn").containsKey("guangyou")) {
                                roomtype = "石狮光游,";
                            } else if (baseinfo.getJSONObject("turn").getInt("turn") == 999) {
                                roomtype = "1课,";
                            } else {
                                roomtype = baseinfo.getJSONObject("turn").getString("turn") + "局 , ";
                            }
                        }

                        String roomNo = room_no;
                        String playerCount = roomtype + baseinfo.getString("player") + "人";
                        String di = baseinfo.containsKey("yuanbao") ? baseinfo.getString("yuanbao") : "";
                        String lc = baseinfo.containsKey("leaveYB") ? baseinfo.getString("leaveYB") : "";
                        String rc = baseinfo.containsKey("enterYB") ? baseinfo.getString("enterYB") : "";

                        //个性化规则（可吃/胡 碰/自摸、有金无平湖、AA）
                        if (baseinfo.containsKey("hasjinnopinghu") && "1".equals(baseinfo.getString("hasjinnopinghu"))) {
                            playerCount += ",有金无平胡";
                        }
                        if (baseinfo.containsKey("isNotChiHu") && "0".equals(baseinfo.getString("isNotChiHu"))) {
                            playerCount += ",可吃/胡";
                        }
                        if (baseinfo.containsKey("isNotChiHu") && "1".equals(baseinfo.getString("isNotChiHu"))) {
                            playerCount += ",碰/自摸";
                        }
                        if (baseinfo.containsKey("paytype") && "1".equals(baseinfo.getString("paytype"))) {
                            playerCount += ",房卡AA";
                        }
                        if (baseinfo.containsKey("paytype") && "0".equals(baseinfo.getString("paytype"))) {
                            playerCount += ",房主支付";
                        }

                        String rate = baseinfo.getString("type");
                        // 是否带漂
                        if (baseinfo.containsKey("piao")) {
                            rate = baseinfo.getString("piao");
                        }
                        String text = data.getString("text").replace(",底:", baseinfo.containsKey("yuanbao") ? ",底:" : "")
                                .replace(",入:", baseinfo.containsKey("leaveYB") ? ",入:" : "")
                                .replace(",离:", baseinfo.containsKey("enterYB") ? ",离:" : "")
                                .replace("[roomno]", roomNo)
                                .replace("[playercount]", playerCount)
                                .replace("[roomName]", roomName)
                                .replace("[gameName]", gameName)
                                .replace("[di]", di)
                                .replace("[rc]", rc)
                                .replace("[lc]", lc)
                                .replace("[rate]", rate);
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", roomNo).replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    } else if (type == 2) {

                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", room_no).replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName);

                            data.put("title", title);
                        }
                        data.put("text", "点击这里查看麻将战绩...");
                        //添加战绩分享html
                        if (data.containsKey("url") && !Dto.stringIsNULL(data.getString("url"))) {
                            data.element("url", data.getString("url") + "?room_id=" + room.getString("id"));
                        }
                    }

                }
                if (gid == 4) {
                    if (type == 1) {
                        String roomNo = room_no;
                        String playerCount = baseinfo.getString("player");
                        String rate = baseinfo.getString("type");
                        String wf = "";
                        if (rate.equals("0")) {
                            wf = "互比";
                        }
                        if (rate.equals("1")) {
                            wf = "霸王庄";
                        }
                        if (rate.equals("2")) {
                            wf = "坐庄";
                        }
                        String color = baseinfo.containsKey("color") ? baseinfo.getString("color") : "0";
                        String paytype = baseinfo.containsKey("paytype") ? baseinfo.getString("paytype") : "";
                        String turn = baseinfo.containsKey("turn") ? baseinfo.getJSONObject("turn").getString("turn") : "";
                        String di = baseinfo.containsKey("yuanbao") ? baseinfo.getString("yuanbao") : "";
                        String lc = baseinfo.containsKey("leaveYB") ? baseinfo.getString("leaveYB") : "";
                        String rc = baseinfo.containsKey("enterYB") ? baseinfo.getString("enterYB") : "";
                        String jm = baseinfo.containsKey("jiama") ? baseinfo.getString("jiama") : "";
                        String maType = "";
                        if (!Dto.stringIsNULL(jm)) {
                            if (jm.equals("0")) {
                                maType = "不加";
                            }
                            if (jm.equals("1")) {
                                maType = "黑桃随机";
                            }
                            if (jm.equals("2")) {
                                maType = "黑桃A";
                            }
                            if (jm.equals("3")) {
                                maType = "黑桃3";
                            }
                            if (jm.equals("4")) {
                                maType = "黑桃5";
                            }
                            if (jm.equals("5")) {
                                maType = "全部随机";
                            }
                        }
                        String text = data.getString("text").replace(",底:", baseinfo.containsKey("yuanbao") ? ",底:" : "")
                                .replace(",入:", baseinfo.containsKey("leaveYB") ? ",入:" : "")
                                .replace(",离:", baseinfo.containsKey("enterYB") ? ",离:" : "")
                                .replace(",[color]", room.getInt("roomtype") == 0 ? ",[color]" : "")
                                .replace(",[paytype]", room.getInt("roomtype") == 0 ? ",[paytype]" : "")
                                .replace(",[maType]", room.getInt("roomtype") == 0 ? ",[maType]" : "")
                                .replace("[roomno]", roomNo)
                                .replace("[playercount]", playerCount)
                                .replace("[turn]", turn)
                                .replace("[rate]", wf)
                                .replace("[di]", di)
                                .replace("[rc]", rc)
                                .replace("[lc]", lc)
                                .replace("[roomName]", roomName)
                                .replace("[gameName]", gameName)
                                .replace("[maType]", maType)
                                .replace("[color]", ("0").equals(color) ? "不加色" : "加" + color + "色")
                                .replace("[paytype]", ("0").equals(paytype) ? "房主支付" : "房费AA");
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", roomNo);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    } else if (type == 2) {

                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", room_no).replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName);
                            data.put("title", title);
                        }
                        data.put("text", "点击这里查看十三水战绩...");
                        //添加战绩分享html
                        if (data.containsKey("url") && !Dto.stringIsNULL(data.getString("url"))) {
                            data.element("url", data.getString("url") + "?room_id=" + room.getString("id"));
                        }
                    }
                }
                if (gid == 5 || gid == 17) {
                    if (type == 1) {

                        if (!Dto.stringIsNULL(platform) && "XYQP".equals(platform)) {
                            String roomNo = room_no;
                            String paytype = baseinfo.getString("paytype");
                            String rate = baseinfo.getString("type");
                            if (rate.equals("0")) {
                                rate = "房主坐庄";
                            } else if (rate.equals("1")) {
                                rate = "看牌抢庄";
                            } else if (rate.equals("2")) {
                                rate = "通比";
                            }
							/*JSONObject zhuangxian = baseinfo.getJSONObject("zhuangxian");
							.replace("[xian]",zhuangxian.getString("xian"))*/
                            String text = data.getString("text").replace("[roomno]", roomNo)
                                    .replace("[turn]", baseinfo.getJSONObject("turn").getString("turn"))
                                    .replace("[type]", rate)
                                    .replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName)
                                    .replace("[paytype]", ("0").equals(paytype) ? "房主支付" : "房费AA");
                            if (data.getString("title").contains("[roomno]")) {
                                String title = data.getString("title").replace("[roomno]", roomNo);
                                data.put("title", title);
                            }
                            data.put("text", text);
                        } else {
                            String roomNo = room_no;
                            String paytype = baseinfo.getString("paytype");
                            String rate = baseinfo.getString("type");
                            if (rate.equals("0")) {
                                rate = "房主坐庄";
                            } else if (rate.equals("1")) {
                                rate = "看牌抢庄";
                            } else if (rate.equals("2")) {
                                rate = "通比";
                            } else if (rate.equals("3")) {
                                rate = "轮庄";
                            } else if (rate.equals("4")) {
                                rate = "抢庄";
                            }
                            /*JSONObject zhuangxian = baseinfo.getJSONObject("zhuangxian");*/
                            String text = data.getString("text").replace("[roomno]", roomNo)
                                    .replace("[turn]", baseinfo.getJSONObject("turn").getString("turn"))
                                    .replace("[type]", rate)
                                    .replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName)
                                    .replace("[paytype]", ("0").equals(paytype) ? "房主支付" : "房费AA");
                            if (data.getString("title").contains("[roomno]")) {
                                String title = data.getString("title").replace("[roomno]", roomNo);
                                data.put("title", title);
                            }
                            data.put("text", text);
                        }
                    }
                }
                if (gid == 6) {
                    String roomNo = room_no;
                    String playerCount = baseinfo.getString("player");
                    String types = baseinfo.containsKey("type") ? baseinfo.getString("type") : "";
                    if (types.equals("0")) {
                        types = "经典模式";
                    } else if (types.equals("1")) {
                        types = "必闷三圈";
                    } else if (types.equals("2")) {
                        types = "激情模式";
                    }
                    String paytype = baseinfo.containsKey("paytype") ? baseinfo.getString("paytype") : "";
                    String dizhu = baseinfo.containsKey("di") ? baseinfo.getString("di") : "";
                    String maxcoins = baseinfo.containsKey("maxcoins") ? baseinfo.getString("maxcoins") : "";
                    // 倍数
                    JSONArray bs = new JSONArray();
                    if (baseinfo.containsKey("baseNum")) {
                        bs = baseinfo.getJSONArray("baseNum");
                    }
                    String bs1 = "";
                    for (int i = 0; i < bs.size(); i++) {
                        bs1 += bs.getString(i);
                        if (i < bs.size() - 1) {
                            bs1 += "/";
                        }
                    }
                    String di = baseinfo.containsKey("yuanbao") ? baseinfo.getString("yuanbao") : "";
                    String lc = baseinfo.containsKey("leaveYB") ? baseinfo.getString("leaveYB") : "";
                    String rc = baseinfo.containsKey("enterYB") ? baseinfo.getString("enterYB") : "";
                    String title = data.getString("title").replace("[roomno]", roomNo);
                    String text = data.getString("text").replace("[playercount]", playerCount)
                            .replace("[di]", di)
                            .replace("[rc]", rc)
                            .replace("[lc]", lc)
                            .replace("[roomName]", roomName)
                            .replace("[gameName]", gameName)
                            .replace(",底:", baseinfo.containsKey("yuanbao") ? ",底:" : "")
                            .replace(",入:", baseinfo.containsKey("leaveYB") ? ",入:" : "")
                            .replace(",离:", baseinfo.containsKey("enterYB") ? ",离:" : "");

                    if (room.getInt("roomtype") == 3) {
                        text = text.replace("[rate]", globalService.getzagame("type", baseinfo.getString("type"), String.valueOf(gid), platform))
                                .replace(",[turn]局", "")
                                .replace(",上限[maxcoins]", "")
                                .replace("[type]", types)
                                .replace("[paytype]", "")
                                .replace("底注[dizhu]", "");
                    } else {
                        text = text.replace("[roomno]", roomNo)
                                .replace("[baseNum]", bs1)
                                .replace("[dizhu]", dizhu)
                                .replace("[maxcoins]", maxcoins)
                                .replace("[type]", types).replace("[paytype]", ("0").equals(paytype) ? "房主支付" : "房费AA")
                                .replace("[turn]", baseinfo.getJSONObject("turn").getString("turn"));
                    }
                    data.put("title", title);
                    data.put("text", text);

                }
                if (gid == 10) {
                    if (type == 1) {
                        String roomNo = room_no;
                        //String playerCount = baseinfo.getString("player");
                        String text = data.getString("text").replace("[roomno]", roomNo);
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title")
                                    .replace("[roomName]", roomName)
                                    .replace("[gameName]", gameName)
                                    .replace("[roomno]", roomNo);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    }
                }
                if (gid == 14) {
                    if (type == 1) {
                        String roomNo = room_no;
                        String rate = baseinfo.getString("type");
                        String singleMax = baseinfo.getString("singleMax");
                        String di = baseinfo.containsKey("yuanbao") ? baseinfo.getString("yuanbao") : "";
                        String lc = baseinfo.containsKey("leaveYB") ? baseinfo.getString("leaveYB") : "";
                        String rc = baseinfo.containsKey("enterYB") ? baseinfo.getString("enterYB") : "";
                        String text = data.getString("text").replace("[roomno]", roomNo)
                                .replace("[rate]", ("1").equals(rate) ? "1赔10.5" : "1赔10")
                                .replace("[singleMax]", ("0").equals(singleMax) ? "不限" : singleMax)
                                .replace("[di]", di)
                                .replace("[rc]", rc)
                                .replace("[lc]", lc)
                                .replace("[roomName]", roomName)
                                .replace("[gameName]", gameName);
                        if (data.getString("title").contains("[roomno]")) {
                            String title = data.getString("title").replace("[roomno]", roomNo);
                            data.put("title", title);
                        }
                        data.put("text", text);
                    }
                }
            }

            if (type == 3) {
                //房号:[roomno][gamename] [visitcode]，战绩：[con]，快来一起玩吧
                String gamename = "游戏";
                if (gid == 1) {
                    gamename = "斗牛";
                } else if (gid == 2) {
                    gamename = "斗地主";
                } else if (gid == 3) {
                    gamename = "麻将";
                } else if (gid == 4) {
                    gamename = "十三水";
                } else if (gid == 5) {
                    gamename = "牌九";
                } else if (gid == 6) {
                    gamename = "炸金花";
                } else if (gid == 7) {
                    gamename = "百家乐";
                } else if (gid == 10) {
                    gamename = "比大小";
                }

                JSONObject userlog = globalService.getuserlogsInfo(Long.valueOf(id));
                JSONObject gamelog = globalService.getgamelogsInfo(userlog.getLong("gamelog_id"));
                if (!Dto.isObjNull(gamelog)) {
                    String visitcode = "";
                    if (gamelog.containsKey("visitcode") && !Dto.stringIsNULL(gamelog.getString("visitcode"))) {
                        visitcode = "录像码：" + gamelog.getString("visitcode");
                    }

                    String roomNo = gamelog.getString("room_no");

                    JSONArray result = JSONArray.fromObject(userlog.getString("result"));
                    String con = "";
                    for (int i = 0; i < result.size(); i++) {
                        con += result.getJSONObject(i).getString("player") + ":" + result.getJSONObject(i).getString("score");
                        if (result.size() > (i + 1)) {
                            con += ",";
                        }
                    }

                    String text = data.getString("text").replace("[roomno]", roomNo)
                            .replace("[gamename]", gamename)
                            .replace("[visitcode]", visitcode)
                            .replace("[con]", con);
                    if (data.getString("title").contains("[roomno]")) {
                        String title = data.getString("title").replace("[roomno]", roomNo);
                        data.put("title", title);
                    }
                    data.put("text", text);
//					//添加战绩分享html
//					if(data.containsKey("url") && !Dto.stringIsNULL(data.getString("url"))){
//						data.element("url", data.getString("url")+"?"+roomNo);
//					}


                } else {
                    obj.put("code", 0);
                    obj.put("msg", "暂未开放");
                }


            }
            if (type == 4) {//总战绩分享
                //房号:[roomno][gamename] [visitcode]，战绩：[con]，快来一起玩吧
                String gamename = "游戏";
                if (gid == 1) {
                    gamename = "斗牛";
                } else if (gid == 2) {
                    gamename = "斗地主";
                } else if (gid == 3) {
                    gamename = "麻将";
                } else if (gid == 4) {
                    gamename = "十三水";
                } else if (gid == 5) {
                    gamename = "牌九";
                } else if (gid == 6) {
                    gamename = "炸金花";
                } else if (gid == 7) {
                    gamename = "百家乐";
                } else if (gid == 10) {
                    gamename = "比大小";
                }

                JSONObject userlog = globalService.getuserlogs2(Long.valueOf(id));
                JSONObject gamelog = globalService.getgamelogsInfo(userlog.getLong("gamelog_id"));
                if (!Dto.isObjNull(gamelog)) {
                    String visitcode = "";
                    if (gamelog.containsKey("visitcode") && !Dto.stringIsNULL(gamelog.getString("visitcode"))) {
                        visitcode = "录像码：" + gamelog.getString("visitcode");
                    }

                    String roomNo = gamelog.getString("room_no");

                    JSONArray result = JSONArray.fromObject(userlog.getJSONArray("result"));
                    String con = "";
                    for (int i = 0; i < result.size(); i++) {
                        con += result.getJSONObject(i).getString("player") + ":" + result.getJSONObject(i).getString("score");
                        if (result.size() > (i + 1)) {
                            con += ",";
                        }
                    }

                    String text = data.getString("text").replace("[roomno]", roomNo)
                            .replace("[gamename]", gamename)
                            .replace("[visitcode]", visitcode)
                            .replace("[con]", con);
                    if (data.getString("title").contains("[roomno]")) {
                        String title = data.getString("title").replace("[roomno]", roomNo);
                        data.put("title", title);
                    }
                    data.put("text", text);
//					//添加战绩分享html
//					if(data.containsKey("url") && !Dto.stringIsNULL(data.getString("url"))){
//						data.element("url", data.getString("url")+"?"+roomNo);
//					}
                } else {
                    obj.put("code", 0);
                    obj.put("msg", "暂未开放");
                }
            }
            if (type == 5) {//获奖记录分享
                String gamename = "游戏";
                if (gid == 1) {
                    gamename = "斗牛";
                } else if (gid == 2) {
                    gamename = "斗地主";
                } else if (gid == 3) {
                    gamename = "麻将";
                } else if (gid == 4) {
                    gamename = "十三水";
                } else if (gid == 5) {
                    gamename = "牌九";
                } else if (gid == 6) {
                    gamename = "炸金花";
                } else if (gid == 7) {
                    gamename = "百家乐";
                } else if (gid == 10) {
                    gamename = "比大小";
                }
                String title = data.getString("title");
                obj.put("title", title);
                obj.put("text", request.getParameter("content"));
                data.put("text", request.getParameter("content"));
            }
            if ("JHG".equals(platform)) {
                String userId = request.getParameter("userId");
                data.element("url", data.getString("url") + "?userId=" + userId);
            }

            obj.put("data", data);
            System.out.println("分享：" + obj);


        } else {
            obj.put("code", 0);
            obj.put("msg", "分享异常");
        }
        Dto.returnJosnMsg(response, obj);
    }


    /**
     * 获取活动列表
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getActivityList")
    public void getActivityList(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JSONArray data = globalService.getActivityList();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "");
        jsonObject.put("data", data);
        jsonObject.put("code", 1);
        Dto.returnJosnMsg(response, jsonObject);
    }


    /**
     * 获取活动详情
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getActivityDetail")
    public void getActivityDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int id = Integer.valueOf(request.getParameter("id"));
        JSONObject msgContent = globalService.getActivityDetail(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "");
        jsonObject.put("data", msgContent);
        jsonObject.put("code", 1);
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 获取玩家战绩
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameLogs")
    public void getUserGameLogs(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int gid = Integer.valueOf(request.getParameter("gid"));
        String uuid = request.getParameter("uuid");
        JSONObject user = userService.getUserInfoByOpenID(uuid, false, null);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            JSONArray gameLogs = globalService.getUserGameLogList(user.getInt("id"), gid);
            jsonObject.put("code", 1);
            jsonObject.put("data", gameLogs);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "您的登录凭证已失效！请重新登录");
        }
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 获取玩家战绩(房卡)（汇总）
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameList")
    public void getUserGameList(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int gid = Integer.valueOf(request.getParameter("gid"));
        String uuid = request.getParameter("uuid");
        JSONObject user = userService.getUserInfoByOpenID(uuid, false, null);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            JSONArray gameLogs = globalService.getUserGameList(user.getInt("id"), gid);
            jsonObject.put("code", 1);
            jsonObject.put("data", gameLogs);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "您的登录凭证已失效！请重新登录");
        }
        Dto.printMsg(response, jsonObject.toString());
    }

    /**
     * 获取玩家战绩(房卡和元宝)（汇总）
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameList3")
    public void getUserGameList3(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int gid = Integer.valueOf(request.getParameter("gid"));
        String uuid = request.getParameter("uuid");
        String type = request.getParameter("type");//1房卡2元宝3房卡和元宝
        JSONObject user = userService.getUserInfoByOpenID(uuid, false, null);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            JSONArray gameLogs = globalService.getUserGameList3(user.getInt("id"), gid, type);
            jsonObject.put("code", 1);
            jsonObject.put("data", gameLogs);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "您的登录凭证已失效！请重新登录");
        }
        Dto.returnJosnMsg(response, jsonObject);
    }

    @RequestMapping("/getUserGameList2")
    public void getUserGameList2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int gid = Integer.valueOf(request.getParameter("gid"));
        JSONObject user = (JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            JSONArray gameLogs = globalService.getUserGameList(user.getInt("id"), gid);
            jsonObject.put("code", 1);
            jsonObject.put("data", gameLogs);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "您的登录凭证已失效！请重新登录");
        }
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 获取玩家战绩（详情）
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameDetail")
    public void getUserGameDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String room_id = request.getParameter("room_id");
        JSONObject msg = new JSONObject();
        try {
            JSONArray list = globalService.getzausergamelogs(room_id);
            msg.element("code", "1")
                    .element("list", list);
        } catch (Exception e) {
            // TODO: handle exception
            msg.element("code", "0");
        }

        Dto.returnJosnMsg(response, msg);
    }

    /**
     * 获取玩家单局战绩（汇总）
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameobj")
    public String getUserGameobj(HttpServletRequest request, HttpServletResponse response) {
        return null;

    }

    /**
     * 获取玩家战绩-元宝
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameLogs3")
    public void getUserGameLogs3(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int gid = Integer.valueOf(request.getParameter("gid"));
        String uuid = request.getParameter("uuid");
        String type = request.getParameter("type");
        JSONObject user = userService.getUserInfoByOpenID(uuid, false, null);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            Dto.writeLog("开始战绩:" + TimeUtil.getNowDate());
            System.out.println("开始战绩:" + TimeUtil.getNowDate());
            JSONArray gameLogs = globalService.getUserGameLogList3(user.getInt("id"), gid, type);
            Dto.writeLog("结束战绩:" + TimeUtil.getNowDate());
            System.out.println("结束战绩:" + TimeUtil.getNowDate());
            jsonObject.put("code", 1);
            jsonObject.put("data", gameLogs);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "您的登录凭证已失效！请重新登录");
        }
        Dto.printMsg(response, jsonObject.toString());
    }


    /**
     * 获得浏览器类型
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getBrowserType")
    public void getBrowserType(HttpServletRequest request, HttpServletResponse response) {
        String head = request.getHeader("User-Agent");
        System.out.println("获得设备的head信息为：" + head);

        String deviceName = DeviceCheckUtil.getDeviceName(head);
        System.out.println("获得设备的deviceName信息为：" + deviceName);
    }


    /**
     * 获取玩家个人信息
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserDatas")
    public void getUserDatas(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String openid = request.getParameter("openid");
        JSONObject user = userService.getUserDatas(openid);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
            jsonObject.put("code", 1);
            jsonObject.put("data", user);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "该账号不存在");
        }
        Dto.returnJosnMsg(response, jsonObject);
    }


    /**
     * 获取游戏回放数据
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/visitMJGameRoom")
    public void visitMJGameRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int gid = Integer.valueOf(request.getParameter("gid"));
        long glog_id = Long.valueOf(request.getParameter("glog_id"));
        String roomNo = request.getParameter("room_no");
        long uid = Long.valueOf(request.getParameter("uid"));
        String visitCode = request.getParameter("visitCode");

        JSONObject jsonObject = new JSONObject();
        if (0L < glog_id) { // 玩家自己查看游戏回放

//			JSONObject room = gameService.getRoomInfoByRno(gid, roomNo, null);
//			if(room!=null){
//				int myIndex = 0;
//				if(uid==room.getLong("user_id0")){
//				}else if(uid==room.getLong("user_id1")){
//					myIndex = 1;
//				}else if(uid==room.getLong("user_id2")){
//					myIndex = 2;
//				}else if(uid==room.getLong("user_id3")){
//					myIndex = 3;
//				}else if(uid==room.getLong("user_id4")){
//					myIndex = 4;
//				}else if(uid==room.getLong("user_id5")){
//					myIndex = 5;
//				}else if(uid==room.getLong("user_id6")){
//					myIndex = 6;
//				}else if(uid==room.getLong("user_id7")){
//					myIndex = 7;
//				}else if(uid==room.getLong("user_id8")){
//					myIndex = 8;
//				}else if(uid==room.getLong("user_id9")){
//					myIndex = 9;
//				}

            JSONObject gameLog = globalService.getGameLogById(glog_id);
            if (gameLog != null && gameLog.getString("room_no").equals(roomNo) && gameLog.getInt("gid") == gid) {
                jsonObject.put("code", 1);

                JSONObject data = JSONObject.fromObject(gameLog.getString("base_info"));
//					data.put("index", myIndex);
                data.put("game_index", gameLog.get("game_index"));
                data.put("data", gameLog.get("action_records"));
                data.put("result", gameLog.get("result"));
                jsonObject.put("data", data);
            }
//			}else{
//				jsonObject.put("code",0);
//				jsonObject.put("msg", "该游戏记录不存在");
//			}

        } else { // 玩家通过观看码查看回放记录

            JSONObject gameLog = globalService.getGameLog(gid, visitCode);
            if (gameLog != null && gameLog.containsKey("action_records")) {
                jsonObject.put("code", 1);

                JSONObject data = JSONObject.fromObject(gameLog.getString("base_info"));
                data.put("index", 0);
                data.put("game_index", gameLog.get("game_index"));
                data.put("data", gameLog.get("action_records"));
                data.put("result", gameLog.get("result"));
                jsonObject.put("data", data);
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "该观战码无效");
            }
        }

        Dto.returnJosnMsg(response, jsonObject);
    }


    /**
     * 获取应用对应版本的配置信息
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getAppVersionInfo")
    public void getAppVersionInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        String version = request.getParameter("version");

        long start1 = System.currentTimeMillis();
        JSONObject versionInfo = globalService.getVersionInfo(version);
        long end1 = System.currentTimeMillis();
        System.out.println("globalService.getVersionInfo查询数据库耗时-----" + (end1 - start1) + "---开始" + start1 + "---结束" + end1);

        JSONObject jsonObject = new JSONObject();
        if (versionInfo != null) {
            jsonObject.put("code", 1);
            jsonObject.put("data", versionInfo);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "找不到数据，请查看参数是否有误");
        }
        long end = System.currentTimeMillis();
        System.out.println("getAppVersionInfo方法消耗时间------" + (end - start) + "---开始" + start + "---结束" + end);
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 获取应用对应版本的配置信息(新)
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getAppVersionInfo_new")
    public void getAppVersionInfo_new(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("============接收到请求版本信息============");
        String version = request.getParameter("version");
        String platform = request.getParameter("platform");

        long start1 = System.currentTimeMillis();
        JSONObject versionInfo = globalService.getVersionInfo_new(platform);
        long end1 = System.currentTimeMillis();
        System.out.println("globalService.getVersionInfo查询数据库耗时-----" + (end1 - start1) + "---开始" + start1 + "---结束" + end1);

        JSONObject jsonObject = new JSONObject();
        if (!Dto.isObjNull(versionInfo) && versionInfo.getString("version").equals(version)) {
            jsonObject.put("code", 1);
            jsonObject.put("data", versionInfo);
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("data", versionInfo);
            jsonObject.put("msg", "找不到数据，请查看参数是否有误");
        }
        long end = System.currentTimeMillis();
        System.out.println("getAppVersionInfo方法消耗时间------" + (end - start) + "---开始" + start + "---结束" + end);
        Dto.returnJosnMsg(response, jsonObject);
    }


    /**
     * 获取结算详情
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping("/getRoomjiesuan")
    public String getRoomjiesuan(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {

        String room_id = request.getParameter("room_id");


        JSONObject gameInfo = globalService.getJesuanSSS(Long.valueOf(room_id));


        JSONObject roomdata = gameInfo.getJSONObject("roomdata");
        JSONObject jiesuan = gameInfo.getJSONObject("jiesuan");


        JSONObject obj = new JSONObject();
        obj.element("room_no", roomdata.getString("room_no"));
        obj.element("game_count", jiesuan.getString("game_index"));
        obj.element("finishtime", jiesuan.getString("finishtime"));

        obj.element("array", JSONArray.fromObject(jiesuan.getString("jiesuan")));
        request.setAttribute("obj", obj);

        int gid = roomdata.getInt("game_id");
        String to_url = "";
        //不同游戏跳转不同页面
        if (gid == 1) {
            to_url = "jiesuan_nn";
        }
        if (gid == 2) {
            to_url = "jiesuan_sss";
        }
        if (gid == 3) {
            to_url = "jiesuan_mj";
        }
        if (gid == 4) {
            to_url = "jiesuan_sss";
        }
        if (gid == 5) {
            to_url = "jiesuan_sss";
        }

        return to_url;

    }

    /**
     * 获取玩家全部战绩-元宝
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getUserGameLogs4")
    public void getUserGameLogs4(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("获取战绩数据1：" + DateUtils.gettimestamp());
        //JSONArray data=new JSONArray();
        String uuid = request.getParameter("uuid");
        String type = request.getParameter("type");
        String gid = request.getParameter("gid");
        JSONObject user = userService.getUserInfoByOpenID(uuid, false, null);
        System.out.println("uuid" + uuid);
        System.out.println("用户名" + user.getString("name"));
        //JSONArray getgames=globalService.getgames();
//		for (int i = 0; i < getgames.size(); i++) {
        //	JSONObject jsonObject= new JSONObject();
        JSONArray gameLogs = globalService.getUserGameLogList3(user.getInt("id"), Integer.valueOf(gid), type);
        System.err.println("战绩数据为----------" + gameLogs);
        for (int i = 0; i < gameLogs.size(); i++) {
            JSONObject jsonObject = gameLogs.getJSONObject(i);
            gameLogs.getJSONObject(i).put("result", JSONArray.fromObject(jsonObject.getString("result")));
        }
        System.out.println("获取战绩数据2：" + DateUtils.gettimestamp());
//			jsonObject.element("gid", 4);
//			jsonObject.element("list", gameLogs);
//			data.add(jsonObject);
        //}
        //	zongzj=data;
        Dto.printMsg(response, gameLogs.toString());
//		System.out.println("战绩"+zongzj);
//		System.out.println("获取战绩数据2："+DateUtils.gettimestamp());
        //Dto.writeLog("战绩"+zongzj);
    }

    /**
     * 根据游戏id获取数据
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getuserxxzj.json")
    public void getuserxxzj(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("获取战绩数据3：" + DateUtils.gettimestamp());
        String gid = request.getParameter("gid");
        JSONArray list = new JSONArray();
        for (int i = 0; i < zongzj.size(); i++) {
            if (gid.equals(zongzj.getJSONObject(i).getString("gid"))) {
                list = zongzj.getJSONObject(i).getJSONArray("list");
                break;
            }
        }
        JSONObject msg = new JSONObject();
        if (list.size() == 0) {
            msg.element("code", "0");
        } else {
            msg.element("code", "1")
                    .element("data", list);
        }
        System.out.println("获取战绩数据4：" + DateUtils.gettimestamp());
        Dto.printMsg(response, msg.toString());
    }

}
