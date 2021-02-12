package telran.logs.bugs.mongo.repo;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;
import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsRepo extends ReactiveMongoRepository<LogDoc, ObjectId> {
	Mono<LogDoc> findFirstByDateTime(Date dateTime);
}
