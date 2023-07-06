package sanko.sankons.web.dto;

import java.time.LocalDateTime;
import java.util.*; //List, Set
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.post.Post;
import sanko.sankons.domain.like.Like;
import sanko.sankons.domain.comment.Comment;

@Getter
public class PostViewResponse {

	private Long id;
	private String content;
	private UserInfoResponse poster;
	private LocalDateTime posted;
	private List<CommentResponse> comments;
	private int views;
	private int likes;
	private boolean login;

	public PostViewResponse(Post post, int commentLength) {
		this.id = post.getId();
		this.content = post.getContent();
		this.poster = new UserInfoResponse(post.getPoster());
		this.posted = post.getCreated();
		Set<Comment> comments = post.getComments();
		if (comments == null) {
			this.comments = null;
		} else {
			this.comments = post.getComments()
				.stream()
				.limit(commentLength)
				.map(CommentResponse::new)
				.collect(Collectors.toList());
		}
		this.views = post.getViews();

		Set<Like> likes = post.getLikes(); 
		this.likes = likes == null? 0: likes.size();
	}

	public PostViewResponse(Post post) {
		this(post, 0);
	}

	public void setLogin(Long userId) {
		if (poster.getId().equals(userId)) {
			login = true;
		}

		if (comments != null) {
			for (CommentResponse comment : comments) {
				comment.setLogin(userId);
			}
		}
	}

}
