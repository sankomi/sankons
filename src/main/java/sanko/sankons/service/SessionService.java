package sanko.sankons.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.web.dto.SessionUser;

@RequiredArgsConstructor
@Service
public class SessionService {

	private final HttpSession httpSession;

	public SessionUser getUser() {
		return (SessionUser) httpSession.getAttribute("user");
	}

	public void setUser(SessionUser sessionUser) {
		httpSession.setAttribute("user", sessionUser);
	}

	public void removeUser() {
		httpSession.removeAttribute("user");
	}

}
