package sanko.sankons.web;

import java.util.*; //List, ArrayList;

import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;
import sanko.sankons.service.PostService;
import sanko.sankons.web.dto.*; //PostPostRequest, PostViewResponse, PostListRequest, PostViewResponse

@WebMvcTest(PostApiController.class)
public class PostApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService postService;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static final Long postId = 1L;
	private static final String image = "image.jpg";
	private static final String content = "content";

	private static final List<Post> posts = new ArrayList();

	private static final int page = 0;
	private static final int size = 2;

	private static User user;
	private static Post post;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(user, "id", userId);

		post = Post.builder()
			.poster(user)
			.image(image)
			.content(content)
			.build();
		ReflectionTestUtils.setField(post, "id", postId);

		for (int i = 0; i < size; i++) {
			Post post = Post.builder()
				.poster(user)
				.image(image)
				.content(content)
				.build();
			ReflectionTestUtils.setField(post, "id", Long.valueOf(i));
			posts.add(post);
		}
	}

	@BeforeEach
	public void mockPostService() throws Exception {
		when(postService.post(any(PostPostRequest.class), any(MultipartFile.class)))
			.thenReturn(postId);

		when(postService.list(any(PostListRequest.class)))
			.thenAnswer(invocation -> {
				PostListRequest request = invocation.getArgument(0, PostListRequest.class);
				return PostListResponse.builder()
					.page(request.getPage())
					.size(request.getSize())
					.posts(posts)
					.build();
			});

		when(postService.view(postId)).thenReturn(new PostViewResponse(post));
	}

	@Test
	public void testPostPost() throws Exception {
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
			.andExpect(status().isOk())
			.andExpect(content().string(postId.toString()));
	}

	@Test
	public void testPostList() throws Exception {
		mockMvc.perform(get("/api/v1/post/list?page=0&size=2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.posts[0].id", is(0)))
			.andExpect(jsonPath("$.posts[0].content", is(content)))
			.andExpect(jsonPath("$.posts[0].poster.username", is(username)));
	}

	@Test
	public void testPostView() throws Exception {
		mockMvc.perform(get("/api/v1/post/" + postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(postId), Long.class))
			.andExpect(jsonPath("$.content", is(content)))
			.andExpect(jsonPath("$.poster.username", is(username)));
	}

}
