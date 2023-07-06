package sanko.sankons.domain.post;

public enum PostVisibility {

	ALL(1),
	SELF(2);

	public final int value;

	private PostVisibility(int value) {
		this.value = value;
	}

}
