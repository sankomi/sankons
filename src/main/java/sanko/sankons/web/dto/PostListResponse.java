package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.post.Post;

@Getter
public class PostListResponse {
 
	private int end;
	private List<PostViewResponse> posts;

	public PostListResponse(List<Post> posts, int start, int length, int commentLength) {
		this.posts = posts.stream()
			.skip(start)
			.limit(length)
			.map(post -> {
				return new PostViewResponse(post, commentLength);
			})
			.collect(Collectors.toList());
		this.end = start + this.posts.size();
	}

}
