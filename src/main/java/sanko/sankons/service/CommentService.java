package sanko.sankons.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository
import sanko.sankons.domain.comment.*; //Comment, CommentRepository
import sanko.sankons.web.dto.*; //CommentAddRequest, SessionUser, CommentDeleteRequest, CommentListRequest

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

	public Boolean delete(CommentDeleteRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) {
			throw new Exception("Not logged in");
		}

		User user = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));

		Comment comment = commentRepository.findById(request.getComment())
			.orElseThrow(() -> new Exception("Comment not found"));

		if (comment.getCommenter().equals(user)) {
			commentRepository.delete(comment);
		} else {
			throw new Exception("Not commenter");
		}

		return true;
	}

	public CommentListResponse list(CommentListRequest request, SessionUser sessionUser) {
		int start = request.getStart();
		int length = request.getLength();

		Long userId = sessionUser == null? null: sessionUser.getId();

		List<Comment> comments = commentRepository.findAllByPostIdOrderByIdDesc(request.getPost());

		return new CommentListResponse(request.getPost(), comments, userId, start, length);
	}

}
