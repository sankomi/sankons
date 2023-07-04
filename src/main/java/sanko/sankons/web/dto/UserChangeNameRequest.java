package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.*; //Getter, NoArgsConstructor, Builder

@Getter
@NoArgsConstructor
public class UserChangeNameRequest {

	@NotBlank(message = "Username can't be blank")
	private String username;

	@Builder
	public UserChangeNameRequest(String username) {
		this.username = username;
	}

}
