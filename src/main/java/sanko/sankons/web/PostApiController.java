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
import sanko.sankons.web.dto.*; //PostPostRequest, PostDeleteRequest, PostListRequest, PostListResponse, PostViewResponse, PostCheckLikeRequest, PostCheckLikeResponse, PostLikeRequest, PostLikeResponse, SessionUser

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostApiController {

	private final PostService postService;

	@PostMapping("/post")
	public Long post(@Valid @RequestPart PostPostRequest request, @RequestPart MultipartFile file, @LoginUser SessionUser sessionUser) throws Exception {
		Long id = postService.post(request, file, sessionUser);

		if (id == null) {
			throw new Exception("Could not post post");
		}

		return id;
	}

	@DeleteMapping("/delete")
	public Boolean delete(@Valid @RequestBody PostDeleteRequest request, @LoginUser SessionUser sessionUser) throws Exception {
		return postService.delete(request, sessionUser);
	}

	@GetMapping("/{id}")
	public PostViewResponse view(@PathVariable Long id, @LoginUser SessionUser sessionUser) throws Exception {
		return postService.view(id, sessionUser);
	}

	@GetMapping("/list")
	public PostListResponse list(PostListRequest request, @LoginUser SessionUser sessionUser) {
		return postService.list(request, sessionUser);
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

	@GetMapping("/like")
	public PostCheckLikeResponse checkLike(PostCheckLikeRequest request, @LoginUser SessionUser sessionUser) throws Exception {
		return postService.checkLike(request, sessionUser);
	}

	@PutMapping("/like")
	public PostLikeResponse like(@Valid @RequestBody PostLikeRequest request, @LoginUser SessionUser sessionUser) throws Exception {
		return postService.like(request, sessionUser);
	}

}
