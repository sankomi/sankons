package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostDeleteRequest {

	@NotNull(message = "Post can't be null")
	private Long post;
	
}
