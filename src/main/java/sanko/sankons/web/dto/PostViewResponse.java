package sanko.sankons.web.dto;

import java.time.LocalDateTime;
import java.util.*; //List, Set
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.comment.Comment;

@Getter
public class PostViewResponse {

	private Long id;
	private String content;
	private UserInfoResponse poster;
	private LocalDateTime posted;
	private List<CommentResponse> comments;

	public PostViewResponse(Post post) {
		this.id = post.getId();
		this.content = post.getContent();
		this.poster = new UserInfoResponse(post.getPoster());
		this.posted = post.getCreated();
		Set<Comment> comments = post.getComments();
		if (comments == null) {
			this.comments = null;
		} else {
			this.comments = post.getComments()
				.stream()
				.map(CommentResponse::new)
				.collect(Collectors.toList());
		}
	}

}
