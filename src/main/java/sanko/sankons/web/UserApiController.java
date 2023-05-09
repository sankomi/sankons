package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping, DeleteMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final UserService userService;

	@PostMapping("/create")
	public Long create(@Valid @RequestBody UserCreateRequest request) throws Exception {
		User user = userService.create(request);

		if (user == null) {
			throw new Exception("Could not create user");
		}

		return user.getId();
	}

	@GetMapping("/login")
	public String checkLogin() {
		return userService.checkLogin();
	}

	@PostMapping("/login")
	public Boolean login(@Valid @RequestBody UserLoginRequest request) {
		return userService.login(request);
	}

	@DeleteMapping("/login")
	public Boolean logout() {
		return userService.logout();
	}

}
