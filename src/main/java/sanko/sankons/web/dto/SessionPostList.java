package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

import lombok.Getter;

import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.user.User;

@Getter
public class SessionPostList implements Serializable {

	private Long length;
	private List<SessionPost> posts;

	public SessionPostList(List<Post> posts) {
		this.posts = posts.stream()
			.map(SessionPost::new)
			.collect(Collectors.toList());
	}

	public PostListResponse toResponse(int start, int length) {
		List<PostViewResponse> postViews = posts.stream()
			.skip(start)
			.limit(length)
			.map(PostViewResponse::new)
			.collect(Collectors.toList());

		int end = start + postViews.size();
		
		return new PostListResponse(end, postViews);
	}

}
