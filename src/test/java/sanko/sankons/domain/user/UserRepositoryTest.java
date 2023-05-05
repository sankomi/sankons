package sanko.sankons.domain.user;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.junit.jupiter.api.*; //Test, AfterAll

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void after() {
		userRepository.deleteAll();
	}

	@Test
	public void testUser() {
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
		assertEquals( username, user.getUsername());
		assertTrue(user.checkPassword(password));
	}

	@Test
	public void testUserTime() {
		//given
		LocalDateTime time = LocalDateTime.now();

		//when
		userRepository.save(User.builder()
			.username("username")
			.password("password")
			.build());

		//then
		User user = userRepository.findAll().get(0);
		assertTrue(time.isBefore(user.getCreated()));
	}

}
