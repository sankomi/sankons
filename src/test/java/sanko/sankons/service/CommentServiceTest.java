package sanko.sankons.service;

import java.util.*; //List, ArrayList, Optional, LinkedHashSet

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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository
import sanko.sankons.domain.comment.*; //Comment, CommentRepository
import sanko.sankons.web.dto.*; //CommentAddRequest, CommentListRequest, CommentListResponse, SessionUser, CommentDeleteRequest

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

	private static final Long userId = 1L;
	private static final Long notCommenterId = 4L;
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
	private static User notCommenter;
	private static Post post;
	private static Comment comment;
	private static SessionUser sessionUser;
	private static SessionUser sessionNotCommenter;

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

		sessionUser = new SessionUser(user);

		notCommenter = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(notCommenter, "id", notCommenterId);

		sessionNotCommenter = new SessionUser(notCommenter);
	}

	@BeforeEach
	public void mockRepositorys() {
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(user));

		when(userRepository.findById(notCommenterId))
			.thenReturn(Optional.of(notCommenter));

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
		Long response = commentService.add(request, sessionUser);

		//then
		assertEquals(commentId, response);
	}

	@Test
	public void testCommentDelete() throws Exception {
		//given
		CommentDeleteRequest request = new CommentDeleteRequest(commentId);
		when(commentRepository.findById(commentId))
			.thenReturn(Optional.of(comment));

		//whenthen
		assertDoesNotThrow(() -> commentService.delete(request, sessionUser));
	}

	@Test
	public void testCommentDeleteNoLogin() throws Exception {
		//given
		CommentDeleteRequest request = new CommentDeleteRequest(commentId);
		when(commentRepository.findById(commentId))
			.thenReturn(Optional.of(comment));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> commentService.delete(request, null));
		assertTrue(exception.getMessage().contains("Not logged in"));
	}

	@Test
	public void testCommentDeleteNotCommenter() throws Exception {
		//given
		CommentDeleteRequest request = new CommentDeleteRequest(commentId);
		when(commentRepository.findById(commentId))
			.thenReturn(Optional.of(comment));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> commentService.delete(request, sessionNotCommenter));
		assertTrue(exception.getMessage().contains("Not commenter"));
	}

	@Test
	public void testCommentDeleteNoComment() throws Exception {
		//given
		CommentDeleteRequest request = new CommentDeleteRequest(commentId);
		when(commentRepository.findById(commentId))
			.thenReturn(Optional.ofNullable(null));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> commentService.delete(request, sessionUser));
		assertTrue(exception.getMessage().contains("Comment not found"));
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
		CommentListResponse response = commentService.list(request, sessionUser);

		//then
		assertTrue(length >= response.getComments().size());
		assertEquals(postId, response.getPost());
		assertEquals(commentContent, response.getComments().get(0).getContent());
		assertEquals(userId, response.getComments().get(0).getCommenter().getId());
		assertEquals(username, response.getComments().get(0).getCommenter().getUsername());
	}

}
