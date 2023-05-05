package sanko.sankons.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest, SessionUser

@RequiredArgsConstructor
@Service
public class UserService {

	private final HttpSession httpSession;
	private final UserRepository userRepository;

	@Transactional
	public Long create(UserCreateRequest request) {
		return userRepository.save(request.toEntity()).getId();
	}

	public String checkLogin() {
		SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

		if (sessionUser == null) return null;
		
		return sessionUser.getUsername();
	}

	public Boolean login(UserLoginRequest request) {
		User user = userRepository.findFirstByUsername(request.getUsername());

		if (user != null && user.checkPassword(request.getPassword())) {
			SessionUser sessionUser = new SessionUser(user);
			httpSession.setAttribute("user", sessionUser);
			return true;
		}

		return false;
	}

}
