package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestMapping, PostMapping, RequestBody, DeleteMapping, GetMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.CommentService;
import sanko.sankons.web.dto.*; //CommentAddRequest, CommentDeleteRequest, CommentListRequest, CommentListResponse, SessionUser

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comment")
public class CommentApiController {

	private final CommentService commentService;

	@PostMapping("/add")
	public Long add(@Valid @RequestBody CommentAddRequest request, @LoginUser SessionUser sessionUser) throws Exception {

		return commentService.add(request, sessionUser);
	}

	@DeleteMapping("/delete")
	public Boolean delete(@Valid @RequestBody CommentDeleteRequest request, @LoginUser SessionUser sessionUser) throws Exception {
		return commentService.delete(request, sessionUser);
	}

	@GetMapping("/list")
	public CommentListResponse list(CommentListRequest request, @LoginUser SessionUser sessionUser) {
		return commentService.list(request, sessionUser);
	}

}
