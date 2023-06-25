package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping, DeleteMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.service.*; //UserService, SessionService
import sanko.sankons.web.dto.*; //SessionUser, UserCreateRequest, UserLoginRequest

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final SessionService sessionService;
	private final UserService userService;

	@PostMapping("/create")
	public Long create(@Valid @RequestBody UserCreateRequest request) throws Exception {
		Long id = userService.create(request);

		if (id == null) {
			throw new Exception("Could not create user");
		}

		return id;
	}

	@GetMapping("/login")
	public String checkLogin() {
		SessionUser sessionUser = sessionService.getUser();

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

}
