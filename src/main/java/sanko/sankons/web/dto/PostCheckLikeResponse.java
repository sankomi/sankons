package sanko.sankons.web.dto;

import java.util.List;

import lombok.*; //Getter, Builder

@Getter
public class PostCheckLikeResponse {

	private List<PostLikeResponse> likes;

	public PostCheckLikeResponse(List<PostLikeResponse> likes) {
		this.likes = likes;
	}

}
