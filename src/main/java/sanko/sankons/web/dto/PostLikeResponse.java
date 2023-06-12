package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

@Getter
public class PostLikeResponse {

	private Long post;
	private boolean liked;
	private int likes;

	@Builder
	public PostLikeResponse(Long post, boolean liked, int likes) {
		this.post = post;
		this.liked = liked;
		this.likes = likes;
	}

}
