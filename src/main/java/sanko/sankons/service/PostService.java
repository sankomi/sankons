package sanko.sankons.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*; //Pageable, PageRequest
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository, PostPagingRepository
import sanko.sankons.web.dto.*; //PostPostRequest, PostViewResponse, PostListRequest, PostListResponse, SessionUser

@RequiredArgsConstructor
@Service
public class PostService {

	private final HttpSession httpSession;
	private final PostRepository postRepository;
	private final PostPagingRepository postPagingRepository;
	private final UserRepository userRepository;

	public Long post(PostPostRequest request, MultipartFile file) throws Exception {
		SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

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

		File upload = new File(path.getAbsolutePath(), filename);
		try {
			file.transferTo(upload);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not upload file");
		}

		return postRepository.save(Post.builder()
			.poster(user)
			.image(filename)
			.content(request.getContent())
			.build()).getId();
	}

	public PostViewResponse view(Long id) throws Exception {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new Exception("Could not find post"));

		return new PostViewResponse(post);
	}

	public PostListResponse list(PostListRequest request) {
		// List<Post> posts = postRepository.findAll();

		int page = request.getPage();
		int size = request.getSize();
		Pageable pageable = PageRequest.of(page, size);
		List<Post> posts = postPagingRepository.findAll(pageable).getContent();

		return PostListResponse.builder()
			.page(page)
			.size(size)
			.posts(posts)
			.build();
	}

	public File getImage(Long id) throws Exception {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new Exception("Could not find post"));

		Long userId = post.getPoster().getId();
		Path path = Paths.get("files", userId.toString(), post.getImage());

		return path.toFile();
	}

}
