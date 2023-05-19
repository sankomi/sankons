package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;
import sanko.sankons.service.CommentService;
import sanko.sankons.web.dto.CommentAddRequest;

@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentService commentService;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";
	private static final Long postId = 2L;
	private static final String image = "image";
	private static final String postContent = "post content";
	private static final Long commentId = 3L;
	private static final String commentContent = "comment content";

	private static User user;
	private static Post post;
	private static ObjectMapper objectMapper;

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
			.content(postContent)
			.build();
		ReflectionTestUtils.setField(post, "id", postId);

		objectMapper = new ObjectMapper();
	}

	@BeforeEach
	public void mockCommentService() throws Exception {
		when(commentService.add(any(CommentAddRequest.class)))
			.thenReturn(commentId);
	}

	@Test
	public void testCommentAdd() throws Exception {
		CommentAddRequest request = CommentAddRequest.builder()
			.post(postId)
			.content(commentContent)
			.build();

		mockMvc.perform(
			post("/api/v1/comment/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(request))
		)
			.andExpect(status().isOk())
			.andExpect(content().string(commentId.toString()));
	}

}
