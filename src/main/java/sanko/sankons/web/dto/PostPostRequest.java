package sanko.sankons.web.dto;

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.post.*; //Post, PostVisibility

@Getter
@NoArgsConstructor
public class PostPostRequest {

	private String content;
	private PostVisibility visibility;

	@Builder
	public PostPostRequest(String content, PostVisibility visibility) {
		this.content = content;
		this.visibility = visibility;
	}

}
