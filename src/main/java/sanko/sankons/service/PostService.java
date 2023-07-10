package sanko.sankons.service;

import java.io.File;
import java.util.*; //List, Set, Date
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.regex.*; //Pattern, MatchResult
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository
import sanko.sankons.domain.comment.CommentRepository;
import sanko.sankons.domain.like.*; //Like, LikeRepository
import sanko.sankons.domain.hashtag.*; //Hashtag, HashtagRepository
import sanko.sankons.web.dto.*; //PostPostRequest, PostDeleteRequest, PostViewResponse, PostListRequest, PostListResponse, SessionUser, PostCheckLikeRequest, PostCheckLikeResponse, PostLikeRequest, PostLikeResponse, PostEditRequest

@RequiredArgsConstructor
@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final HashtagRepository hashtagRepository;

	public Long post(PostPostRequest request, MultipartFile file, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		String filename = file.getOriginalFilename();
		File path = new File(Paths.get("files", user.getId().toString()).toString());
		if (!path.exists() && !path.mkdirs()) {
			throw new Exception("Could not find upload path");
		}

		String newname = new SimpleDateFormat("yyMMdd-HHmmss").format(new Date())
			+ "-" + RandomStringUtils.randomAlphanumeric(8)
			+ "-" + filename;
		File upload = new File(path.getAbsolutePath(), newname);
		try {
			file.transferTo(upload);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not upload file");
		}

		Post post = postRepository.save(Post.builder()
			.poster(user)
			.image(filename)
			.content(request.getContent())
			.visibility(request.getVisibility())
			.comments(null)
			.build());

		List<Hashtag> hashtags = getHashtags(request.getContent()).stream()
			.map(tag -> Hashtag.builder()
				.post(post)
				.tag(tag)
				.build())
			.collect(Collectors.toList());

		hashtagRepository.saveAll(hashtags);

		return post.getId();
	}

	public boolean edit(PostEditRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		Post post = postRepository.findById(request.getPost())
			.orElseThrow(() -> new Exception("Post not found"));

		if (post.getPoster().equals(user)) {
			post.editContent(request.getContent());

			hashtagRepository.deleteAll(post.getHashtags());

			List<Hashtag> hashtags = getHashtags(request.getContent()).stream()
				.map(tag -> Hashtag.builder()
					.post(post)
					.tag(tag)
					.build())
				.collect(Collectors.toList());

			hashtagRepository.saveAll(hashtags);
		} else {
			throw new Exception("Not poster");
		}

		return true;
	}

	private Set<String> getHashtags(String content) {
		return Pattern.compile("\\#[a-zA-z0-9]+[a-zA-Z0-9\\-\\_]*[a-zA-z0-9]+")
			.matcher(content)
			.results()
			.map(MatchResult::group)
			.collect(Collectors.toSet());
	}

	public Boolean delete(PostDeleteRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		Post post = postRepository.findById(request.getPost())
			.orElseThrow(() -> new Exception("Post not found"));

		if (post.getPoster().equals(user)) {
			File file = new File(Paths.get("files", user.getId().toString(), post.getImage()).toString());
			likeRepository.deleteAll(post.getLikes());
			commentRepository.deleteAll(post.getComments());
			hashtagRepository.deleteAll(post.getHashtags());
			postRepository.delete(post);
			file.delete();
		} else {
			throw new Exception("Not poster");
		}

		return true;
	}

	public PostViewResponse view(Long id, SessionUser sessionUser) throws Exception {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new Exception("Could not find post"));

		PostVisibility visibility = post.getVisibility();
		Exception exception = new Exception("Not permitted");
		if (visibility == PostVisibility.SELF) {
			if (sessionUser == null) throw exception;
			if (!sessionUser.getId().equals(post.getPoster().getId())) throw exception;
		} else if (post.getVisibility() != PostVisibility.ALL) {
			throw exception;
		}

		post.view();
		postRepository.save(post);

		PostViewResponse response = new PostViewResponse(post);
		if (sessionUser != null) response.setLogin(sessionUser.getId());

		return response;
	}

	public PostListResponse list(PostListRequest request, SessionUser sessionUser) {
		int start = request.getStart();
		int length = request.getLength();
		int commentLength = request.getCommentLength();

		Long userId = sessionUser == null? null: sessionUser.getId();

		List<Post> posts;
		String tag = request.getTag();
		if (tag == null) {
			posts = postRepository.findAllByOrderByCreatedDesc();
		} else {
			posts = postRepository.findAllByHashtagsTagOrderByCreatedDesc("#" + tag);
		}

		posts = posts.stream()
			.filter(post -> {
				PostVisibility visibility = post.getVisibility();
				boolean visible = false;
				visible |= visibility == PostVisibility.ALL;
				visible |= visibility == PostVisibility.SELF && post.getPoster().getId().equals(userId);
				return visible;
			})
			.collect(Collectors.toList());

		return new PostListResponse(posts, userId, start, length, commentLength);
	}

	public File getImage(Long id) throws Exception {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new Exception("Could not find post"));

		Long userId = post.getPoster().getId();
		Path path = Paths.get("files", userId.toString(), post.getImage());

		return path.toFile();
	}

	public PostCheckLikeResponse checkLike(PostCheckLikeRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		List<Like> likes = likeRepository.findAllByLikerAndPostIdIn(user, request.getPosts());

		List<PostLikeResponse> responses = likes.stream()
			.map(like -> {
				return PostLikeResponse.builder()
					.post(like.getPost().getId())
					.liked(true)
					.likes(like.getPost().getLikes().size())
					.build();
			}).collect(Collectors.toList());

		return new PostCheckLikeResponse(responses);
	}

	public PostLikeResponse like(PostLikeRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		Post post = postRepository.findById(request.getPost())
			.orElseThrow(() -> new Exception("Post not found"));

		Like like = likeRepository.findByLikerAndPost(user, post);

		if (like == null) {
			like = Like.builder()
				.liker(user)
				.post(post)
				.build();
			likeRepository.save(like);
			return PostLikeResponse.builder()
				.post(post.getId())
				.liked(true)
				.likes(post.getLikes().size())
				.build();
		} else {
			likeRepository.delete(like);
			return PostLikeResponse.builder()
				.post(post.getId())
				.liked(false)
				.likes(post.getLikes().size())
				.build();
		}
	}

}
