package com.loserico.concurrent.chapter4;

public final class Update implements Comparable<Update> {

	/*
	 * Final fields must be initialized in constructor
	 */
	private final Author author;
	private final String updateText;
	private final long createTime;

	public Author getAuthor() {
		return author;
	}

	public String getUpdateText() {
		return updateText;
	}

	private Update(Builder builder) {
		this.author = builder.author;
		this.updateText = builder.updateText;
		this.createTime = builder.createTime;
	}

	/**
	 * Builder class must be static inner
	 * 
	 * @author Loser
	 * @since Jul 13, 2016
	 * @version
	 *
	 */
	public static class Builder implements ObjBuilder<Update> {
		public long createTime;
		private Author author;
		private String updateText;

		/*
		 * Methods on Builder return Builder for chain calls
		 */
		public Builder author(Author author) {
			this.author = author;
			return this;
		}

		public Builder updateText(String updateText) {
			this.updateText = updateText;
			return this;
		}

		public Builder createTime(long createTime) {
			this.createTime = createTime;
			return this;
		}

		public Update build() {
			return new Update(this);
		}
	}

	public int compareTo(Update other) {
		if (null == other) {
			throw new NullPointerException();
		}
		return (int) (other.createTime - this.createTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + (int) (createTime ^ (createTime >>> 32));
		result = prime * result + ((updateText == null) ? 0 : updateText.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Update other = (Update) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (createTime != other.createTime) {
			return false;
		}
		if (updateText == null) {
			if (other.updateText != null) {
				return false;
			}
		} else if (!updateText.equals(other.updateText)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Update [author=" + author + ", updateText=" + updateText + ", createTime=" + createTime + "]";
	}

}
