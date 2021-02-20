package telran.logs.bugs.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDocClass;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.dto.LogTypeClass;
import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogRepository extends ReactiveMongoRepository<LogDoc, ObjectId>, LogsStatistics {

	Flux<LogDoc> findByLogType(LogType logType);

	Flux<LogDoc> findByLogTypeNot(LogType noException);

	@Aggregation(pipeline = { 
			"{ $group: { _id: $logType,count: { $sum: 1}}}",
			"{ $sort: { count:-1}}",
			"{ $project: { _id: 0, logType: $_id, count: 1 } }" 
			})
	Flux<LogTypeAndCountDto> getLogTypeOccurencesByAggregation();

	@Aggregation(pipeline = { 
			"{ $group: {_id: $artifact,count: { $sum: 1}}}", 
			"{ $sort: { count:-1}}",
			"{ $project: { _id: 0, artifact: $_id, count: 1 } }" })
	Flux<ArtifactAndCountDto> getArtifactOccuresncesByAggregation();

	@Aggregation(pipeline = {
			"{ $match : { logType : { $ne : NO_EXCEPTION}}}",
			"{ $group : { _id : $logType, count : { $sum : 1}}}", 
			"{ $sort : { count : -1}}",
			"{ $limit : ?0}",
			"{ $project : { _id : 0, logType : $_id}}" })
	Flux<LogTypeClass> getFirstMostEncounteredExceptionsByAggregation(int limit);

	@Aggregation(pipeline = { 
			"{ $group : { _id : $artifact, count : { $sum : 1}}}",
			"{ $sort : { count : -1}}",
			"{ $limit : ?0}",
			"{ $project : { _id : 0, artifact : $_id}}" })
	Flux<LogDocClass> getFirstMostEncounteredArtifactsByAggregation(int limit);

}
