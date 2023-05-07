package sanko.sankons.web;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping, DeleteMapping
import org.springframework.http.*; //ResponseEntity, HttpStatus
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final UserService userService;

	@PostMapping("/create")
	public ResponseEntity<Long> create(@RequestBody UserCreateRequest request) {
		Long id = userService.create(request);

		if (id == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(id, HttpStatus.OK);
	}

	@GetMapping("/login")
	public String checkLogin() {
		return userService.checkLogin();
	}

	@PostMapping("/login")
	public Boolean login(@RequestBody UserLoginRequest request) {
		return userService.login(request);
	}

	@DeleteMapping("/login")
	public Boolean logout() {
		return userService.logout();
	}

}
