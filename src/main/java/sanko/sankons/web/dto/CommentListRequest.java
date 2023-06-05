package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

@Getter
public class CommentListRequest {

	private Long post;
	private int start;

	@Builder
	public CommentListRequest(Long post, int start) {
		this.post = post;
		this.start = start;
	}

}
