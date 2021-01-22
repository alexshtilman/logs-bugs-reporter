package telran.logs.providers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import telran.logs.bugs.dto.LogDto;
import telran.logs.interfaces.LogTypeAndCountDto;

public class RandomLogsRepoImpl implements RandomLogsRepoCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public RandomLogsRepoImpl(MongoTemplate mongoTemplate) {
	this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<LogTypeAndCountDto> getStatistics() {
	List<AggregationOperation> list = new ArrayList<AggregationOperation>();
	list.add(Aggregation.group("$logType").count().as("count"));
	list.add(Aggregation.sort(Sort.Direction.ASC, "count"));
	list.add(Aggregation.project("count").and("_id").as("logType"));
	TypedAggregation<LogDto> agg = Aggregation.newAggregation(LogDto.class, list);
	return mongoTemplate.aggregate(agg, LogDto.class, LogTypeAndCountDto.class).getMappedResults();
    }

}
