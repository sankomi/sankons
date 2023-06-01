package sanko.sankons.service;

import java.util.*; //List, ArrayList, Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*; //Page, Pageable

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.post.*; //PostRepository, PostPagingRepository
import sanko.sankons.web.dto.SessionUser;
import sanko.sankons.web.dto.*; //PostPostRequest, PostListRequest, PostViewResponse, PostListResponse

@ExtendWith(SpringExtension.class)
@Import(PostService.class)
public class PostServiceTest {

	@Autowired
	private PostService postService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PostRepository postRepository;

	@MockBean
	private PostPagingRepository postPagingRepository;

	@MockBean
	private HttpSession httpSession;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static final Long postId = 2L;
	private static final String image = "image";
	private static final String content = "content";

	private static final int page = 0;
	private static final int size = 2;

	private static final List<Post> posts = new ArrayList<>();

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

		for (int i = 0; i < 5; i++) {
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
	public void mockRepositorys() {
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(user));

		when(httpSession.getAttribute("user"))
			.thenReturn(new SessionUser(user));

		when(postRepository.save(any(Post.class)))
			.thenAnswer(invocation -> {
				ReflectionTestUtils.setField(post, "id", postId);
				return post;
			});

		when(postRepository.findById(postId))
			.thenReturn(Optional.of(post));

		Page<Post> postPage = mock(Page.class);
		when(postPage.getContent()).thenReturn(
			posts.stream()
				.limit(size)
				.collect(Collectors.toList())

		);
		when(postPagingRepository.findAll(any(Pageable.class)))
			.thenReturn(postPage);
	}

	@Test
	public void testPostPost() throws Exception {
		//given
		PostPostRequest request = PostPostRequest.builder()
			.content(content)
			.build();
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn(image);

		//when
		Long post = postService.post(request, file);

		//then
		assertEquals(postId, post);
	}

	@Test
	public void testPostView() throws Exception {
		//when
		PostViewResponse view = postService.view(postId);

		//then
		assertEquals(postId, view.getId());
		assertEquals(content, view.getContent());
		assertEquals(userId, view.getPoster().getId());
		assertEquals(username, view.getPoster().getUsername());
	}

	@Test
	public void testPostList() throws Exception {
		//given
		PostListRequest request = new PostListRequest(page, size);

		//when
		PostListResponse response = postService.list(request);

		//then
		assertEquals(page, response.getPage());
		assertEquals(size, response.getSize());
		assertEquals(size, response.getPosts().size());
		assertEquals(content, response.getPosts().get(0).getContent());
		assertEquals(username, response.getPosts().get(0).getPoster().getUsername());
	}

}
