package sanko.sankons.domain.user;

import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.*; //Test, AfterAll

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void after() {
		userRepository.deleteAll();
	}

	@Test
	public void test() {
		//given
		String username = "username";
		String password = "password";

		userRepository.save(User.builder()
			.username(username)
			.password(password)
			.build());

		//when
		List<User> users = userRepository.findAll();

		//then
		User user = users.get(0);
		assertEquals(user.getUsername(), username);
		assertEquals(user.getPassword(), password);
	}

}
