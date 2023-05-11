package sanko.sankons.web.dto;

import java.time.LocalDateTime;

import lombok.Getter;

import sanko.sankons.domain.post.Post;

@Getter
public class PostViewResponse {

	private Long id;
	private String image;
	private String content;
	private UserInfoResponse poster;
	private LocalDateTime posted;

	public PostViewResponse(Post post) {
		this.id = post.getId();
		this.image = post.getImage();
		this.content = post.getContent();
		this.poster = new UserInfoResponse(post.getPoster());
		this.posted = post.getCreated();
	}

}
