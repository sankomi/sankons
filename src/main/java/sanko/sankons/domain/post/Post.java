package sanko.sankons.domain.post;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.TimedEntity;
import sanko.sankons.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post extends TimedEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "poster", nullable = false)
	@ManyToOne
	private User poster;

	@Column(name = "image")
	private String image;

	@Column(name = "content")
	private String content;

	@Builder
	public Post(User poster, String image, String content) {
		this.poster = poster;
		this.image = image;
		this.content = content;
	}

}
