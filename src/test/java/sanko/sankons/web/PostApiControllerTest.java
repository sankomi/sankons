package sanko.sankons.web;

import java.util.*; //List, ArrayList, LinkedHashSet

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.like.Like;
import sanko.sankons.domain.comment.Comment;
import sanko.sankons.service.PostService;
import sanko.sankons.web.dto.*; //PostPostRequest, PostViewResponse, PostListRequest, PostListResponse, PostLikeRequest, PostLikeResponse

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

	private static final String commentContent = "comment content";

	private static final List<Post> posts = new ArrayList<>();
	private static final int start = 0;
	private static final int length = 5;
	private static final int commentLength = 5;

	private static final int likes = 14;

	private static User user;
	private static Post post;
	private static Like like;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(user, "id", userId);

		like = Like.builder()
			.liker(user)
			.post(post)
			.build();

		post = Post.builder()
			.poster(user)
			.image(image)
			.content(content)
			.build();
		ReflectionTestUtils.setField(post, "id", postId);
		ReflectionTestUtils.setField(post, "likes", Set.of(like));

		for (int i = 0; i < length; i++) {
			Set<Comment> comments = new LinkedHashSet<>();
			for (int j = 0; j < 2; j++) {
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
				.comments(comments)
				.build();
			ReflectionTestUtils.setField(post, "id", Long.valueOf(i));
			ReflectionTestUtils.setField(post, "likes", Set.of(like));

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
				return new PostListResponse(posts, request.getStart(), request.getLength(), request.getCommentLength());
			});

		when(postService.view(postId)).thenReturn(new PostViewResponse(post));

		when(postService.checkLike(postId))
			.thenReturn(PostLikeResponse.builder()
				.liked(true)
				.likes(likes)
				.build());

		when(postService.like(any(PostLikeRequest.class)))
			.thenReturn(PostLikeResponse.builder()
				.liked(true)
				.likes(likes)
				.build());
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
		mockMvc.perform(get("/api/v1/post/list?start=" + start + "&length=" + length + "&commentLength=" + commentLength))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.end", is(start + length)))
			.andExpect(jsonPath("$.posts[0].id", is(0)))
			.andExpect(jsonPath("$.posts[0].content", is(content)))
			.andExpect(jsonPath("$.posts[0].poster.username", is(username)))
			.andExpect(jsonPath("$.posts[0].comments", hasSize(lessThanOrEqualTo(commentLength))))
			.andExpect(jsonPath("$.posts[0].comments[0].commenter.username", is(username)))
			.andExpect(jsonPath("$.posts[0].comments[0].content", is(commentContent)));
	}

	@Test
	public void testPostView() throws Exception {
		mockMvc.perform(get("/api/v1/post/" + postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(postId), Long.class))
			.andExpect(jsonPath("$.content", is(content)))
			.andExpect(jsonPath("$.comments", is(nullValue())))
			.andExpect(jsonPath("$.poster.username", is(username)));
	}

	@Test
	public void testCheckLike() throws Exception {
		mockMvc.perform(get("/api/v1/post/" + postId + "/like"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.liked", is(true), Boolean.class))
			.andExpect(jsonPath("$.likes", is(likes), Integer.class));
	}

	@Test
	public void testLike() throws Exception {
		PostLikeRequest request = new PostLikeRequest(postId);

		mockMvc.perform(
			put("/api/v1/post/like")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(new ObjectMapper().writeValueAsBytes(request))
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.liked", is(true), Boolean.class))
			.andExpect(jsonPath("$.likes", is(likes), Integer.class));
	}

}
