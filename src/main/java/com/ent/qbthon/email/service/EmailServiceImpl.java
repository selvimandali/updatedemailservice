package com.ent.qbthon.email.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ent.qbthon.email.entity.Event;
import com.ent.qbthon.email.entity.User;
import com.ent.qbthon.email.entity.UserEvent;
import com.ent.qbthon.email.exception.EmailServiceException;
import com.ent.qbthon.email.model.EventUserDetails;
import com.ent.qbthon.email.repository.EventRepository;
import com.ent.qbthon.email.repository.UserEventRepository;
import com.ent.qbthon.email.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserEventRepository userEventRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Value("${url.dashBoard}")
	private String dashBoardUrl;
	
	@Value("${hour.startMorning}")
	private String startMorning;
	
	@Value("${hour.startNoon}")
	private String startNoon;
	
	@Value("${hour.endMorning}")
	private String endMorning;
	
	@Value("${hour.endNoon}")
	private String endNoon;
	
    private static final String SOMETHING_WENT_WRONG = "Something Went Wrong";
    
    private static final String BACKGROUND = " background:#0033CC;padding:0in 5.4pt 0in 5.4pt;height:20.1pt'>\r\n";
    
    private static final String SPAN = "      <p class=MsoNormal><b><span style='font-family:\"Arial\",sans-serif;\r\n";
    
    private static final String IMAGE1 = "image1";
    
    private static final String IMAGE2 = "image2";
    
    private static final String IMAGE3 = "image3";
    
    private static final String DASHBOARD = "dashBoard";
    
    private static final String NOMINATION_COLOUMN1_START= "<td width=300 style='width:225.0pt;border-top:solid #C9C9C9 1.0pt;\r\n"
			+ "      border-left:none;border-bottom:solid white 1.0pt;border-right:none;\r\n"
			+ BACKGROUND
			+ SPAN
			+ "      color:#1F497D'>&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;</span></b><b><span\r\n"
			+ "      style='font-family:\"Arial\",sans-serif;color:white'>" ;
    
    private static final String NOMINATION_COLOUMN2_START= "<td width=396 style='width:297.0pt;border-top:solid #C9C9C9 1.0pt;\r\n"
			+ "      border-left:none;border-bottom:solid white 1.0pt;border-right:none;\r\n"
			+ BACKGROUND
			+ SPAN
			+ "      color:white'>";
    
    private static final String REMAINDER_COLOUMN1_START = "<td width=301 colspan=2 style='width:225.0pt;border:none;border-bottom:\r\n"
			+ "      solid white 1.0pt;background:#0033CC;padding:0in 5.4pt 0in 5.4pt;\r\n"
			+ "      height:20.1pt'>\r\n"
			+ SPAN
			+ "      color:#1F497D'>&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></b><b><span\r\n"
			+ "      style='font-family:\"Arial\",sans-serif;color:white'>";
    
    private static final String REMAINDER_COLOUMN2_START = "<td width=393 style='width:297.0pt;border:none;border-bottom:solid white 1.0pt;\r\n"
			+ BACKGROUND
			+ SPAN
			+ "      color:white'>";
    
    private static final String COLOUMN1_END = "<o:p></o:p></span></b></p>\r\n" + "      </td>";
    
    private static final String COLOUMN2_END = "<o:p></o:p></span></b></p>\r\n" + "      </td>\r \n"
			+ "	</tr>";
    
    private static final String NOMINATION_ROW_END = "</tr>";
    
    private static final String REMAINDER_ROW_END  = "<td width=3 style='width:.75pt;padding:0in 0in 0in 0in;height:20.1pt'>\r\n"
			+ "      <p class=MsoNormal>&nbsp;</p>\r\n" + "      </td>\r\n" + "     </tr>";
    
	@Override
	public void sendNominationMail(EventUserDetails eventDetails) throws IOException {
		InputStream templateFileInputStream;
		Map<String, String> eventDetailsPropertyMap = processEventDetails(eventDetails);
		getHTMLMetaData();
		String templateFileName = "templates/nominations.htm";
		templateFileInputStream = new ClassPathResource(templateFileName).getInputStream();
		File templateFile=new File("/temp/nominations.htm");
		FileUtils.copyInputStreamToFile(templateFileInputStream,templateFile);
		FileReader reader = new FileReader(templateFile);
		Template template = Mustache.compiler().compile(reader);
		String emailBody = template.execute(eventDetailsPropertyMap);
		emailSender.sendNotificationMail(emailBody, "QBthon Nominations - Here are the skill areas!",
				eventDetailsPropertyMap.get(IMAGE1),getEmaiIdsforEmpIds(eventDetails.getUsersList()));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> processEventDetails(EventUserDetails eventDetails) {
		Map<String, String> eventDetailsPropertyMap;
		ObjectMapper objectMapper = new ObjectMapper();
		Date date = eventDetails.getDate();
		Format monthFormatter = new SimpleDateFormat("MMMM");
		String month = monthFormatter.format(date);
		Format dayFormatter = new SimpleDateFormat("dd");
		String day = dayFormatter.format(date);
		eventDetailsPropertyMap = objectMapper.convertValue(eventDetails, Map.class);
		eventDetailsPropertyMap.put("day", day);
		eventDetailsPropertyMap.put("month", month);
		eventDetailsPropertyMap.put(DASHBOARD,dashBoardUrl);
		StringBuilder skillTable = new StringBuilder();
		skillTable.append(
				"<table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0 width=0 style='width:7.25in;border-collapse:collapse;mso-yfti-tbllook:1184;mso-padding-alt:0in 0in 0in 0in'>");
		formTable(eventDetails, skillTable, NOMINATION_COLOUMN1_START, NOMINATION_COLOUMN2_START, NOMINATION_ROW_END);
		skillTable.append("</table>");
		eventDetailsPropertyMap.put("skillTable", skillTable.toString());
		String imageIdentifier = UUID.randomUUID().toString();
		eventDetailsPropertyMap.put(IMAGE1, imageIdentifier);
		eventDetailsPropertyMap.put(IMAGE2, imageIdentifier + "1");
		eventDetailsPropertyMap.put(IMAGE3, imageIdentifier + "2");
		return eventDetailsPropertyMap;
	}

	@Override
	public void sendRemainderMail(String eventId) {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		Optional<List<UserEvent>> optionalUserEvent = userEventRepository.findByEventIdAndRoleAndNominated(eventId, "USER",false);
		Event event = null;
		List<UserEvent> userEventList ;
		List<String> users = new ArrayList<>();
		if (optionalEvent.isPresent()) {
			event = optionalEvent.get();
		} else {
			throw new EmailServiceException("no event with the given eventId " + eventId);
		}
		if (optionalUserEvent.isPresent()&&!optionalUserEvent.get().isEmpty()) {
			userEventList = optionalUserEvent.get();
			userEventList.stream().forEach(userEvent -> users.add(userEvent.getUserId()));
		} else {
			throw new EmailServiceException("no users for the event with the given eventId " + eventId);
		}
		InputStream templateFileInputStream;
		try {
			EventUserDetails eventDetails = EventUserDetails.builder().skills(event.getSkills()).usersList(users)
					.build();
			Map<String, String> eventDetailsPropertyMap = processRemainderDetails(eventDetails);
			getHTMLMetaData();
			String templateFileName = "templates/remainder.htm";
			templateFileInputStream = new ClassPathResource(templateFileName).getInputStream();
			File templateFile = new File("/temp/remainder.htm");
			FileUtils.copyInputStreamToFile(templateFileInputStream, templateFile);
			try(FileReader reader = new FileReader(templateFile);){
			Template template = Mustache.compiler().compile(reader);
			String emailBody = template.execute(eventDetailsPropertyMap);
			emailSender.sendRemainderMail(emailBody, eventDetailsPropertyMap.get(IMAGE1), getEmaiIdsforEmpIds(users));
			}
		} catch (EmailServiceException ex) {
			log.error("sendRemainderMail EmailServiceException Error:::",ex.getStackTrace());
			throw ex;
		}catch (IOException ioEx) {
			log.error("sendRemainderMail IOException Error:::",ioEx.getStackTrace());
			throw new EmailServiceException(
					"There Is A Problem In Feting The File At Location 'templates/QBthon Nominations - Here is the technical scope!.htm'");

		} catch (MustacheException mEx) {
			log.error("sendRemainderMail MustacheException Error:::",mEx.getStackTrace());
			throw new EmailServiceException(
					"An exception thrown when an error occurs parsing or executing a Mustache template 'templates/QBthon Nominations - Here is the technical scope!.htm'");
		} catch (Exception ex) {
			log.error("sendRemainderMail Error:::",ex.getStackTrace());
			throw new EmailServiceException(SOMETHING_WENT_WRONG,ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> processRemainderDetails(EventUserDetails eventDetails) {
		Map<String, String> eventDetailsPropertyMap;
		ObjectMapper objectMapper = new ObjectMapper();
		eventDetailsPropertyMap = objectMapper.convertValue(eventDetails, Map.class);
		StringBuilder skillTable = new StringBuilder();
		formTable(eventDetails, skillTable, REMAINDER_COLOUMN1_START, REMAINDER_COLOUMN2_START, REMAINDER_ROW_END);
		eventDetailsPropertyMap.put("skillTable", skillTable.toString());
		String imageIdentifier = UUID.randomUUID().toString();
		eventDetailsPropertyMap.put(IMAGE1, imageIdentifier);
		eventDetailsPropertyMap.put(IMAGE2, imageIdentifier + "1");
		eventDetailsPropertyMap.put(IMAGE3, imageIdentifier + "2");
		eventDetailsPropertyMap.put("image4", imageIdentifier + "3");
		eventDetailsPropertyMap.put("image5", imageIdentifier + "4");
		eventDetailsPropertyMap.put("image6", imageIdentifier + "5");
		eventDetailsPropertyMap.put(DASHBOARD,dashBoardUrl);

		return eventDetailsPropertyMap;
	}

	@Override
	public void sendEducatorMail(String eventId){
		String templateFileName = null;
		File templateFile = null;
		String str = null;
		List<UserEvent> userEvents;
		List<String> users = new ArrayList<>();
		Optional<List<UserEvent>> userEventList = userEventRepository.findByEventIdAndReminderFlagAndNominatedAndRole(eventId, false, true, "USER");
		if (userEventList.isPresent()&&!userEventList.get().isEmpty()) {
			userEvents = userEventList.get();
			userEvents.stream().forEach(userEvent -> users.add(userEvent.getUserId()));
			InputStream templateFileInputStream;
			try {
				getHTMLMetaData();
				templateFileInputStream = new ClassPathResource("templates/images/image001.jpg").getInputStream();
				File image1 = new File("/temp/images/image001.jpg");
				FileUtils.copyInputStreamToFile(templateFileInputStream, image1);
				templateFileInputStream = new ClassPathResource("templates/images/image002.jpg").getInputStream();
				File image2 = new File("/temp/images/image002.jpg");
				FileUtils.copyInputStreamToFile(templateFileInputStream, image2);
				templateFileInputStream = new ClassPathResource("templates/images/image003.jpg").getInputStream();
				File image3 = new File("/temp/images/image003.jpg");
				FileUtils.copyInputStreamToFile(templateFileInputStream, image3);
				templateFileInputStream = new ClassPathResource("templates/images/image005.jpg").getInputStream();
				File image5 = new File("/temp/images/image005.jpg");
				FileUtils.copyInputStreamToFile(templateFileInputStream, image5);
				templateFileName = "templates/anatomy.htm";
				templateFileInputStream = new ClassPathResource(templateFileName).getInputStream();
				templateFile=new File("/temp/anatomy.htm");
				FileUtils.copyInputStreamToFile(templateFileInputStream, templateFile);
				str = FileUtils.readFileToString(templateFile, "UTF-8");
				emailSender.sendEducatorMail(str, "QBthon - Know the anatomy of an MCQ", getEmaiIdsforEmpIds(users));
				templateFileName = "templates/dosdonts.htm";
				templateFileInputStream = new ClassPathResource(templateFileName).getInputStream();
				templateFile=new File("/temp/dosdonts.htm");
				FileUtils.copyInputStreamToFile(templateFileInputStream, templateFile);
				str = FileUtils.readFileToString(templateFile, "UTF-8");
				emailSender.sendEducatorMail(str, "QBthon - MCQ creation - DOs and DONTs (contd.)", getEmaiIdsforEmpIds(users));
				List<UserEvent> list = userEventList.get().stream().map(userEvent -> {
					userEvent.setReminderFlag(true);
					return userEvent;
				}).collect(Collectors.toList());
				userEventRepository.saveAll(list);
			} catch (IOException iex) {
				log.error("sendEducatorMail Error:::",iex.getStackTrace());
				throw new EmailServiceException("There Is An Error In Fecting File " + templateFileName);
			} catch (EmailServiceException ex) {
				log.error("sendEducatorMail Error:::",ex.getStackTrace());
				throw ex;
			}
		}else {
			throw new EmailServiceException("No Pending Users For The Event To Send Educator Mail");
		}
	}
	
	@Override
	public void sendStartEventMail(Event event) {
		Map<String, String> eventDetailsPropertyMap = new HashMap<>();
		Date date = event.getDate();
		Format monthFormatter = new SimpleDateFormat("MMMM");
		String month = monthFormatter.format(date);
		eventDetailsPropertyMap.put("month", month);
		Format dayFormatter = new SimpleDateFormat("dd");
		String day = dayFormatter.format(date);
		eventDetailsPropertyMap.put("date", day);
		Format yearFormatter = new SimpleDateFormat("yyyy");
		String year=yearFormatter.format(date);
		eventDetailsPropertyMap.put("year", year);
		eventDetailsPropertyMap.put("timeSlot", getTime(event.getSlot()));
		String imageIdentifier = UUID.randomUUID().toString();
		eventDetailsPropertyMap.put(IMAGE1, imageIdentifier);
		eventDetailsPropertyMap.put(IMAGE2, imageIdentifier + "1");
		eventDetailsPropertyMap.put(IMAGE3, imageIdentifier + "2");
		eventDetailsPropertyMap.put(DASHBOARD,dashBoardUrl);
		sendEventStartOrEndMail(event, eventDetailsPropertyMap, "QBthonEventDay", "QBthon - Event Day");
	}

		
	@Scheduled(cron="${timer.startEventMail}")
	public void scheduleEventStartMail() {
		processStartOrEndEventMail(startMorning, startNoon, "start");
	}
	
	@Override
	public void sendFeedBackMail(Event event) {
		Map<String, String> eventDetailsPropertyMap = new HashMap<>();
		String imageIdentifier = UUID.randomUUID().toString();
		eventDetailsPropertyMap.put(IMAGE1, imageIdentifier);
		eventDetailsPropertyMap.put(IMAGE2, imageIdentifier + "1");
		eventDetailsPropertyMap.put(IMAGE3, imageIdentifier + "2");
		eventDetailsPropertyMap.put("image4", imageIdentifier + "3");
		sendEventStartOrEndMail(event, eventDetailsPropertyMap, "feedback", "Thanks for your participation on QBthon day!");
	}
	
	//@Scheduled(cron="${timer.feedBackMail}")
	public void schedulefeedBackMail() {
		processStartOrEndEventMail(endMorning, endNoon, "end");
	}
	
	private void sendEventStartOrEndMail(Event event, Map<String, String> eventDetailsPropertyMap, String fileName, String subject) {
		Optional<List<UserEvent>> optionalUserEvent = userEventRepository.findByEventIdAndRoleAndNominated(event.getId(), "USER",true);
		List<UserEvent> userEvents;
		List<String> users = new ArrayList<>();
		if (optionalUserEvent.isPresent() &&!optionalUserEvent.get().isEmpty()) {
			userEvents = optionalUserEvent.get();
			userEvents.stream().forEach(userEvent -> users.add(userEvent.getUserId()));
			String templateFileName = "templates/"+ fileName +".htm";
			InputStream templateFileInputStream;
			try {
				getHTMLMetaData();
				templateFileInputStream=new ClassPathResource(templateFileName).getInputStream();
				File templateFile =new File("/temp/"+ fileName +".htm");
				FileUtils.copyInputStreamToFile(templateFileInputStream, templateFile);
				try(FileReader reader = new FileReader(templateFile);){
				Template template = Mustache.compiler().compile(reader);
				String emailBody = template.execute(eventDetailsPropertyMap);
				emailSender.sendNotificationMail(emailBody, subject, eventDetailsPropertyMap.get(IMAGE1),
						getEmaiIdsforEmpIds(users));
				}
			} catch (IOException ioEx) {
				log.error("sendStartEventMail IOException Error:::",Arrays.toString(ioEx.getStackTrace()));
				throw new EmailServiceException(
					"There Is A Problem In Feting The File At Location 'templates/"+ fileName +".htm'");

			} catch (MustacheException mEx) {
				log.error("sendStartEventMail MustacheException Error:::",Arrays.toString(mEx.getStackTrace()));
				throw new EmailServiceException(
					"An Exception Thrown When An Error Occurs Parsing Or Executing A Mustache Template 'templates/"+ fileName +".htm'");
			} catch (EmailServiceException ex) {
				log.error("sendStartEventMail EmailServiceException Error:::",Arrays.toString(ex.getStackTrace()));
				throw ex;
			}catch (Exception ex) {
				log.error("sendStartEventMail Error:::",Arrays.toString(ex.getStackTrace()));
				throw new EmailServiceException(SOMETHING_WENT_WRONG,ex);
			}

		}else {
			throw new EmailServiceException("There Are No Nominated User For The Event ");
		}
	}

	private void processStartOrEndEventMail(String morningHour,String noonHour,String type) {
		Date date= new Date();
		log.info(date.toString());
		Format hourFormatter = new SimpleDateFormat("HH");
		String hour=hourFormatter.format(date);
		Event event=null;
		List<Event> eventActiveList= new ArrayList<>();
		List<Event> eventList = (List<Event>) eventRepository.findAll();
		eventList.stream().forEach(activeEvent -> {
			 if (activeEvent.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.isEqual(LocalDate.of(2020, 07, 22))) {
				eventActiveList.add(activeEvent);
			} 
		});
		log.info(eventActiveList.toString());
		if(hour.equals(morningHour)&&!eventActiveList.isEmpty()) {
			eventActiveList.removeIf(activeEvent->activeEvent.getSlot().equals("noon"));
			event=eventActiveList.get(0);
		}else if(hour.equals(noonHour)&&!eventActiveList.isEmpty()) {
			eventActiveList.removeIf(activeEvent->activeEvent.getSlot().equals("morning"));
			event=eventActiveList.get(0);
		}
		if(!ObjectUtils.isEmpty(event)&&type.equals("start")) {
			sendStartEventMail(event);
		}else if(!ObjectUtils.isEmpty(event)&&type.equals("end")){
			sendFeedBackMail(event);
		}
		else {
			log.info("No Active Events For The Slot");
		}
	}
	
	private List<String> getEmaiIdsforEmpIds(List<String>ids) {
		List<String> mails=new ArrayList<>();
		List<User> users= userRepository.findByIdIn(ids).orElse(new ArrayList<>());
		if(!users.isEmpty()) {
			users.forEach(user->mails.add(user.getEmail()));
		}
		return mails;
	}
	
	private String getTime(String slot) {
		if(slot.equals("morning"))
			return "10 AM - 2 PM";
		else if(slot.equals("noon"))
			return "2 PM - 6 PM";
		else 
			return " ";
	}
	
	private void getHTMLMetaData() throws IOException {
		InputStream templateFileInputStream;
		String colorschememapping="templates/images/colorschememapping.xml";
		templateFileInputStream=new ClassPathResource(colorschememapping).getInputStream();
		File colorschememappingFile = new File("/temp/images/colorschememapping.xml");
		FileUtils.copyInputStreamToFile(templateFileInputStream, colorschememappingFile);
		String filelist="templates/images/filelist.xml";
		templateFileInputStream=new ClassPathResource(filelist).getInputStream();
		File filelistFile = new File("/temp/images/filelist.xml");
		FileUtils.copyInputStreamToFile(templateFileInputStream, filelistFile);
		String themedata="templates/images/themedata.thmx";
		templateFileInputStream=new ClassPathResource(themedata).getInputStream();
		File themedataFile = new File("/temp/images/themedata.thmx");
		FileUtils.copyInputStreamToFile(templateFileInputStream, themedataFile);
	}
	
	private void formTable(EventUserDetails eventDetails, StringBuilder skillTable, String coloumn1Start, String coloumn2Start, String rowEnd) {
		List<String> skillsList = Arrays.asList(eventDetails.getSkills().split("\\s*,\\s*"));
		int i = 0;
		int j = 0;
		for (String skill : skillsList) {
			if (j % 2 == 0) {
				if (j == 0) {
					skillTable.append(
							"<tr style='mso-yfti-irow:" + String.valueOf(i) + ";mso-yfti-firstrow:yes;height:20.1pt'>");
					i++;
				} else {
					skillTable.append("<tr style='mso-yfti-irow:" + String.valueOf(i) + ";height:20.1pt'>");
					i++;
				}
				skillTable.append(coloumn1Start + skill
						+ COLOUMN1_END);
				j++;
			} else {
				skillTable.append(coloumn2Start+ skill +COLOUMN2_END);
				j++;
			}
		}
		if (skillsList.size() % 2 != 0) {
			skillTable.append(rowEnd);
		}
	}

}
