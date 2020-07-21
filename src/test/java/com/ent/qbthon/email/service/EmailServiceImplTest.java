package com.ent.qbthon.email.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;


import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.ent.qbthon.email.entity.Event;
import com.ent.qbthon.email.entity.UserEvent;
import com.ent.qbthon.email.exception.EmailServiceException;
import com.ent.qbthon.email.model.EventUserDetails;
import com.ent.qbthon.email.repository.EventRepository;
import com.ent.qbthon.email.repository.UserEventRepository;
import com.ent.qbthon.email.repository.UserRepository;
import com.samskivert.mustache.MustacheException;



public class EmailServiceImplTest {

	@InjectMocks
	EmailServiceImpl emailServiceImpl;
	
	@Mock
	private EmailSender emailSender;
	
	@Mock
	private EventRepository eventRepository;
	
	@Mock
	private UserEventRepository userEventRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(emailServiceImpl, "dashBoardUrl", "http://localhost:4200/login");
	}

	Event getEvent() {
		Event event= new Event();
		event.setDate(Calendar.getInstance().getTime());
		event.setId("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		event.setSlot("morning");
		event.setSkills("java,c,html,css,angular");
		return event;
	}
	
	List<UserEvent> getUserEventList(){
		List<UserEvent> list= new ArrayList<>();
		UserEvent userEvent= new UserEvent();
		userEvent.setId("5c265bd5-c1c8-4c97-84f6-73319463224a");
		userEvent.setEventId("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		userEvent.setRole("USER");
		userEvent.setUserId("737230");
		list.add(userEvent);
		userEvent= new UserEvent();
		userEvent.setId("5c265bd5-c1c8-4c97-84f6-73319463224b");
		userEvent.setEventId("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		userEvent.setRole("USER");
		userEvent.setUserId("76923");
		list.add(userEvent);
		return list;
	}
	
	EventUserDetails getEventUserDetails() {
		List<String> users= new ArrayList<>();
		users.add("737230");
		users.add("736923");
		EventUserDetails eventUserDetails=EventUserDetails.builder()
				.date(Calendar.getInstance().getTime())
				.slot("morning")
				.skills("java,c,html")
				.usersList(users)
				.build();
		return eventUserDetails;
	}
	
	@Test
	public void testSendNominationMail() throws IOException {
		
		emailServiceImpl.sendNominationMail(getEventUserDetails());
		verify(emailSender,times(1)).sendNotificationMail(any(),any(), any(), any());
	}
	
	@Test
	public void testSendNominationMailevenSkill() throws IOException {
		emailServiceImpl.sendNominationMail(getEventUserDetails());
		verify(emailSender,times(1)).sendNotificationMail(any(),any(), any(), any());
	}

	@Test
	public void testSendRemainderMail() {
		Optional<Event> optionalEvent=Optional.of(getEvent());
		when(eventRepository.findById("40c71f4e-c8b8-4a40-9833-d7d1e775724a")).thenReturn(optionalEvent);
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",false)).thenReturn(optionalUserEvent);
		emailServiceImpl.sendRemainderMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(eventRepository,times(1)).findById(any());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}

	@Test(expected=EmailServiceException.class)
	public void testSendRemainderMailWithNoEvent() {
		emailServiceImpl.sendRemainderMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(eventRepository,times(1)).findById(any());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	@Test(expected=EmailServiceException.class)
	public void testSendRemainderMailWithEmailServiceException() {
		Optional<Event> optionalEvent=Optional.of(getEvent());
		when(eventRepository.findById("40c71f4e-c8b8-4a40-9833-d7d1e775724a")).thenReturn(optionalEvent);
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",false)).thenReturn(optionalUserEvent);
		doThrow(EmailServiceException.class).when(emailSender).sendRemainderMail(any(), any(), any());
		emailServiceImpl.sendRemainderMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(eventRepository,times(1)).findById(any());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	
	@Test(expected=EmailServiceException.class)
	public void testSendRemainderMailWithMustacheException() {
		Optional<Event> optionalEvent=Optional.of(getEvent());
		when(eventRepository.findById("40c71f4e-c8b8-4a40-9833-d7d1e775724a")).thenReturn(optionalEvent);
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",false)).thenReturn(optionalUserEvent);
		doThrow(MustacheException.class).when(emailSender).sendRemainderMail(any(), any(), any());
		emailServiceImpl.sendRemainderMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(eventRepository,times(1)).findById(any());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	
	@Test(expected=EmailServiceException.class)
	public void testSendRemainderMailNoNominatedUsers() {
		Optional<Event> optionalEvent=Optional.of(getEvent());
		when(eventRepository.findById("40c71f4e-c8b8-4a40-9833-d7d1e775724a")).thenReturn(optionalEvent);
		emailServiceImpl.sendRemainderMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(eventRepository,times(1)).findById(any());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}

	
	@Test
	public void testSendEducatorMail() {
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndReminderFlagAndNominatedAndRole("40c71f4e-c8b8-4a40-9833-d7d1e775724a", false, true, "USER")).thenReturn(optionalUserEvent);
		emailServiceImpl.sendEducatorMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(userEventRepository,times(1)).findByEventIdAndReminderFlagAndNominatedAndRole(any(),any(),any(),any());
	}

	@Test(expected = EmailServiceException.class)
	public void testSendEducatorMailWithNoPendingUsers() {
		List<UserEvent> list= new ArrayList<>();
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(list);
		when(userEventRepository.findByEventIdAndReminderFlagAndNominatedAndRole("40c71f4e-c8b8-4a40-9833-d7d1e775724a", false, true, "USER")).thenReturn(optionalUserEvent);
		emailServiceImpl.sendEducatorMail("40c71f4e-c8b8-4a40-9833-d7d1e775724a");
		verify(userEventRepository,times(1)).findByEventIdAndReminderFlagAndNominatedAndRole(any(),any(),any(),any());
	}

	@Test
	public void testSendStartEventMail() {
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.sendStartEventMail(getEvent());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	
	@Test(expected = EmailServiceException.class)
	public void testSendStartEventMailWithNoNominatedUsers() {
		Optional<List<UserEvent>> optionalUserEvent = Optional.empty();
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.sendStartEventMail(getEvent());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}

	@Test
	public void testSendFeedBackMail() {
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.sendFeedBackMail(getEvent());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	
	@Test(expected = EmailServiceException.class)
	public void testSendFeedBackMailWithNoNominatedUsers() {
		Optional<List<UserEvent>> optionalUserEvent = Optional.empty();
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.sendFeedBackMail(getEvent());
		verify(userEventRepository,times(1)).findByEventIdAndRoleAndNominated(any(),any(),any());
	}
	
	@Test
	public void testScheduleEventStartMail() {
		Date date= new Date();
		Format hourFormatter = new SimpleDateFormat("HH");
		String hour=hourFormatter.format(date);
		ReflectionTestUtils.setField(emailServiceImpl, "startMorning", hour);
		List<Event> list= new ArrayList<>();
		list.add(getEvent());
		when(eventRepository.findAll()).thenReturn(list);
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.scheduleEventStartMail();
		verify(eventRepository,times(1)).findAll();
	}
	
	@Test
	public void testschedulefeedBackMail() {
		Date date= new Date();
		Format hourFormatter = new SimpleDateFormat("HH");
		String hour=hourFormatter.format(date);
		ReflectionTestUtils.setField(emailServiceImpl, "endNoon", hour);
		List<Event> list= new ArrayList<>();
		Event noonEvent =getEvent();
		noonEvent.setSlot("noon");
		list.add(noonEvent);
		when(eventRepository.findAll()).thenReturn(list);
		Optional<List<UserEvent>> optionalUserEvent = Optional.of(getUserEventList());
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.schedulefeedBackMail();
		verify(eventRepository,times(1)).findAll();
	}
	
	@Test
	public void testschedulefeedBackMailWithNoEvent() {
		Date date= new Date();
		Format hourFormatter = new SimpleDateFormat("HH");
		String hour=hourFormatter.format(date);
		ReflectionTestUtils.setField(emailServiceImpl, "endNoon", hour);
		List<Event> list= new ArrayList<>();
		when(eventRepository.findAll()).thenReturn(list);
		Optional<List<UserEvent>> optionalUserEvent = Optional.empty();
		when(userEventRepository.findByEventIdAndRoleAndNominated("40c71f4e-c8b8-4a40-9833-d7d1e775724a", "USER",true)).thenReturn(optionalUserEvent);
		emailServiceImpl.schedulefeedBackMail();
		verify(eventRepository,times(1)).findAll();
	}

}
