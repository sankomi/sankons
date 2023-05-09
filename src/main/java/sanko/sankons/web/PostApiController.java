package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestMapping, PostMapping, ResponseBody
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.post.Post;
import sanko.sankons.service.PostService;
import sanko.sankons.web.dto.PostPostRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostApiController {

	private final PostService postService;

	@PostMapping("/post")
	public Long post(@Valid @RequestBody PostPostRequest request) throws Exception {
		Post post = postService.post(request);

		if (post == null) {
			throw new Exception("Could not post post");
		}

		return post.getId();
	}

}
