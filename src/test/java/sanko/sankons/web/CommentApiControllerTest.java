package sanko.sankons.web;

import java.util.*; //List, ArrayList

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.comment.Comment;
import sanko.sankons.service.*; //CommentService, SessionService
import sanko.sankons.web.dto.*; //CommentAddRequest, CommentListRequest, CommentListResponse, SessionUser

@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentService commentService;

	@MockBean
	private SessionService sessionService;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static final Long postId = 2L;
	private static final String image = "image";
	private static final String postContent = "post content";

	private static final Long commentId = 3L;
	private static final String commentContent = "comment content";

	private static final List<Comment> comments = new ArrayList<>();

	private static final int start = 0;
	private static final int length = 5;

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

		for (int i = 1; i <= 5; i++) {
			Comment comment = Comment.builder()
				.post(post)
				.commenter(user)
				.content(commentContent)
				.build();
			ReflectionTestUtils.setField(comment, "id", Long.valueOf(i));
			comments.add(comment);
		}

		objectMapper = new ObjectMapper();
	}

	@BeforeEach
	public void mockCommentService() throws Exception {
		when(commentService.add(any(CommentAddRequest.class), any(SessionUser.class)))
			.thenReturn(commentId);

		when(commentService.list(any(CommentListRequest.class), any(SessionUser.class)))
			.thenAnswer(invocation -> {
				CommentListRequest request = invocation.getArgument(0, CommentListRequest.class);
				return CommentListResponse.builder()
					.post(request.getPost())
					.comments(comments)
					.start(request.getStart())
					.length(length)
					.build();
			});

		when(sessionService.getUser())
			.thenReturn(new SessionUser(user));
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

	@Test
	public void testCommentList() throws Exception {
		mockMvc.perform(get("/api/v1/comment/list?post=" + postId + "&start=" + start + "&length=" + length))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.end", is(start + length)))
			.andExpect(jsonPath("$.comments", hasSize(lessThanOrEqualTo(length))))
			.andExpect(jsonPath("$.comments[0].id", is(1)))
			.andExpect(jsonPath("$.comments[0].content", is(commentContent)))
			.andExpect(jsonPath("$.comments[0].commenter.username", is(username)));
	}

}
