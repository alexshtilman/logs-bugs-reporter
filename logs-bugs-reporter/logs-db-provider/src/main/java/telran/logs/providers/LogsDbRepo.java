package telran.logs.providers;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsDbRepo extends MongoRepository<LogDoc, ObjectId> {

}
