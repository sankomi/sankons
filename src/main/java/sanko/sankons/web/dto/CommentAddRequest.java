package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*; //NoArgsConstructor, Getter, Builder

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;

@NoArgsConstructor
@Getter
public class CommentAddRequest {

	@NotNull(message = "Post can't be null")
	private Long post;

	private String content;

	@Builder
	public CommentAddRequest(Long post, String content) {
		this.post = post;
		this.content = content;
	}

}
