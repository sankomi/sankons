package sanko.sankons.service;

import java.util.*; //List, ArrayList, Optional, LinkedHashSet, Arrays
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository, PostVisibility
import sanko.sankons.domain.comment.*; //Comment, CommentRepository
import sanko.sankons.domain.like.*; //Like, LikeRepository
import sanko.sankons.domain.hashtag.*; //Hashtag, HashtagRepository
import sanko.sankons.service.SessionService;
import sanko.sankons.web.dto.*; //PostPostRequest, PostListRequest, PostViewResponse, PostListResponse, PostLikeRequest, PostLikeResponse, PostEditRequest, SessionUser

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
	private CommentRepository commentRepository;

	@MockBean
	private LikeRepository likeRepository;

	@MockBean
	private HashtagRepository hashtagRepository;

	@MockBean
	private SessionService sessionService;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static final Long postId = 2L;
	private static final String image = "image";
	private static final String content = "content";
	private static final int views = 10;

	private static final String commentContent = "comment content";

	private static final int likes = 5;

	private static final int start = 0;
	private static final int length = 5;
	private static final int commentLength = 5;

	private static final List<Post> posts = new ArrayList<>();

	private static final Long catPostId = 4L;
	private static final String catContent = "cat content";
	private static final String catTag = "cat";

	private static User user;
	private static Post post;
	private static Post catPost;
	private static Hashtag catHashtag;
	private static Like like;
	private static SessionUser sessionUser;

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
			.visibility(PostVisibility.ALL)
			.build();
		ReflectionTestUtils.setField(post, "id", postId);

		like = Like.builder()
			.liker(user)
			.post(post)
			.build();
		ReflectionTestUtils.setField(post, "likes", Set.of(like));

		catPost = Post.builder()
			.poster(user)
			.image(image)
			.content(catContent)
			.visibility(PostVisibility.ALL)
			.build();
		ReflectionTestUtils.setField(catPost, "id", catPostId);

		Like catLike = Like.builder()
			.liker(user)
			.post(catPost)
			.build();
		ReflectionTestUtils.setField(catPost, "likes", Set.of(catLike));

		catHashtag = Hashtag.builder()
			.post(catPost)
			.tag("#" + catTag)
			.build();

		for (int i = 0; i < 5; i++) {
			Set<Comment> comments = new LinkedHashSet();
			for (int j = 0; j < 5; j++) {
				Comment comment = Comment.builder()
					.commenter(user)
					.content(commentContent)
					.build();
				ReflectionTestUtils.setField(comment, "id", Long.valueOf(i * 10 + j));

				comments.add(comment);
			}

			Post post = Post.builder()
				.poster(user)
				.image(image)
				.content(content)
				.visibility(PostVisibility.ALL)
				.comments(comments)
				.build();
			ReflectionTestUtils.setField(post, "id", Long.valueOf(i));
			ReflectionTestUtils.setField(post, "views", views);
			ReflectionTestUtils.setField(post, "likes", Set.of(like));

			posts.add(post);
		}

		sessionUser = new SessionUser(user);
	}

	@BeforeEach
	public void mockRepositorys() {
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(user));

		when(postRepository.save(any(Post.class)))
			.thenAnswer(invocation -> {
				ReflectionTestUtils.setField(post, "id", postId);
				return post;
			});

		when(postRepository.findById(postId))
			.thenReturn(Optional.of(post));

		when(postRepository.findAllByOrderByCreatedDesc())
			.thenReturn(posts);

		when(likeRepository.findAllByLikerAndPostIdIn(any(User.class), any(List.class)))
			.thenReturn(Arrays.asList(like));

		when(likeRepository.findByLikerAndPost(user, post))
			.thenReturn(like);

		when(hashtagRepository.saveAll(any(List.class)))
			.thenReturn(null);

		when(sessionService.getUser())
			.thenReturn(sessionUser);
	}

	@Test
	public void testPostPost() throws Exception {
		//given
		PostPostRequest request = PostPostRequest.builder()
			.content(content)
			.visibility(PostVisibility.ALL)
			.build();
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn(image);

		//when
		Long post = postService.post(request, file, sessionUser);

		//then
		assertEquals(postId, post);
	}

	@Test
	public void testPostView() throws Exception {
		//given
		int views = post.getViews();
		ReflectionTestUtils.setField(post, "content", content);

		//when
		PostViewResponse view = postService.view(postId, sessionUser);

		//then
		assertEquals(postId, view.getId());
		assertEquals(content, view.getContent());
		assertEquals(null, view.getComments());
		assertEquals(views + 1, view.getViews());
		assertEquals(userId, view.getPoster().getId());
		assertEquals(username, view.getPoster().getUsername());
	}

	@Test
	public void testPostViewSelf() throws Exception {
		//given
		Long selfPostId = 11L;
		Post selfPost = Post.builder()
			.poster(user)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(selfPost, "id", selfPostId);

		when(postRepository.findById(selfPostId))
			.thenReturn(Optional.of(selfPost));

		//when
		PostViewResponse response = postService.view(selfPostId, sessionUser);

		//then
		assertEquals(selfPostId, response.getId());
	}

	@Test
	public void testPostViewSelfNoLogin() throws Exception {
		//given
		Long selfPostId = 11L;
		Post selfPost = Post.builder()
			.poster(user)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(selfPost, "id", selfPostId);

		when(postRepository.findById(selfPostId))
			.thenReturn(Optional.of(selfPost));

		//when
		PostViewResponse response = postService.view(selfPostId, sessionUser);

		//then
		Exception exception = assertThrows(Exception.class, () -> postService.view(selfPostId, null));
		assertTrue(exception.getMessage().contains("Not permitted"));
	}

	@Test
	public void testPostViewSelfNotPoster() throws Exception {
		//given
		User otherUser = User.builder()
			.username("other user")
			.password("other password")
			.build();
		ReflectionTestUtils.setField(otherUser, "id", 12L);

		Long otherPostId = 8L;
		Post otherPost = Post.builder()
			.poster(otherUser)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(otherPost, "id", otherPostId);

		when(postRepository.findById(otherPostId))
			.thenReturn(Optional.of(otherPost));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> postService.view(otherPostId, sessionUser));
		assertTrue(exception.getMessage().contains("Not permitted"));
	}

	@Test
	public void testPostList() throws Exception {
		//given
		PostListRequest request = new PostListRequest(start, length, commentLength, null);

		//when
		PostListResponse response = postService.list(request, sessionUser);

		//then
		assertEquals(content, response.getPosts().get(0).getContent());
		assertEquals(views, response.getPosts().get(0).getViews());
		assertEquals(username, response.getPosts().get(0).getPoster().getUsername());
		assertEquals(username, response.getPosts().get(0).getComments().get(0).getCommenter().getUsername());
		assertTrue(commentLength >= response.getPosts().get(0).getComments().size());
		assertEquals(commentContent, response.getPosts().get(0).getComments().get(0).getContent());
	}

	@Test
	public void testPostListWithTag() throws Exception {
		//given
		PostListRequest request = new PostListRequest(start, length, commentLength, catTag);

		when(postRepository.findAllByHashtagsTagOrderByCreatedDesc("#" + catTag))
			.thenReturn(Arrays.asList(catPost));

		//when
		PostListResponse response = postService.list(request, sessionUser);

		//then
		assertEquals(catPostId, response.getPosts().get(0).getId());
		assertEquals(catContent, response.getPosts().get(0).getContent());
	}

	@Test
	public void testPostListSelfPoster() throws Exception {
		//given
		Long selfPostId = 4L;
		Post selfPost = Post.builder()
			.poster(user)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(selfPost, "id", selfPostId);

		Long anotherSelfPostId = 5L;
		Post anotherSelfPost = Post.builder()
			.poster(user)
			.image(image)
			.content(content)
			.visibility(PostVisibility.SELF)
			.build();
		ReflectionTestUtils.setField(anotherSelfPost, "id", anotherSelfPostId);

		when(postRepository.findAllByOrderByCreatedDesc())
			.thenReturn(List.of(selfPost, anotherSelfPost));

		PostListRequest request = new PostListRequest(start, length, commentLength, null);

		//when
		PostListResponse response = postService.list(request, sessionUser);

		//then
		assertEquals(2, response.getPosts().size());
	}

	@Test
	public void testPostListSelfNoLogin() throws Exception {
		//given
		Long selfPostId = 6L;
		Post selfPost = Post.builder()
			.poster(user)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();

		Long anotherSelfPostId = 7L;
		Post anotherSelfPost = Post.builder()
			.poster(user)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(anotherSelfPost, "id", anotherSelfPostId);

		when(postRepository.findAllByOrderByCreatedDesc())
			.thenReturn(List.of(selfPost, anotherSelfPost));

		PostListRequest request = new PostListRequest(start, length, commentLength, null);

		//when
		PostListResponse response = postService.list(request, null);

		//then
		assertEquals(0, response.getPosts().size());
	}

	@Test
	public void testPostListSelfNotPoster() throws Exception {
		//given
		User otherUser = User.builder()
			.username("other user")
			.password("other password")
			.build();
		ReflectionTestUtils.setField(otherUser, "id", 12L);

		Long otherPostId = 8L;
		Post otherPost = Post.builder()
			.poster(otherUser)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(otherPost, "id", otherPostId);

		Long anotherPostId = 9L;
		Post anotherPost = Post.builder()
			.poster(otherUser)
			.image(image)
			.visibility(PostVisibility.SELF)
			.content(content)
			.build();
		ReflectionTestUtils.setField(anotherPost, "id", anotherPostId);

		when(postRepository.findAllByOrderByCreatedDesc())
			.thenReturn(List.of(otherPost, anotherPost));

		PostListRequest request = new PostListRequest(start, length, commentLength, null);

		//when
		PostListResponse response = postService.list(request, sessionUser);

		//then
		assertEquals(0, response.getPosts().size());
	}

	@Test
	public void testPostCheckLike() throws Exception {
		//given
		PostCheckLikeRequest request = PostCheckLikeRequest.builder()
			.posts(Arrays.asList(postId))
			.build();

		//when
		PostCheckLikeResponse response = postService.checkLike(request, sessionUser);

		//then
		assertEquals(true, response.getLikes().get(0).isLiked());
		assertEquals(1, response.getLikes().get(0).getLikes());
	}

	@Test
	public void testPostLike() throws Exception {
		//given
		PostLikeRequest request = new PostLikeRequest(postId);

		//when
		PostLikeResponse response = postService.like(request, sessionUser);

		//then
		assertEquals(postId, response.getPost());
		assertEquals(false, response.isLiked());
		assertEquals(1, response.getLikes());
	}

	@Test
	public void testPostEdit() throws Exception {
		//given
		String newContent = "new content";

		PostEditRequest request = PostEditRequest.builder()
			.post(postId)
			.content(newContent)
			.build();

		//when
		boolean edited = postService.edit(request, sessionUser);

		//then
		assertTrue(edited);
	}

	@Test
	public void testPostEditNoPost() throws Exception {
		//given
		Long noPostId = 17L;
		String newContent = "new content";

		PostEditRequest request = PostEditRequest.builder()
			.post(noPostId)
			.content(newContent)
			.build();

		when(postRepository.findById(noPostId))
			.thenReturn(Optional.ofNullable(null));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> postService.edit(request, sessionUser));
		assertTrue(exception.getMessage().contains("Post not found"));
	}

}
