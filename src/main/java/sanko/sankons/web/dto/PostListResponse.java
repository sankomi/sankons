package sanko.sankons.web.dto;

import java.util.List;

import lombok.*; //Getter, Builder

@Getter
public class PostListResponse {

	private int start;
	private int end;
	private List<PostViewResponse> posts;

	@Builder
	public PostListResponse(int start, int end, List<PostViewResponse> posts) {
		this.start = start;
		this.end = end;
		this.posts = posts;
	}

}
