package com.project.EWCM.repository;

import com.project.EWCM.Document.File;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, ObjectId> {
}
