package com.ent.qbthon.email.repository;

import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ent.qbthon.email.entity.User;
@EnableScan
public interface UserRepository extends CrudRepository<User, String>{
	Optional<List<User>> findByIdIn(List<String> ids);
}
