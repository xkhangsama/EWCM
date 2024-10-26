package com.project.EWCM.repository;

import com.project.EWCM.Document.Account;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends MongoRepository<Account, ObjectId> {

    Optional<Account> findByUsername(String username);

    List<Account> findByUnitIsNull();

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
