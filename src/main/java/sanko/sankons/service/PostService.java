package sanko.sankons.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.post.PostRepository;
import sanko.sankons.web.dto.PostPostRequest;
import sanko.sankons.web.dto.PostListRequest;
import sanko.sankons.web.dto.PostViewResponse;
import sanko.sankons.web.dto.SessionUser;

@RequiredArgsConstructor
@Service
public class PostService {

	private final HttpSession httpSession;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public Post post(PostPostRequest request, MultipartFile file) throws Exception {
		SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		String filename = file.getOriginalFilename();
		File path = new File("files");
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
			.build());
	}

	public PostViewResponse view(Long id) throws Exception {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new Exception("Could not find post"));

		return new PostViewResponse(post);
	}

	public List<PostViewResponse> list(PostListRequest request) {
		return postRepository.findAll()
			.stream()
			.map(PostViewResponse::new)
			.collect(Collectors.toList());
	}

}
