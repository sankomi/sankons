package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*; //Getter, NoArgsConstructor

@Getter
@NoArgsConstructor
public class PostEditRequest {

	@NotNull(message = "Post can't be null") 
	private Long post;
	private String content;

	@Builder
	public PostEditRequest(Long post, String content) {
		this.post = post;
		this.content = content;
	}

}
