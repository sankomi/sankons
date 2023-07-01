package sanko.sankons.web.dto;

import lombok.*; //Getter, NoArgsConstructor, Builder

@Getter
@NoArgsConstructor
public class UserChangePasswordRequest {

	private String oldPassword;
	private String newPassword;
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
