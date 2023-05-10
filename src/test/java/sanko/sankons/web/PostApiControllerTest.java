package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeEach, AfterEach
import org.springframework.beans.factory.annotation.*; //Autowired, Value
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*; //HttpStatus, ResponseEntity, HttpHeaders, HttpMethod, HttpEntity, MediaType
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest, PostPostRequest

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testPostPost() {
		/*
		//given
		String username = "postpost";
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
		String content = "content";

		String postUrl = "http://localhost:" + port + "/api/v1/post/post";
		PostPostRequest postRequest = PostPostRequest.builder()
			.content(content)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity request = new HttpEntity(postRequest, headers);

		ResponseEntity<Long> postResponse = restTemplate.exchange(
			postUrl,
			HttpMethod.POST,
			request,
			Long.class
		);

		//then
		assertEquals(HttpStatus.OK, postResponse.getStatusCode());
		assertTrue(postResponse.getBody() > 0L);
		*/
	}

}
