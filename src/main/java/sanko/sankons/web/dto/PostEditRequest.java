package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*; //Getter, NoArgsConstructor

import sanko.sankons.domain.post.PostVisibility;

@Getter
@NoArgsConstructor
public class PostEditRequest {

	@NotNull(message = "Post can't be null") 
	private Long post;
	private String content;
	private PostVisibility visibility;

	@Builder
	public PostEditRequest(Long post, String content, PostVisibility visibility) {
		this.post = post;
		this.content = content;
		this.visibility = visibility;
	}

}
