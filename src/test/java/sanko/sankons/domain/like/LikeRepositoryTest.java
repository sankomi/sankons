package sanko.sankons.domain.like;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.*; //Test, AfterEach

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository

@DataJpaTest
public class LikeRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private LikeRepository likeRepository;

	@AfterEach
	public void after() {
		likeRepository.deleteAll();
	}

	@Test
	public void testLike() {
		//given
		String username = "liker";
		String password = "password";
		String file = "file.txt";
		String content = "content";

		User user = userRepository.save(User.builder()
			.username(username)
			.password(password)
			.build());

		Post post = postRepository.save(Post.builder()
			.poster(user)
			.image(file)
			.content(content)
			.build());

		//when
		likeRepository.save(Like.builder()
			.liker(user)
			.post(post)
			.build());

		//then
		Like like = likeRepository.findByLikerAndPost(user, post);
		assertTrue(like != null);
		assertEquals(username, like.getLiker().getUsername());
		assertEquals(content, like.getPost().getContent());
	}

}
