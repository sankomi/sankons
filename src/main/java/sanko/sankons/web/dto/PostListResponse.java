package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.post.Post;

@Getter
public class PostListResponse {
 
	private int end;
	private List<PostViewResponse> posts;

	public PostListResponse(int end, List<PostViewResponse> postViews) {
		this.end = end;
		this.posts = postViews;
	}

}
