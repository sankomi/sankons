package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*; //Getter, NoArgsConstructor, Builder

@Getter
@NoArgsConstructor
public class PostLikeRequest {

	@NotNull(message = "Post can't be null")
	private Long post;

	@Builder
	public PostLikeRequest(Long post) {
		this.post = post;
	}

}
