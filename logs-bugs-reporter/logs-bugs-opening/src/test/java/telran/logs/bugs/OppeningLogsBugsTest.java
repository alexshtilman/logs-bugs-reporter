package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import telran.logs.bugs.jpa.entities.Programmer;

@SpringBootTest
@AutoConfigureTestDatabase

public class OppeningLogsBugsTest {

	@Autowired
	ProgrammersRepo programmersRepo;

	@Test
	void primiteveTest() {
		programmersRepo.save(new Programmer(1, "Vasya"));
		assertEquals(1, programmersRepo.count());
	}
}
