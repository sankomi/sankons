package sanko.sankons.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.post.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {

	Like findByLikerAndPost(User liker, Post post);

}
