package com.qy.game.server.dating;

import com.qy.game.service.MiscellService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Dto;
import com.qy.game.utils.JsSdkUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 杂7杂8的
 *
 * @author ASUS
 */
@Controller
public class MiscellaneousService {
    @Resource
    private MiscellService msbiz;
    @Resource
    private UserService userService;
    @Resource
    private GoldShopBizServer gsbiz;

    /**
     * 获取装盘的基础数据
     *
     * @throws IOException
     */
    @RequestMapping("go_systurntab.json")
    public void gosysturntab(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String platform = request.getParameter("platform");
        JSONObject data = msbiz.systurntab(id, platform);
        Dto.printMsg(response, data.toString());
    }

    //开始转的钱
    @RequestMapping("delturntabmeny.json")
    public void delturntabmeny(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String platform = request.getParameter("platform");
        JSONObject msg = msbiz.delturntabmeny(id, platform);
        Dto.printMsg(response, msg.toString());
    }

    /**
     * 金黄冠获取每天的分享金币和总的分享金币
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("JHGcosin.json")
    public void JHGcosin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        JSONObject msg = new JSONObject();
        try {
            msg = msbiz.JHGcosin(id);

        } catch (Exception e) {
            // TODO: handle exception
            msg.element("newprice", 0);
            msg.element("countprice", 0);
        }
        Dto.printMsg(response, msg.toString());
    }


    /**
     * 金黄冠福袋分享完获取的钱
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("JHGfd.json")
    public void JHGfd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String type = request.getParameter("type");
        String code = "1";
        System.out.println("进入福袋type=" + type);
        try {
            msbiz.JHGfd(id, type);
        } catch (Exception e) {
            // TODO: handle exception
            code = "0";
        }
        Dto.printMsg(response, code);
    }

    /**
     * JHG 分享
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("JHGfenxin.html")
    public String JHGfenxin(HttpServletRequest request, HttpServletResponse response) {
        String domainstr = request.getServerName();
        String url2 = gsbiz.getdomain(domainstr);
        HttpServletRequest httpRequest = request;
        String url = "http://" + request.getServerName() //服务器地址
                + httpRequest.getContextPath()      //项目名称
                + httpRequest.getServletPath();      //请求页面或其他地址
        String query = request.getQueryString();

        if (!Dto.stringIsNULL(query))
            url = url + "?" + (httpRequest.getQueryString()); //参数
        JSONObject jssdkdata = JsSdkUtils.getReadyParameter(request, url);
        JSONObject shu = msbiz.zdlwase(url2);
        request.setAttribute("jssdkdata", jssdkdata);
        request.setAttribute("wechimg", shu.getString("wechimg"));
        request.setAttribute("wechtext", shu.getString("wechtext"));
        request.setAttribute("wechhao", shu.getString("wechhao"));
        request.setAttribute("query", query);
        return "work/JHG/fenxing";
    }

    /**
     * 金黄冠忘记密码
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("JHGwjmm.html")
    public String JHGwjmm(HttpServletRequest request, HttpServletResponse response) {
        return "work/JHG/wanjimm";
    }

    /**
     * 金黄冠app获取 -用户头像
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("gouserimg.json")
    public void gouserimg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject data = msbiz.gouserimg();
        String basePath = userService.getSokcetInfo().get("headimgUrl");
        if (data.getJSONArray("nan").size() != 0) {
            JSONArray d = data.getJSONArray("nan");
            for (int i = 0; i < d.size(); i++) {
                d.getJSONObject(i).element("img", basePath + d.getJSONObject(i).getString("img"));
            }
        }
        if (data.getJSONArray("nv").size() != 0) {
            JSONArray d = data.getJSONArray("nv");
            for (int i = 0; i < d.size(); i++) {
                d.getJSONObject(i).element("img", basePath + d.getJSONObject(i).getString("img"));
            }
        }
        Dto.printMsg(response, data.toString());
    }

    /**
     * 更新用户头像
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("JHGupuserimg.json")
    public void JHGupuserimg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String basePath = userService.getSokcetInfo().get("headimgUrl");
        String id = request.getParameter("id");
        String img = request.getParameter("img");
        JSONObject msg = new JSONObject();
        try {
            img = img.replace(basePath, "");
            msbiz.JHGupuserimg(id, img);
            msg.element("code", "1")
                    .element("msg", "操作成功");
        } catch (Exception e) {
            // TODO: handle exception
            msg.element("code", "0")
                    .element("msg", "操作失败");
        }

        Dto.printMsg(response, msg.toString());
    }

    //获取微招代理的微信好
    @RequestMapping("zdlwase.json")
    public void zdlwase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject data = new JSONObject();
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        try {
            JSONObject shu = msbiz.zdlwase(url);

            data.element("code", 1)
                    .element("wetchaMsg", shu.containsKey("serviceMsg") ? shu.getString("serviceMsg") : "")
                    .element("wechatCode", shu.containsKey("wechatCode") ? shu.getString("wechatCode") : "")
                    .element("wechatName", shu.containsKey("wechatName") ? shu.getString("wechatName") : "");
            if (shu.containsKey("proxyMsg")) {
                data.element("wechRecruit", shu.getString("proxyMsg"));
            }
        } catch (Exception e) {
            // TODO: handle exception
            data.element("code", 0);
        }
        Dto.printMsg(response, data.toString());
    }

    /**
     * 不属于游戏的-贷款
     */
    @RequestMapping("loan.html")
    public String loan(HttpServletRequest request, HttpServletResponse response) {

        return "work/item/loan";

    }

    @RequestMapping("getmessage9")
    public void getmessage9(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject msg = new JSONObject();
        try {
            msg = msbiz.getmessage9();
            msg.element("code", 1);
        } catch (Exception e) {
            // TODO: handle exception
            msg.element("code", 0);
        }
        Dto.printMsg(response, msg.toString());
    }

    /**
     * 把机器码清空
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("upMachinenull.json")
    public void upMachinenull(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        JSONObject msg = new JSONObject();
        try {
            msbiz.upMachinenull(id);
            msg.element("code", "1");
        } catch (Exception e) {
            // TODO: handle exception
            msg.element("code", "0");
        }
        Dto.printMsg(response, msg.toString());
    }

    /**
     * 检查福袋分享完有没有插入数据
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("czJHGfd.json")
    public void czJHGfd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String type = msbiz.czJHGfd(id);
        Dto.printMsg(response, type);
    }

}
