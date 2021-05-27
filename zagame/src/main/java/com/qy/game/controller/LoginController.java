package com.qy.game.controller;



import com.qy.game.utils.Dto;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletResponse;


public class LoginController {

    @RequestMapping("/getABC")
    public void abc(HttpServletResponse response){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","1");
        Dto.returnJosnMsg(response,jsonObject);
    }
}
