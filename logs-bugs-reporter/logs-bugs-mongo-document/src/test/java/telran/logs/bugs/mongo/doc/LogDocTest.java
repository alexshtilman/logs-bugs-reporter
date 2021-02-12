package telran.logs.bugs.mongo.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.test.StepVerifier;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LogsRepo.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
class LogDocTest {
	@Autowired
	LogsRepo logs;

	@BeforeEach
	void setup() {
		logs.deleteAll().log().subscribe();
	}

	@Test
	void docStoreTest() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		logs.save(new LogDoc(logDto)).subscribe();
		StepVerifier.create(logs.count()).expectNextCount(1).verifyComplete();

		StepVerifier.create(logs.findAll()).assertNext(doc -> {
			assertEquals(logDto.dateTime, doc.getLogDto().dateTime);
			assertEquals(logDto.logType, doc.getLogDto().logType);
			assertEquals(logDto.artifact, doc.getLogDto().artifact);
			assertEquals(logDto.responseTime, doc.getLogDto().responseTime);
			assertEquals(logDto.result, doc.getLogDto().result);
		}).expectNextCount(0).verifyComplete();
	}
}
