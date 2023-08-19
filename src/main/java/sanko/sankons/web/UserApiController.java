package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping, DeleteMapping, PutMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.service.*; //UserService, FollowService
import sanko.sankons.web.dto.*; //SessionUser, UserCreateRequest, UserLoginRequest, UserChangePasswordRequest, UserChangeNameRequest, UserFollowRequest, UserCheckFollowRequest, UserFollowingResponse, UserFollowerResponse

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final UserService userService;
	private final FollowService followService;

	@PostMapping("/create")
	public Long create(@Valid @RequestBody UserCreateRequest request) throws Exception {
		Long id = userService.create(request);

		if (id == null) {
			throw new Exception("Could not create user");
		}

		return id;
	}

	@PutMapping("/password")
	public Boolean changePassword(
		@Valid @RequestBody UserChangePasswordRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return userService.changePassword(request, sessionUser);
	}

	@GetMapping("/login")
	public String checkLogin(@LoginUser SessionUser sessionUser) {
		return userService.checkLogin(sessionUser);
	}

	@PostMapping("/login")
	public Boolean login(@Valid @RequestBody UserLoginRequest request) {
		return userService.login(request);
	}

	@DeleteMapping("/login")
	public Boolean logout() {
		return userService.logout();
	}

	@PutMapping("/username")
	public Boolean changeUsername(
		@Valid @RequestBody UserChangeNameRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return userService.changeUsername(request, sessionUser);
	}

	@PutMapping("/follow")
	public Boolean follow(
		@Valid @RequestBody UserFollowRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return followService.follow(request, sessionUser);
	}

	@DeleteMapping("/follow")
	public Boolean unfollow(
		@Valid @RequestBody UserFollowRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return followService.unfollow(request, sessionUser);
	}

	@GetMapping("/follow")
	public Boolean checkFollow(
		@Valid UserCheckFollowRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return followService.checkFollow(request, sessionUser);
	}

	@GetMapping("/followers")
	public UserFollowerResponse getFollowers(
		@Valid UserCheckFollowRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return followService.getFollowers(request, sessionUser);
	}

	@GetMapping("/followings")
	public UserFollowingResponse getFollowings(
		@Valid UserCheckFollowRequest request,
		@LoginUser SessionUser sessionUser
	) throws Exception {
		return followService.getFollowings(request, sessionUser);
	}

}
