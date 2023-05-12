package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeEach, AfterEach
import org.springframework.beans.factory.annotation.*; //Autowired, Value
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*; //HttpStatus, ResponseEntity, HttpHeaders, HttpMethod, HttpEntity
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest
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
		String url = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest request = UserCreateRequest.builder()
			.username("username")
			.password("password")
			.build();

		//when
		ResponseEntity<Long> response = restTemplate.postForEntity(url, request, Long.class);

		//then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody() > 0L);
	}

	@Test
	public void testEmptyUser() {
		//given
		String url = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest noUsername = UserCreateRequest.builder()
			.username(null)
			.password("password")
			.build();
		UserCreateRequest noPassword = UserCreateRequest.builder()
			.username("nopassword")
			.password(null)
			.build();

		//when
		ResponseEntity<Object> usernameResponse = restTemplate.postForEntity(url, noUsername, Object.class);
		ResponseEntity<Object> passwordResponse = restTemplate.postForEntity(url, noPassword, Object.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST, usernameResponse.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, passwordResponse.getStatusCode());
	}

	@Test
	public void testInvalidUser() {
		//given
		String url = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest blankUsername = UserCreateRequest.builder()
			.username("  ")
			.password("password")
			.build();
		UserCreateRequest emptyPassword = UserCreateRequest.builder()
			.username("emptypassword")
			.password("")
			.build();

		//when
		ResponseEntity<Object> usernameResponse = restTemplate.postForEntity(url, blankUsername, Object.class);
		ResponseEntity<Object> passwordResponse = restTemplate.postForEntity(url, emptyPassword, Object.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST, usernameResponse.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, passwordResponse.getStatusCode());
	}

	@Test
	public void testUniqueUser() {
		//given
		String url = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest request = UserCreateRequest.builder()
			.username("unique")
			.password("password")
			.build();
		restTemplate.postForEntity(url, request, Long.class);

		//when
		ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);

		//then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	public void testLogin() {
		//given
		String username = "login";
		String password = "password";
		String createUrl = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest createRequest = UserCreateRequest.builder()
			.username(username)
			.password(password)
			.build();
		restTemplate.postForEntity(createUrl, createRequest, Long.class);

		//when
		String loginUrl = "http://localhost:" + port + "/api/v1/user/login";
		UserLoginRequest loginRequest = UserLoginRequest.builder()
			.username(username)
			.password(password)
			.build();
		ResponseEntity<Boolean> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, Boolean.class);
		String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

		//then
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Object> request = new HttpEntity<>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
			loginUrl,
			HttpMethod.GET,
			request,
			String.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(username, response.getBody());
	}

	@Test
	public void testInvalidLogin() {
		//given
		String loginUrl = "http://localhost:" + port + "/api/v1/user/login";
		UserLoginRequest blankUsername = UserLoginRequest.builder()
			.username(" ")
			.password("password")
			.build();
		UserLoginRequest emptyPassword = UserLoginRequest.builder()
			.username("username")
			.password("")
			.build();

		//when
		ResponseEntity<Object> usernameResponse = restTemplate.postForEntity(loginUrl, blankUsername, Object.class);
		ResponseEntity<Object> passwordResponse = restTemplate.postForEntity(loginUrl, emptyPassword, Object.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST, usernameResponse.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, passwordResponse.getStatusCode());
	}

	@Test
	public void testLogout() {
		//given
		String username = "logout";
		String password = "password";
		String createUrl = "http://localhost:" + port + "/api/v1/user/create";
		UserCreateRequest createRequest = UserCreateRequest.builder()
			.username(username)
			.password(password)
			.build();
		restTemplate.postForEntity(createUrl, createRequest, Long.class);

		String loginUrl = "http://localhost:" + port + "/api/v1/user/login";
		UserLoginRequest loginRequest = UserLoginRequest.builder()
			.username(username)
			.password(password)
			.build();
		ResponseEntity<Boolean> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, Boolean.class);
		String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
		
		//when
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Object> request = new HttpEntity<>(null, headers);
		
		ResponseEntity<Boolean> logoutResponse = restTemplate.exchange(
			loginUrl,
			HttpMethod.DELETE,
			request,
			Boolean.class
		);

		//then
		ResponseEntity<String> response = restTemplate.exchange(
			loginUrl,
			HttpMethod.GET,
			request,
			String.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(null, response.getBody());
	}

}
