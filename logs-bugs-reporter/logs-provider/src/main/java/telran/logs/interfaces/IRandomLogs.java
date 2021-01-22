package telran.logs.interfaces;

import telran.logs.bugs.dto.LogDto;

public interface IRandomLogs {
    public LogDto createRandomLog();

    public void generateLogs(int count);
}
