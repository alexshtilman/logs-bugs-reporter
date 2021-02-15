package telran.logs.providers;

import java.util.List;

import telran.logs.bugs.dto.LogDto;

public interface RandomLogs {
	public LogDto createRandomLog();

	public List<LogDto> generateLogs(int count);

	public void getStatisticsAggregate();
}
