package sanko.sankons.domain.hashtag;

import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository

@DataJpaTest
public class HashtagRepositoryTest {

	@Autowired
	private HashtagRepository hashtagRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Test
	public void testHashtag() {
		//given
		String username = "username";
		String password = "password";

		User user = User.builder()
			.username(username)
			.password(password)
			.build();
		userRepository.save(user);

		String image = "image";
		String content = "content";

		Post post = Post.builder()
			.poster(user)
			.image(image)
			.content(content)
			.build();
		postRepository.save(post);

		String tag = "tag";

		//when
		hashtagRepository.save(Hashtag.builder()
			.post(post)
			.tag(tag)
			.build());
		List<Hashtag> hashtags = hashtagRepository.findAll();

		//then
		assertTrue(hashtags.size() > 0);
		Hashtag hashtag = hashtags.get(0);
		assertTrue(hashtag.getId() > 0L);
		assertEquals(content, hashtag.getPost().getContent());
		assertEquals(tag, hashtag.getTag());
	}

}
