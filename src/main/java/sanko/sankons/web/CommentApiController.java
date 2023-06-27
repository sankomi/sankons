package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestMapping, PostMapping, RequestBody
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.CommentService;
import sanko.sankons.web.dto.*; //CommentAddRequest, CommentListRequest, CommentListResponse, SessionUser

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comment")
public class CommentApiController {

	private final CommentService commentService;

	@PostMapping("/add")
	public Long add(@Valid @RequestBody CommentAddRequest request, @LoginUser SessionUser sessionUser) throws Exception {

		return commentService.add(request, sessionUser);
	}

	@GetMapping("/list")
	public CommentListResponse list(CommentListRequest request) {
		return commentService.list(request);
	}

}
