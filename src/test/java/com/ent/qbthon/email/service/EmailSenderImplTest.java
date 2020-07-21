package com.ent.qbthon.email.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.ent.qbthon.email.exception.EmailServiceException;

public class EmailSenderImplTest {

	@InjectMocks
	private EmailSenderImpl emailSenderImpl;
	
	@Mock
	private JavaMailSender emailSender;
	
	@Mock
    private MimeMessageHelper mimeMessageHelper;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	List<String> getUsers(){
		List<String> list= new ArrayList<>();
		list.add("Selvi.Mandali@cognizant.com");
		return list;
	}
	
	@Test
	public void testSendNotificationMail() {
		emailSenderImpl.sendNotificationMail("emailbody", "notificationMail", UUID.randomUUID().toString(), getUsers());
		verify(emailSender,times(1)).send(any(MimeMessagePreparator.class));
	}

	@Test(expected = EmailServiceException.class)
	public void testSendNotificationMailWithException() {
		emailSenderImpl.sendNotificationMail(any(), any(), any(), any());
		verify(emailSender,times(1)).send(any(MimeMessagePreparator.class));
	}

	@Test
	public void testSendRemainderMail() {
		emailSenderImpl.sendRemainderMail("emailbody", UUID.randomUUID().toString(), getUsers());
		verify(emailSender,times(1)).send(any(MimeMessagePreparator.class));
	}

	@Test(expected = EmailServiceException.class)
	public void testSendRemainderMailWithException() {
		emailSenderImpl.sendRemainderMail(any(),any(),getUsers());
		verify(emailSender,times(0)).send(any(MimeMessagePreparator.class));
	}
	
	@Test
	public void testSendEducatorMail() {
		emailSenderImpl.sendEducatorMail("emailBody", "subject", getUsers());
		verify(emailSender,times(1)).send(any(MimeMessagePreparator.class));
	}
	
	@Test(expected = EmailServiceException.class)
	public void testSendEducatorMailWithException() {
		emailSenderImpl.sendEducatorMail(any(), any(), any());
		verify(emailSender,times(1)).send(any(MimeMessagePreparator.class));
	}
}
