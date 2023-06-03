package sanko.sankons.web.dto;

import java.io.Serializable;

import lombok.Getter;

import sanko.sankons.domain.comment.Comment;
import sanko.sankons.domain.user.User;

@Getter
public class SessionComment implements Serializable {

	private Long id;
	private SessionUser commenter;
	private String content;

	public SessionComment(Comment comment) {
		this.id = comment.getId();
		this.commenter = new SessionUser(comment.getCommenter());
		this.content = comment.getContent();
	}

}
