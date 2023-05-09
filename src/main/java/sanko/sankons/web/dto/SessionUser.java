package sanko.sankons.web.dto;

import java.io.Serializable;

import lombok.Getter;

import sanko.sankons.domain.user.User;

@Getter
public class SessionUser implements Serializable {

	private Long id;
	private String username;

	public SessionUser(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
	}

}
