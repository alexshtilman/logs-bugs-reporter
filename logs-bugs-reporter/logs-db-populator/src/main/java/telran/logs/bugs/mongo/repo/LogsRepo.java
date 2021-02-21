package telran.logs.bugs.mongo.repo;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsRepo extends ReactiveMongoRepository<LogDoc, ObjectId> {
	Mono<LogDoc> findFirstByDateTime(Date dateTime);

	@Aggregation(pipeline = { "{$group: {_id: $logType,count: { $sum: 1}}}", "{$sort: {count:-1}}",
			"{ $project: { _id: 0, logType: $_id, count: 1 } }" })
	Flux<LogTypeAndCountDto> getStatisticsAggregate();
}
