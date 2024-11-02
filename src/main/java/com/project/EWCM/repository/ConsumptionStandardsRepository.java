package com.project.EWCM.repository;

import com.project.EWCM.Document.ConsumptionStandards;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConsumptionStandardsRepository extends MongoRepository<ConsumptionStandards, ObjectId> {
    Optional<ConsumptionStandards> findByUnitLevel(int level);
}
