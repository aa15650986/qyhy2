package com.qy.game.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wxtest")
public class WxTestServer {
	
	
	@RequestMapping(value = "/getMsg",method = RequestMethod.GET)
	public void getMsg(@RequestParam Object code,@RequestParam String state) {
		System.out.println("===============================");
		System.out.println(code);
		System.out.println(state);
		System.out.println("===============================");
	}

}
