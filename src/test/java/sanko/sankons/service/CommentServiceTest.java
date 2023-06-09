package sanko.sankons.service;

import java.util.*; //List, ArrayList, Optional, LinkedHashSet
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.post.PostRepository;
import sanko.sankons.domain.comment.Comment;
import sanko.sankons.domain.comment.CommentRepository;
import sanko.sankons.web.dto.SessionUser;
import sanko.sankons.web.dto.*; //CommentAddRequest, CommentListRequest, CommentListResponse

@ExtendWith(SpringExtension.class)
@Import(CommentService.class)
public class CommentServiceTest {

	@Autowired
	private CommentService commentService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PostRepository postRepository;

	@MockBean
	private CommentRepository commentRepository;

	@MockBean
	private HttpSession httpSession;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static final Long postId = 2L;
	private static final String image = "image";
	private static final String content = "content";

	private static final Long commentId = 6L;
	private static final String commentContent = "comment content";

	private static final List<Comment> comments = new ArrayList<>();
	private static final int start = 2;
	private static final int length = 5;

	private static User user;
	private static Post post;
	private static Comment comment;

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

		comment = Comment.builder()
			.post(post)
			.commenter(user)
			.content(commentContent)
			.build();
		ReflectionTestUtils.setField(comment, "id", commentId);

		for (int i = 0; i < 10; i++) {
			Comment comment = Comment.builder()
				.post(post)
				.commenter(user)
				.content(commentContent)
				.build();
			ReflectionTestUtils.setField(comment, "id", Long.valueOf(i));

			comments.add(comment);
		}
	}

	@BeforeEach
	public void mockRepositorys() {
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(user));

		when(httpSession.getAttribute("user"))
			.thenReturn(new SessionUser(user));

		when(postRepository.findById(postId))
			.thenReturn(Optional.of(post));

		when(commentRepository.save(any(Comment.class)))
			.thenReturn(comment);

		when(commentRepository.findAllByPostIdOrderByIdDesc(anyLong()))
			.thenReturn(comments);
	}

	@Test
	public void testCommentAdd() throws Exception {
		//given
		CommentAddRequest request = CommentAddRequest.builder()
			.post(postId)
			.content(commentContent)
			.build();

		//when
		Long response = commentService.add(request);

		//then
		assertEquals(commentId, response);
	}

	@Test
	public void testCommentList() throws Exception {
		//given
		CommentListRequest request = CommentListRequest.builder()
			.post(postId)
			.start(start)
			.length(length)
			.build();

		//when
		CommentListResponse response = commentService.list(request);

		//then
		assertTrue(length >= response.getComments().size());
		assertEquals(postId, response.getPost());
		assertEquals(commentContent, response.getComments().get(0).getContent());
		assertEquals(userId, response.getComments().get(0).getCommenter().getId());
		assertEquals(username, response.getComments().get(0).getCommenter().getUsername());
	}

}
