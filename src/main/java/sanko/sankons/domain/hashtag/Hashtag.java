package sanko.sankons.domain.hashtag;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.post.Post;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "hashtags")
public class Hashtag {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "post", nullable = false)
	@ManyToOne
	private Post post;

	@Column(name = "tag", nullable = false)
	private String tag;

	@Builder
	public Hashtag(Post post, String tag) {
		this.post = post;
		this.tag = tag;
	}

}
