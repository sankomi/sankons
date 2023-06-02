package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.post.Post;

@Getter
public class PostListResponse {
 
	private List<PostViewResponse> posts;

	public PostListResponse(List<PostViewResponse> postViews) {
		this.posts = postViews;
	}

}
