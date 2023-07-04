package sanko.sankons.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.service.SessionService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest, UserChangePasswordRequest, UserChangeNameRequest, SessionUser

@RequiredArgsConstructor
@Service
public class UserService {

	private final SessionService sessionService;
	private final UserRepository userRepository;

	public Long create(UserCreateRequest request) {
		try {
			return userRepository.save(request.toEntity()).getId();
		} catch (Exception e) {
			return null;
		}
	}

	public String checkLogin(SessionUser sessionUser) {
		if (sessionUser == null) return null;

		return sessionUser.getUsername();
	}

	public Boolean changePassword(UserChangePasswordRequest request, SessionUser sessionUser) throws Exception {
		if (!request.confirm()) throw new Exception("Confirm password does not match");

		if (sessionUser == null) throw new Exception("Not logged in");

		User user = findUserById(sessionUser.getId());

		if (!user.checkPassword(request.getOldPassword())) throw new Exception("Incorrect password");

		return user.changePassword(request.getNewPassword());
	}

	public Boolean login(UserLoginRequest request) {
		User user = userRepository.findFirstByUsername(request.getUsername());

		if (user != null && user.checkPassword(request.getPassword())) {
			SessionUser sessionUser = new SessionUser(user);
			sessionService.setUser(sessionUser);
			return true;
		}

		return false;
	}

	public Boolean logout() {
		sessionService.removeUser();
		
		return true;
	}

	public Boolean changeUsername(UserChangeNameRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User user = findUserById(sessionUser.getId());

		return user.changeUsername(request.getUsername());
	}

	private User findUserById(Long id) throws Exception {
		return userRepository.findById(id)
			.orElseThrow(() -> new Exception("Invalid user"));
	}

}
