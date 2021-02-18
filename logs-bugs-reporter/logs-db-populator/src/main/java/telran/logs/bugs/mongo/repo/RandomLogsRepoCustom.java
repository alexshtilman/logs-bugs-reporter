package telran.logs.bugs.mongo.repo;

import java.util.List;

import telran.logs.bugs.dto.LogTypeAndCountDto;

public interface RandomLogsRepoCustom {
	List<LogTypeAndCountDto> getStatistics();
}
