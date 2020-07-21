package com.ent.qbthon.email.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.ent.qbthon.email.exception.EmailServiceException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EmailSenderImpl implements EmailSender {
	@Autowired
	private JavaMailSender emailSender;

	private static final String COGNIZANT_MAIL="cognizantlearn@cognizant.com";
	
	private static final String SOMETHING_WENT_WRONG = "Something Went Wrong";
	
	private static final String TEMPLATES = "templates/";
	
	private static final String TEMP = "/temp/";
	
	private static final String REMAINDER_IMAGE7="/temp/imagesRemainder/image007.gif";
	
	private static final String REMAINDER_IMAGE9="/temp/imagesRemainder/image009.gif";
	
	@Override
	public void sendNotificationMail(String emailBody, String subject,String imageIdentifier,List<String> users) {

		try {
			MimeMessagePreparator preparator = mimeMessage -> {
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true);
				msg.setFrom(COGNIZANT_MAIL);
				if(users!=null&&!users.isEmpty())
				{
					msg.setTo(users.stream().toArray(String[]::new));
				}
				msg.setSubject(subject);
				msg.setText(emailBody, true);
				getImagesForMsg(imageIdentifier, msg, "images", "/image004.jpg");
			};
			log.info("Sending Notifiction Mail");
			emailSender.send(preparator);
		} catch (Exception e) {
			log.error("sendNotificationMail Error:::",Arrays.toString(e.getStackTrace()));
			throw new EmailServiceException(SOMETHING_WENT_WRONG,e);
		}
	}

	@Override
	public void sendRemainderMail(String emailBody, String imageIdentifier,List<String> users) {
		
		log.info("Remainderusers->"+users.toString());
		try {
			MimeMessagePreparator preparator = mimeMessage -> {
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true);
				msg.setFrom(COGNIZANT_MAIL);
				if(users!=null&&!users.isEmpty()) {
					msg.setTo(users.stream().toArray(String[]::new)); 
				}
				msg.setSubject("QBthon Nominations - When are you going to nominate?");
				msg.setText(emailBody, true);
				InputStream templateFileInputStream=null;
				getImagesForMsg(imageIdentifier, msg, "imagesRemainder", "/image005.gif");
				templateFileInputStream = new ClassPathResource("templates/imagesRemainder/image007.gif").getInputStream();
				File image5 = new File(REMAINDER_IMAGE7);
				FileUtils.copyInputStreamToFile(templateFileInputStream, image5);
				msg.addInline(imageIdentifier + "4", image5);
				templateFileInputStream = new ClassPathResource("templates/imagesRemainder/image009.gif").getInputStream();
				File image6 = new File(REMAINDER_IMAGE9);
				FileUtils.copyInputStreamToFile(templateFileInputStream, image6);
				msg.addInline(imageIdentifier + "5", image6);
				
			};
			emailSender.send(preparator);
		} catch (Exception e) {
			log.error("sendRemainderMail Error:::",Arrays.toString(e.getStackTrace()));
			throw new EmailServiceException(SOMETHING_WENT_WRONG,e);
		}

	}

	@Override
	public void sendEducatorMail(String emailBody,String subject,List<String> users) {
		try {
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true);
			msg.setFrom(COGNIZANT_MAIL);
			if(users!=null&&!users.isEmpty())
			{
				msg.setTo(users.stream().toArray(String[]::new));
			}
			msg.setSubject(subject);
			msg.setText(emailBody, true);
		};
		emailSender.send(preparator);
	} catch(Exception e) {
		log.error("sendEducatorMail Error:::",Arrays.toString(e.getStackTrace()));
		throw new EmailServiceException(SOMETHING_WENT_WRONG,e);
	}
	}
	

	private void getImagesForMsg(String imageIdentifier, MimeMessageHelper msg, String folder, String image004) throws IOException, MessagingException {
		InputStream templateFileInputStream;
		templateFileInputStream = new ClassPathResource(TEMPLATES + folder +"/image001.jpg").getInputStream();
		File image1 = new File(TEMP+ folder +"/image001.jpg");
		FileUtils.copyInputStreamToFile(templateFileInputStream, image1);
		msg.addInline(imageIdentifier, image1);
		templateFileInputStream = new ClassPathResource(TEMPLATES + folder +"/image002.jpg").getInputStream();
		File image2 = new File(TEMP+ folder +"/image002.jpg");
		FileUtils.copyInputStreamToFile(templateFileInputStream, image2);
		msg.addInline(imageIdentifier + "1", image2);
		templateFileInputStream = new ClassPathResource(TEMPLATES + folder +"/image003.jpg").getInputStream();
		File image3 = new File(TEMP+ folder +"/image003.jpg");
		FileUtils.copyInputStreamToFile(templateFileInputStream, image3);
		msg.addInline(imageIdentifier + "2", image3);
		templateFileInputStream = new ClassPathResource(TEMPLATES + folder + image004).getInputStream();
		File image4 = new File(TEMP+ folder  + image004);
		FileUtils.copyInputStreamToFile(templateFileInputStream, image4);
		msg.addInline(imageIdentifier + "3", image4);
	}
	
	
		
}
