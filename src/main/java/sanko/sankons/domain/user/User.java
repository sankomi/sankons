package sanko.sankons.domain.user;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType

import lombok.*; //Builder, Getter, NoArgsConstructor

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Builder
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
