package telran.logs.providers;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.logs.bugs.mongo.doc.LogDoc;

public interface RandomLogsRepo extends MongoRepository<LogDoc, ObjectId>, RandomLogsRepoCustom {

    @Aggregation(pipeline = { "{$group: {_id: $logType,count: { $sum: 1}}}", "{$sort: {count:-1}}",
	    "{ $project: { _id: 0, logType: $_id, count: 1 } }" })
    List<LogTypeAndCountDto> getStatisticsAggregate();

}
