package com.dxd.poc.greetingrestservice.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dxd.poc.greetingrestservice.bean.GreetingBean;

@RestController
public class GreetingController {

	@Value("${config.greeting}")
	private String greeting;

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public GreetingBean home(Model model, HttpServletRequest request) {

		GreetingBean greetingBean = new GreetingBean();
		greetingBean.setGreeting(greeting);
		greetingBean.setTimeStamp(new Date().getTime());

		return greetingBean;
	}
}