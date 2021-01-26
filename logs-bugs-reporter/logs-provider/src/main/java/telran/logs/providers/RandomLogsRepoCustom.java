package telran.logs.providers;

import java.util.List;


public interface RandomLogsRepoCustom {
    List<LogTypeAndCountDto> getStatistics();
}
