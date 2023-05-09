package sanko.sankons.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.post.PostRepository;
import sanko.sankons.web.dto.PostPostRequest;
import sanko.sankons.web.dto.SessionUser;

@RequiredArgsConstructor
@Service
public class PostService {

	private final HttpSession httpSession;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public Post post(PostPostRequest request) {
		SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

		if (sessionUser == null) return null;

		User user = userRepository.findById(sessionUser.getId())
			.orElse(null);

		if (user == null) return null;

		return postRepository.save(Post.builder()
			.poster(user)
			.content(request.getContent())
			.build());
	}

}
