package com.project.EWCM.repository;

import com.project.EWCM.Document.Consumption;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsumptionRepository extends MongoRepository<Consumption, ObjectId> {
    List<Consumption> findByUnitId(ObjectId unitId);

    List<Consumption> findByUnitIdIn(List<ObjectId> unitIds);
}
