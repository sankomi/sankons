package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeEach, AfterEach
import org.springframework.beans.factory.annotation.*; //Autowired, Value
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*; //HttpStatus, ResponseEntity
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.web.dto.UserCreateRequestDto;
import sanko.sankons.domain.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	@AfterEach
	public void deleteAllUsers() {
		userRepository.deleteAll();
	}

	@Test
	public void testCreateUser() {
		//given
		String url = "http://localhost:" + port + "/api/v1/user/";
		UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
			.username("username")
			.password("password")
			.build();

		//when
		ResponseEntity<Long> response = restTemplate.postForEntity(url, requestDto, Long.class);

		//then
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertTrue(response.getBody() == 1L);
	}

}
