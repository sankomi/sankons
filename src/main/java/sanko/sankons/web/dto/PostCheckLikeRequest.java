package sanko.sankons.web.dto;

import java.util.List;

import lombok.*; //Getter, Builder

@Getter
public class PostCheckLikeRequest {

	private List<Long> posts;

	@Builder
	public PostCheckLikeRequest(List<Long> posts) {
		this.posts = posts;
	}

}
