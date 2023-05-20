package sanko.sankons.web;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*; //RestController, GetMapping
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class IndexController {

	private final HttpSession httpSession;

	@GetMapping("/test")
	public String index() {
		httpSession.setAttribute("test", String.valueOf(Math.random()));

		return (String) httpSession.getAttribute("test");
	}

}
