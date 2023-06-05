package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.*; //Getter, Builder

import sanko.sankons.domain.comment.Comment;

@Getter
public class CommentListResponse {

	private Long post;
	private List<CommentResponse> comments;
	private int end;

	@Builder
	public CommentListResponse(Long post, List<Comment> comments, int start, int length) {
		this.post = post;
		this.comments = comments.stream()
			.skip(start)
			.limit(length)
			.map(CommentResponse::new)
			.collect(Collectors.toList());
		this.end = start + this.comments.size();
	}

}
