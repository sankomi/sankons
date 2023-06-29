package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@NoArgsConstructor
public class CommentDeleteRequest {

	@NotNull
	private Long comment;

	public CommentDeleteRequest(Long comment) {
		this.comment = comment;
	}

}
