package sanko.sankons.web;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final UserService userService;

	@PostMapping("/create")
	public Long create(@RequestBody UserCreateRequest request) {
		return userService.create(request);
	}

	@GetMapping("/login")
	public String checkLogin() {
		return userService.checkLogin();
	}

	@PostMapping("/login")
	public Boolean login(@RequestBody UserLoginRequest request) {
		return userService.login(request);
	}

}
