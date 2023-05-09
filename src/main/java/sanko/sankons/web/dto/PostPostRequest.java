package sanko.sankons.web.dto;

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.post.Post;

@Getter
@NoArgsConstructor
public class PostPostRequest {

	private String content;

	@Builder
	public PostPostRequest(String content) {
		this.content = content;
	}

}
