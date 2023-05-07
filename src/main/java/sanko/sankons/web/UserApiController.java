package sanko.sankons.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, RequestMapping, GetMapping, PostMapping, DeleteMapping
import org.springframework.http.*; //ResponseEntity, HttpStatus
import org.springframework.validation.Errors;
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

	private final UserService userService;

	@PostMapping("/create")
	public ResponseEntity<Long> create(@Valid @RequestBody UserCreateRequest request, Errors errors) {
		if (errors.hasErrors()) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

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
	public ResponseEntity<Boolean> login(@Valid @RequestBody UserLoginRequest request, Errors errors) {
		if (errors.hasErrors()) {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
	}

	@DeleteMapping("/login")
	public Boolean logout() {
		return userService.logout();
	}

}
