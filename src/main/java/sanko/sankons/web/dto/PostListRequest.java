package sanko.sankons.web.dto;

import lombok.*; //AllArgsConstructor, Getter

@AllArgsConstructor
@Getter
public class PostListRequest {

	private int page;
	private int size;

}
