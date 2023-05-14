package sanko.sankons.web;

import java.util.List;
import java.io.*; //File, FileInputStream
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestMapping, PostMapping, ResponsePart, GetMapping, PathVariable
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*; //ResponseEntity, MediaType
import org.springframework.core.io.InputStreamResource;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.post.Post;
import sanko.sankons.service.PostService;
import sanko.sankons.web.dto.PostPostRequest;
import sanko.sankons.web.dto.PostListRequest;
import sanko.sankons.web.dto.PostListResponse;
import sanko.sankons.web.dto.PostViewResponse;

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

	@GetMapping("/{id}")
	public PostViewResponse view(@PathVariable Long id) throws Exception {
		return postService.view(id);
	}

	@GetMapping("/list")
	public PostListResponse list(PostListRequest request) {
		List<PostViewResponse> posts = postService.list(request);

		return PostListResponse.builder()
			.start(0)
			.end(0)
			.posts(posts)
			.build();
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<InputStreamResource> getImage(@PathVariable Long id) throws Exception {
		File file = postService.getImage(id);

		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		InputStream inputStream = new FileInputStream(file);

		return ResponseEntity.ok()
			.contentType(MediaType.valueOf(mimeType))
			.body(new InputStreamResource(inputStream));
	}

}
