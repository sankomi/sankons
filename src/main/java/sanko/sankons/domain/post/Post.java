package sanko.sankons.domain.post;

import java.util.Set;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne, OneToMany, OrderBy

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.TimedEntity;
import sanko.sankons.domain.user.User;
import sanko.sankons.domain.comment.Comment;
import sanko.sankons.domain.like.Like;
import sanko.sankons.domain.hashtag.Hashtag;

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

	@Column(name = "visibility", columnDefinition = "INTEGER DEFAULT 0")
	private PostVisibility visibility = PostVisibility.ALL;

	@OneToMany(mappedBy = "post")
	@OrderBy("id DESC")
	private Set<Comment> comments;

	@Column(name = "views", columnDefinition = "INTEGER DEFAULT 0")
	private int views;

	@OneToMany(mappedBy = "post")
	private Set<Like> likes;

	@OneToMany(mappedBy = "post")
	@OrderBy("tag ASC")
	private Set<Hashtag> hashtags;

	@Builder
	public Post(User poster, String image, String content, PostVisibility visibility, Set<Comment> comments) {
		this.poster = poster;
		this.image = image;
		this.content = content;
		this.visibility = visibility;
		this.comments = comments;
	}

	public void view() {
		this.views++;
	}

}
