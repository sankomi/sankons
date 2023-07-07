package sanko.sankons.domain.post;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.*; //Test, AfterEach

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.domain.user.*; //User, UserRepository

@DataJpaTest
public class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void after() {
		postRepository.deleteAll();
	}

	@Test
	public void testPost() {
		//given
		String username = "poster";
		String password = "password";
		String file = "file.txt";
		String content = "content";
		PostVisibility visibility = PostVisibility.ALL;

		User user = userRepository.save(User.builder()
			.username(username)
			.password(password)
			.build());

		//when
		postRepository.save(Post.builder()
			.poster(user)
			.image(file)
			.content(content)
			.visibility(visibility)
			.build());
		List<Post> posts = postRepository.findAll();

		//then
		Post post = posts.get(0);
		assertEquals(user.getId(), post.getPoster().getId());
		assertEquals(file, post.getImage());
		assertEquals(content, post.getContent());
	}

}
