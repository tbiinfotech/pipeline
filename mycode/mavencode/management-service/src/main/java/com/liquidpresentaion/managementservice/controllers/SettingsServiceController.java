package com.liquidpresentaion.managementservice.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.managementservice.model.Message;
import com.liquidpresentaion.managementservice.model.Presentation;
import com.liquidpresentaion.managementservice.model.Settings;
import com.liquidpresentaion.managementservice.model.User;
import com.liquidpresentaion.managementservice.service.SettingsService;
import com.liquidpresentation.common.utils.UrlUtil;

@RestController
@RequestMapping("internal/v1/settings")
public class SettingsServiceController {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;
	@Value("${urlHost}")
	private String urlHost;
	
	@Autowired
	private SettingsService settingsService;

	@RequestMapping(method = RequestMethod.GET)
	public Settings getUser() {
		return settingsService.getSettings(1);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void updateUser(@RequestBody Settings settings) {
		settingsService.updateSettings(settings);
	}

	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void sendMessage(@RequestBody Message message) throws InterruptedException {
		settingsService.sendMessage(message);
	}
	
	@GetMapping(value = "/maxfilesize")
	public String getmaxFileSize() {
		return maxFileSize;
	}
	
	@PostMapping(value = "/simplemailmessage")
	public void sendMessage(@RequestParam(name = "to", required = true)String to,
			@RequestParam(name = "title", required = true)String title,
			@RequestParam(name = "message", required = true)String message) {
		settingsService.sendSimpleMessage(to, title, message);
	}
	
	@PostMapping(value = "/contactus")
	public void contactUs(@RequestBody Map<String, String> map) {
		StringBuilder body = new StringBuilder("Name: " + map.get("name"));
		body.append("\n");
		body.append("Company: " + map.get("company"));
		body.append("\n");
		body.append("Email: " + map.get("email"));
		body.append("\n");
		body.append("Message: " + map.get("message"));
		settingsService.contactUs(body.toString());
	}
	
	
	@RequestMapping(value = "/sendemailwithpdf", method = RequestMethod.POST)
	public void sendEmailWithPdf(@RequestBody Presentation presentation) throws IOException {
		settingsService.sendEmailWithPdf(presentation);
	}
	
	@RequestMapping(value = "/contactus/email/recoveryCode", method = RequestMethod.POST)
	public void getRecoverCodeBySendEmail(HttpServletRequest request, @RequestParam(name = "email") String email){
		String title = "Liquid Presentation LLC Password Recovery Robot";
		String recoveryCode = UUID.randomUUID().toString(); // recovery code
		User user = settingsService.updateUserRecoveryCode(email, recoveryCode);
		if(user == null || StringUtils.isBlank(user.getEmail())) { // update user's recoveryCode failed
			throw new RuntimeException("Failed to send the email.");
		}
		String signStr = recoveryCode + "_" + email;
		String sign = UrlUtil.enCryptAndEncode(signStr); // encrypt recovery code
		String UrlTemp = urlHost + "/#/Recover";
		String url = UrlTemp + "?username=" + email + "&recoveryCode=" + recoveryCode + "&sign=" + sign; // prepare access url
		StringBuilder message = new StringBuilder();
		message.append("Recovery URL: " + url);
/*		message.append("\n");
		message.append("Recovery Code: " + recoveryCode);*/
		settingsService.sendSimpleMessage(email, title, message.toString());
	}
	
    private static String getCurrentLocation(String urlHost) {
        String url = "http://123.56.105.227:9100/#/"; // ali cloud
//        String url = "http://liquidpresentation.s3-website.us-east-2.amazonaws.com/#/"; // amazonaws
        return url;
    }
    
    @RequestMapping(value = "/email/notification", method = RequestMethod.POST)
    public void sendEmailNotification(@RequestParam("subject") String subject, 
    								  @RequestParam("content") String content, 
    								  @RequestParam("email") String email) {
    	settingsService.sendSimpleCopyMessage(email,subject,content);
    }
	@RequestMapping(value = "/send/upgrade/email", method = RequestMethod.GET)
	public Map<String, List<String>> sendIngredientUpgradeEmail(@RequestParam(name = "newBrandName") String newBrandName) throws IOException {
		return settingsService.sendIngredientUpgradeEmail(newBrandName);
	}
}
