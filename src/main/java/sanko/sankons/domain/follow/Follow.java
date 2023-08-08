package sanko.sankons.domain.follow;

import jakarta.persistence.*; //Entity, Table, UniqueConstraint, Id, Column, GeneratedValue, GenerationType, JoinColumn, ManyToOne

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.TimedEntity;
import sanko.sankons.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "follows", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"follower", "following"})
})
public class Follow extends TimedEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "follower", nullable = false)
	@ManyToOne
	private User follower;

	@JoinColumn(name = "following", nullable = false)
	@ManyToOne
	private User following;

	@Builder
	public Follow(User follower, User following) {
		this.follower = follower;
		this.following = following;
	}

}
