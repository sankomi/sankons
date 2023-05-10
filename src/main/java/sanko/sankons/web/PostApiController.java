package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestMapping, PostMapping, ResponsePart
import org.springframework.web.multipart.MultipartFile;
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
	public Long post(@Valid @RequestPart PostPostRequest request, @RequestPart MultipartFile file) throws Exception {
		Post post = postService.post(request, file);

		if (post == null) {
			throw new Exception("Could not post post");
		}

		return post.getId();
	}

}
