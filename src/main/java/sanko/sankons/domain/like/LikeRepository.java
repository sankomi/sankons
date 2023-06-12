package sanko.sankons.domain.like;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {

	List<Like> findAllByLikerAndPostIdIn(User liker, List<Long> postIds);
	Like findByLikerAndPost(User liker, Post post);

}
