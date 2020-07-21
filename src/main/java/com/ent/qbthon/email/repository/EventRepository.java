package com.ent.qbthon.email.repository;

import java.util.Date;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ent.qbthon.email.entity.Event;



@EnableScan
public interface EventRepository extends CrudRepository<Event, String> {
	Optional<Event> findById(String id);
	Optional<Event> findByDateAndSlot(Date date, String slot);
}
