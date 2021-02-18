package telran.logs.bugs.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogRepository extends ReactiveMongoRepository<LogDoc, ObjectId>, LogsStatistics {

	Flux<LogDoc> findByLogType(LogType logType);

	Flux<LogDoc> findByLogTypeNot(LogType noException);
}
