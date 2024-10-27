package com.project.EWCM.repository;

import com.project.EWCM.Document.Consumption;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsumptionRepository extends MongoRepository<Consumption, ObjectId> {
}
