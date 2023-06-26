package sanko.sankons.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository
import sanko.sankons.domain.comment.*; //Comment, CommentRepository
import sanko.sankons.web.dto.*; //CommentAddRequest, SessionUser

@RequiredArgsConstructor
@Service
public class CommentService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public Long add(CommentAddRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		Post post = postRepository.findById(request.getPost())
			.orElseThrow(() -> new Exception("Invalid post"));

		return commentRepository.save(Comment.builder()
			.post(post)
			.commenter(user)
			.content(request.getContent())
			.build()
		).getId();
	}

	public CommentListResponse list(CommentListRequest request) {
		int start = request.getStart();
		int length = request.getLength();

		List<Comment> comments = commentRepository.findAllByPostIdOrderByIdDesc(request.getPost());

		return new CommentListResponse(request.getPost(), comments, start, length);
	}

}
