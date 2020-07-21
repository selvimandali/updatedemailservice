package com.ent.qbthon.email.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ent.qbthon.email.model.EventUserDetails;
import com.ent.qbthon.email.service.EmailService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
//@RequestMapping("/email")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	private static final String SUCCESS="success";
	@PostMapping("/sendNominationMail")
	public ResponseEntity<String> sendNominationMail(@RequestBody(required= true) EventUserDetails eventDetails) throws  IOException {
		log.info("eventDetails->"+eventDetails.toString());
		emailService.sendNominationMail(eventDetails);
		return  new ResponseEntity<>(SUCCESS, HttpStatus.OK);
	}
	@GetMapping(value = "/sendRemainderMail/{eventId}")
	public ResponseEntity<String> sendRemainderMail(@PathVariable String eventId){
		emailService.sendRemainderMail(eventId);
		return new ResponseEntity<>(SUCCESS,HttpStatus.OK);
	}
	@GetMapping("/sendEducatorMail/{eventId}")
	public ResponseEntity<String> sendEducatorMail(@PathVariable String eventId) {
		emailService.sendEducatorMail(eventId);
		return new ResponseEntity<>(SUCCESS,HttpStatus.OK);
	}

}
