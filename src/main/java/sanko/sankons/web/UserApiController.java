package sanko.sankons.web;

import org.springframework.web.bind.annotation.*; //RestController, RequestBody, PostMapping
import lombok.RequiredArgsConstructor;

import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.UserCreateRequestDto;

@RequiredArgsConstructor
@RestController
public class UserApiController {

	private final UserService userService;

	@PostMapping("/api/v1/user/")
	public Long create(@RequestBody UserCreateRequestDto requestDto) {
		return userService.create(requestDto);
	}

}
