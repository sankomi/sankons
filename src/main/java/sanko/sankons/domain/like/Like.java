package sanko.sankons.domain.like;

import jakarta.persistence.*; //Entity, Table, UniqueConstraint, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.TimedEntity;
import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "likes", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"liker", "post"})
})
public class Like extends TimedEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "liker", nullable = false)
	@ManyToOne
	private User liker;

	@JoinColumn(name = "post", nullable = false)
	@ManyToOne
	private Post post;

	@Builder
	public Like(User liker, Post post) {
		this.liker = liker;
		this.post = post;
	}

}
