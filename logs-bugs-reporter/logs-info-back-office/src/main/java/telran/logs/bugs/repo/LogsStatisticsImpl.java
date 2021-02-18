package telran.logs.bugs.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDocClass;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.dto.LogTypeClass;
import telran.logs.bugs.mongo.doc.LogDoc;

public class LogsStatisticsImpl implements LogsStatistics {

	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	private static final String COUNT = "count";

	@Override
	public Flux<LogTypeAndCountDto> getLogTypeOccurences() {

		List<AggregationOperation> aggregations = new ArrayList<>();
		aggregations.add(Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT));
		aggregations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
		aggregations.add(Aggregation.project(COUNT).and("_id").as(LogTypeAndCountDto.LOG_TYPE));

		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregations);
		return mongoTemplate.aggregate(pipeline, LogTypeAndCountDto.class);
	}

	@Override
	public Flux<ArtifactAndCountDto> getArtifactOccuresnces() {
		List<AggregationOperation> aggregations = new ArrayList<>();
		aggregations.add(Aggregation.group(LogDoc.ARTIFACT).count().as(COUNT));
		aggregations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
		aggregations.add(Aggregation.project(COUNT).and("_id").as(LogDoc.ARTIFACT));

		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregations);
		return mongoTemplate.aggregate(pipeline, ArtifactAndCountDto.class);
	}

	@Override
	public Flux<LogTypeClass> getFirstMostEncounteredExceptions(int count) {
		List<AggregationOperation> aggregations = new ArrayList<>();
		aggregations.add(Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne("NO_EXCEPTION")));
		aggregations.add(Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT));
		aggregations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
		aggregations.add(Aggregation.limit(count));
		aggregations.add(Aggregation.project().andExclude("_id").and("_id").as(LogTypeAndCountDto.LOG_TYPE));

		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregations);
		return mongoTemplate.aggregate(pipeline, LogTypeClass.class);
	}

	@Override
	public Flux<LogDocClass> getFirstMostEncounteredArtifacts(int count) {
		List<AggregationOperation> aggregations = new ArrayList<>();
		aggregations.add(Aggregation.group(LogDoc.ARTIFACT).count().as(COUNT));
		aggregations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
		aggregations.add(Aggregation.limit(count));
		aggregations.add(Aggregation.project().andExclude("_id").and("_id").as(LogDoc.ARTIFACT));

		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregations);
		return mongoTemplate.aggregate(pipeline, LogDocClass.class);
	}

}
