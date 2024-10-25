package com.project.EWCM.repository;

import com.project.EWCM.Document.Unit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnitRepository extends MongoRepository<Unit, ObjectId> {
}
