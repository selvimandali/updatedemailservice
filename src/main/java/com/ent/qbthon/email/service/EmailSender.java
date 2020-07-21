package com.ent.qbthon.email.service;

import java.util.List;

public interface EmailSender {
	void sendNotificationMail(String emailBody ,String subject, String imageIdentifier,List<String> users);
	void sendRemainderMail(String emailBody , String imageIdentifier,List<String> users) ;
	void sendEducatorMail(String emailBody,String subject,List<String> users);
}
