package sanko.sankons.web.dto;

import java.io.Serializable;

import lombok.Getter;

import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.user.User;

@Getter
public class SessionPost implements Serializable {

	private Long id;
	private SessionUser poster;
	private String content;
	
	public SessionPost(Post post) {
		this.id = post.getId();
		this.poster = new SessionUser(post.getPoster());
		this.content = post.getContent();
	}

}
