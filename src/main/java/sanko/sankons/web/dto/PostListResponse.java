package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.*; //Getter, Builder

import sanko.sankons.domain.post.Post;

@Getter
public class PostListResponse {

	private int page;
	private int size;
	private List<PostViewResponse> posts;

	@Builder
	public PostListResponse(int page, int size, List<Post> posts) {
		this.page = page;
		this.size = size;
		this.posts = posts.stream()
			.map(PostViewResponse::new)
			.collect(Collectors.toList());
	}

}
