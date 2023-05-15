package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.*; //Getter, Builder

import sanko.sankons.domain.post.Post;

@Getter
public class PostListResponse {

	private int start;
	private int end;
	private List<PostViewResponse> posts;

	@Builder
	public PostListResponse(int start, int end, List<Post> posts) {
		this.start = start;
		this.end = end;
		this.posts = posts.stream()
			.map(PostViewResponse::new)
			.collect(Collectors.toList());
	}

}
