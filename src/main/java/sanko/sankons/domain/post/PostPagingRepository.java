package sanko.sankons.domain.post;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Pageable;

public interface PostPagingRepository extends PagingAndSortingRepository<Post, Long> {

}
