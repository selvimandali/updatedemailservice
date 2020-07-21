package com.ent.qbthon.email.service;

import java.io.IOException;

import com.ent.qbthon.email.entity.Event;
import com.ent.qbthon.email.model.EventUserDetails;

public interface EmailService {
	void sendNominationMail(EventUserDetails eventDetails) throws IOException;
	void sendRemainderMail(String eventId);
	void sendEducatorMail(String eventId) ;
	void sendStartEventMail(Event event);
	void sendFeedBackMail(Event event);
}
