package sanko.sankons.domain.comment;

import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.post.*; //Post, PostRepository

@DataJpaTest
public class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	private static final String username = "username";
	private static final String password = "password";
	private static final String postImage = "image.png";
	private static final String postContent = "post content";

	@Test
	public void testComment() {
		//given
		User user = userRepository.save(User.builder()
			.username(username)
			.password(password)
			.build());

		Post post = postRepository.save(Post.builder()
			.poster(user)
			.image(postImage)
			.content(postContent)
			.build());
		String content = "comment content";

		//when
		commentRepository.save(Comment.builder()
			.post(post)
			.commenter(user)
			.content(content)
			.build());

		//then
		List<Comment> comments = commentRepository.findAll();
		Comment comment = comments.get(0);
		assertEquals(post.getId(), comment.getPost().getId());
		assertEquals(user.getId(), comment.getCommenter().getId());
		assertEquals(user.getUsername(), comment.getCommenter().getUsername());
		assertEquals(content, comment.getContent());
	}

}
