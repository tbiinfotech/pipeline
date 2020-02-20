package com.liquidpresentaion.managementservice.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.managementservice.model.Message;
import com.liquidpresentaion.managementservice.model.Presentation;
import com.liquidpresentaion.managementservice.model.Settings;
import com.liquidpresentaion.managementservice.model.User;
import com.liquidpresentaion.managementservice.repository.SettingsRepository;

@Service
public class SettingsService {

	@Autowired
	private SettingsRepository settingsRepository;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private FeignClientService feignClientService;

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    		
	public void updateSettings(Settings settings) {
		settingsRepository.save(settings);
	}

	public Settings getSettings(int i) {
		return settingsRepository.findById(i).get();
	}

	public Map<String, List<String>> sendIngredientUpgradeEmail(String newBrandName){
		Map<String, List<String>> receivers = feignClientService.findIngredientUpgradeMailList();
		
		if (receivers.isEmpty()) {
			logger.error("Mail list is empty for upgraded brand :[" + newBrandName + "]");
		} else {
			List<String> currentUser = receivers.get("currentUser");
			
			String subject = "\"" + newBrandName + "\" Has Been Created into a Brand";
			String text = "\"" + newBrandName + "\" Has Been Created into a Brand by " + currentUser.get(0) + ", " 
							+ currentUser.get(1) + ", " + currentUser.get(2) + ".";
			String[] to = receivers.get("administrators").toArray(new String[0]);
			String[] cc = receivers.get("distributorMixologists").toArray(new String[0]);
			
			this.sendMimeMessage(to, cc, subject, text);
		}
		
		return receivers;
	}
	
	public void sendMimeMessage(String[] to, String[] cc, String subject, String htmlText) {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(to);
			helper.setCc(cc);
			helper.setText(htmlText, true);
			helper.setSubject(subject);
			Settings mailSetting = getSettings(1);
			helper.setFrom(mailSetting.getSmtpUser());
			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.error("Failed to send email to [" + to + "], email subject [" + subject + "]" + ", email content [" + htmlText + "]");
		}

	}

	public void sendMessage(Message message) throws InterruptedException {
		String subject = message.getTitle();
		String text = message.getMessage();
		List<User> users = feignClientService.findUsersByGroups(message.getGroups());
		for (User user : users) {
			this.sendSimpleMessage(user.getEmail(), subject, text);
		}
	}

	public void sendSimpleMessage(String to, String subject, String text, Boolean... isCopy) {
		SimpleMailMessage message = new SimpleMailMessage();
		// TODO
		Settings mailSetting = getSettings(1);
		if (StringUtils.isEmpty(to)) {
			message.setTo(mailSetting.getContactUs());
		} else {
			message.setTo(to);
		}
		message.setFrom(mailSetting.getSmtpUser());
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}
	
	public void sendSimpleCopyMessage(String to, String subject, String text) {
		try {
			Settings mailSetting = getSettings(1);
			MimeMessage msg = emailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(msg,true,"UTF-8");
	        messageHelper.setFrom(mailSetting.getSmtpUser());	//设置发件人Email
	        messageHelper.setTo(to);	//设定收件人Email
	        messageHelper.setSubject(subject);	//设置邮件主题
	        String html = "<html> <body> "+text+" </body></html>";
	        messageHelper.setText(html,true);	//设置邮件主题内容
			emailSender.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 利用helper设置各种邮件发送相关的信息
	 */
	private MimeMessageHelper setInfoByHelper(String to, String subject, String content, MimeMessage message) throws MessagingException {
		// true表示需要创建一个multipart message
		Settings mailSetting = getSettings(1);
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(mailSetting.getSmtpUser());
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, true);
		return helper;
	}

	/**
	 * 发送带附件的邮�?
	 * 
	 * @param to
	 *            收件人列�?
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 * @param inputStreamSource
	 *            附件streamSource，可以这样获得：new
	 *            ByteArrayResource(ByteArrayOutputStream.toByteArray());
	 * @param fileName
	 *            附件的文件名
	 */
	public void sendAttachmentsMail(String to, String subject, String content, InputStreamSource inputStreamSource, String fileName) {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = setInfoByHelper(to, subject, content, message);
			helper.addAttachment(fileName, inputStreamSource);
			emailSender.send(message);
		} catch (MessagingException e) {
		}
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		Settings mailSetting = getSettings(1);
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailSetting.getSmtpServer());
		mailSender.setPort(mailSetting.getSmtpPort());

		mailSender.setUsername(mailSetting.getSmtpUser());
		mailSender.setPassword(mailSetting.getSmtpPassword());

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", mailSetting.isSmtpUseTls());
		props.put("mail.debug", "true");
		// TODO
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		return mailSender;
	}

	public void contactUs(String body) {
		sendSimpleMessage(null, "Contact Us", body);
	}

	public void sendMailWithExcel() throws IOException {
		String[] headers = { "col1", "col2", "col3" };
		// 声明一个工作薄
		HSSFWorkbook wb = new HSSFWorkbook();
		// 生成一个表�?
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
		}
		int rowIndex = 1;

		for (int j = 0; j < 3; j++) {
			row = sheet.createRow(rowIndex);
			rowIndex++;
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(j);
			cell1 = row.createCell(1);
			cell1.setCellValue(j + 1);
			cell1 = row.createCell(2);
			cell1.setCellValue(j + 2);
		}
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream(1000);
		wb.write(os);
		wb.close();
		InputStreamSource iss = new ByteArrayResource(os.toByteArray());
		os.close();
		this.sendAttachmentsMail("fuyu_oo@163.com", "attachmentMail subject", "I have an attachment", iss, "abc1.xlsx");
	}

	public void sendEmailWithPdf(Presentation presentation) throws IOException {
		ResponseEntity<byte[]> is = feignClientService.getPresentationPdf(presentation);
		InputStreamSource iss = new ByteArrayResource(is.getBody());
		this.sendAttachmentsMail(presentation.getTo(), presentation.getSubject(), presentation.getMessage(), iss,presentation.getTitle() + ".pdf");
	}
	
	public User updateUserRecoveryCode(String email, String recoveryCode) {
		return feignClientService.updateUserRecoveryCode(email, recoveryCode);
	}
}
