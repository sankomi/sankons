package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

@Getter
public class PostLikeResponse {

	private boolean liked;
	private int likes;

	@Builder
	public PostLikeResponse(boolean liked, int likes) {
		this.liked = liked;
		this.likes = likes;
	}

}
