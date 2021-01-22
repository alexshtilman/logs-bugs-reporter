package telran.logs.providers;

import java.util.List;

import telran.logs.interfaces.LogTypeAndCountDto;

public interface RandomLogsRepoCustom {
    List<LogTypeAndCountDto> getStatistics();
}
