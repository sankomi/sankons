package sanko.sankons.web.dto;

import lombok.*; //AllArgsConstructor, Getter

@AllArgsConstructor
@Getter
public class PostListRequest {

	private int start;
	private int length;
	private int commentLength;
	private String tag;

}
