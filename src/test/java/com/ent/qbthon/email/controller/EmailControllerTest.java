package com.ent.qbthon.email.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ent.qbthon.email.model.EventUserDetails;
import com.ent.qbthon.email.service.EmailService;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class EmailControllerTest {

	private MockMvc mockMvc;
	
	@InjectMocks
	private EmailController emailController;
	
	@Mock
	private EmailService emailService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
	}

	@Test
	public void testSendNominationMail() throws IOException {
		List<String> users= new ArrayList<>();
		users.add("737230");
		users.add("736923");
		EventUserDetails eventUserDetails=EventUserDetails.builder()
				.date(Calendar.getInstance().getTime())
				.slot("morning")
				.skills("java,c")
				.usersList(users)
				.build();
		ResponseEntity<String> response= emailController.sendNominationMail(eventUserDetails);
		assertEquals("success",response.getBody());
	}

	@Test
	public void testSendRemainderMail() {
		ResponseEntity<String> response= emailController.sendRemainderMail("eventId");
		assertEquals("success",response.getBody());
	}

	@Test
	public void testSendEducatorMail() {
		ResponseEntity<String> response= emailController.sendEducatorMail("eventId");
		assertEquals("success",response.getBody());
	}

}
