package sanko.sankons.domain.user;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import lombok.*; //Builder, Getter, NoArgsConstructor

import sanko.sankons.domain.TimedEntity;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends TimedEntity {

	private static final String SECRET = "muchsecret";
	private static final int SALT_LENGTH = 16;
	private static final int ITERATIONS = 65536;
	private static final int HASH_WIDTH = 256;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Builder
	public User(String username, String password) {
		this.username = username;
		this.password = encodePassword(password);
	}

	private String encodePassword(String password) {
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, ITERATIONS, HASH_WIDTH);
		return encoder.encode(password);
	}

	public boolean checkPassword(String password) {
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, ITERATIONS, HASH_WIDTH);
		return encoder.matches(password, this.password);
	}

}
