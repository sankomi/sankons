package sanko.sankons.web.dto;

import java.io.Serializable;

import lombok.Getter;

import sanko.sankons.domain.user.User;

@Getter
public class SessionUser implements Serializable {

	private String username;

	public SessionUser(User user) {
		this.username = user.getUsername();
	}

}
