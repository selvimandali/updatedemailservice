package com.ent.qbthon.email.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class EventUserDetails {
	@NotNull(message="date must not be null")
	Date date;
	@NotNull(message="slot must not be null")
	String slot;
	@NotNull(message="skills must not be null")
	String skills;
	@NotNull(message="users must not be null")
	List<String> usersList;
}
