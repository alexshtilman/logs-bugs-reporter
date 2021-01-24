package telran.logs.interfaces;

import java.util.List;

import telran.logs.bugs.dto.LogDto;

public interface IRandomLogs {
    public LogDto createRandomLog();

    public List<LogDto> generateLogs(int count);

    public void getStatisticsAggregate();
}
