package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

import sanko.sankons.domain.comment.Comment;

@Getter
public class CommentResponse {

	private Long id;
	private UserInfoResponse commenter;
	private String content;
	private boolean login;

	@Builder
	public CommentResponse(Comment comment) {
		this.id = comment.getId();
		this.commenter = new UserInfoResponse(comment.getCommenter());
		this.content = comment.getContent();
	}

	public void setLogin(Long userId) {
		if (commenter.getId().equals(userId)) {
			login = true;
		}
	}

}
