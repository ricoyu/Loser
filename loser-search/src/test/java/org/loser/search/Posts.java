package org.loser.search;

import java.time.LocalDate;

import com.loserico.search.annotation.Indexed;

@Indexed(index="posts", type="doc", id="id")
public class Posts {
	
	private Long id;

	private String user;

	private LocalDate postDate;

	private String message;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public LocalDate getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDate postDate) {
		this.postDate = postDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
