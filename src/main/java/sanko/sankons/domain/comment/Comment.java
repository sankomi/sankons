package sanko.sankons.domain.comment;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.TimedEntity;
import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends TimedEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "post", nullable = false)
	@ManyToOne
	private Post post;

	@JoinColumn(name = "commenter", nullable = false)
	@ManyToOne
	private User commenter;

	@Column(name = "content")
	private String content;

	@Builder
	public Comment(Post post, User commenter, String content) {
		this.post = post;
		this.commenter = commenter;
		this.content = content;
	}

}
