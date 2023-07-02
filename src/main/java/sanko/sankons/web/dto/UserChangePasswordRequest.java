package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.*; //Getter, NoArgsConstructor, Builder

@Getter
@NoArgsConstructor
public class UserChangePasswordRequest {

	@NotEmpty(message = "Password can't be empty")
	private String oldPassword;

	@NotEmpty(message = "Password can't be empty")
	private String newPassword;

	@NotEmpty(message = "Password can't be empty")
	private String confirmPassword;

	@Builder
	public UserChangePasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}

	public boolean confirm() {
		return newPassword == confirmPassword;
	}

}
