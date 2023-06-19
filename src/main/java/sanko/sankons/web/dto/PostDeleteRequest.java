package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostDeleteRequest {

	@NotNull
	private Long post;
	
}
