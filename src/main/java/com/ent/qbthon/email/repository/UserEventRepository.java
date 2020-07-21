package com.ent.qbthon.email.repository;

import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ent.qbthon.email.entity.UserEvent;



@EnableScan
public interface UserEventRepository extends CrudRepository<UserEvent, String> {
	
	Optional<List<UserEvent>> findByEventIdAndRoleAndNominated(String id,String Role,Boolean nominated);
	Optional<List<UserEvent>> findByEventIdAndReminderFlagAndNominatedAndRole(String eventId, Boolean reminderFlag, Boolean nominated, String Role);

}
