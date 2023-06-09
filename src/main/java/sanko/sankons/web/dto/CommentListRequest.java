package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

@Getter
public class CommentListRequest {

	private Long post;
	private int start;
	private int length;

	@Builder
	public CommentListRequest(Long post, int start, int length) {
		this.post = post;
		this.start = start;
		this.length = length;
	}

}
