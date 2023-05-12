package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeAll
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;
import sanko.sankons.service.PostService;
import sanko.sankons.web.dto.PostPostRequest;
import sanko.sankons.web.dto.PostViewResponse;

@WebMvcTest(PostApiController.class)
public class PostApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService postService;

	private static final String username = "username";
	private static final String password = "password";
	private static final String image = "image.jpg";
	private static final String content = "content";

	private static User user;
	private static Post post;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();

		post = Post.builder()
			.poster(user)
			.image(image)
			.content(content)
			.build();
	}

	@Test
	public void testPostPost() throws Exception {
		when(postService.post(any(PostPostRequest.class), any(MultipartFile.class)))
			.thenReturn(post);

		MultipartFile multipartFile = mock(MultipartFile.class);
		when(multipartFile.getOriginalFilename()).thenReturn(image);
		MockMultipartFile file = new MockMultipartFile("file", multipartFile.getInputStream());

		PostPostRequest postPostRequest = PostPostRequest.builder()
			.content(content)
			.build();
		String json = new ObjectMapper().writeValueAsString(postPostRequest);
		MockMultipartFile request = new MockMultipartFile(
			"request",
			"request",
			MediaType.APPLICATION_JSON_VALUE,
			json.getBytes()
		);

		mockMvc.perform(
			multipart("/api/v1/post/post")
				.file(file)
				.file(request)
		)
			.andExpect(status().isOk());
	}

	@Test
	public void testPostView() throws Exception {
		Long postId = 1L;

		when(postService.view(postId)).thenReturn(new PostViewResponse(post));

		mockMvc.perform(get("/api/v1/post/" + postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.image", is(image)))
			.andExpect(jsonPath("$.content", is(content)))
			.andExpect(jsonPath("$.poster.username", is(username)));
	}

}
