package sanko.sankons.domain.user;

import jakarta.persistence.*; //Entity, Table, Id, Column, GeneratedValue, GenerationType

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
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
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, ITERATIONS, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		return encoder.encode(password);
	}

	public boolean checkPassword(String password) {
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, ITERATIONS, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		return encoder.matches(password, this.password);
	}

	public boolean changePassword(String password) {
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, ITERATIONS, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		password = encoder.encode(password);

		return true;
	}

	public boolean changeUsername(String username) {
		this.username = username;

		return true;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null) return false;
		return ((User) object).getId() == id;
	}

}
