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

	@Value("${config.revision}")
	private String revision;

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public GreetingBean home(Model model, HttpServletRequest request) {

		GreetingBean greetingBean = new GreetingBean();
		greetingBean.setGreeting(greeting);
		greetingBean.setTimeStamp(new Date().getTime());
		greetingBean.setRevision(revision);

		return greetingBean;
	}
}