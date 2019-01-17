package com.loserico.search.option;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.unit.TimeValue;

/**
 * 更新选项
 * <p>
 * Copyright: Copyright (c) 2018-08-23 21:54
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class UpdateOption {

	private String routing;

	private String parent;

	/**
	 * Timeout(seconds) to wait for primary shard to become available
	 */
	private Long timeout;

	/**
	 * If the document does not already exist, it is possible to define some
	 * content that will be inserted as a new document using the upsert
	 * method
	 */
	private boolean upsert;

	/**
	 * Indicate that the script must run regardless of whether the document
	 * exists or not, ie the script takes care of creating the document if
	 * it does not already exist.
	 */
	private boolean scriptedUpsert;

	/**
	 * Indicate that the partial document must be used as the upsert
	 * document if it does not exist yet.
	 */
	private boolean docAsUpsert;

	private boolean fetchSource;

	private UpdateOption(Builder builder) {
		this.routing = builder.routing;
		this.parent = builder.parent;
		this.timeout = builder.timeout;
		this.upsert = builder.upsert;
		this.scriptedUpsert = builder.scriptedUpsert;
		this.docAsUpsert = builder.docAsUpsert;
		this.fetchSource = builder.fetchSource;
	}

	public void apply(UpdateRequest updateRequest) {
		if (scriptedUpsert) {
			updateRequest.scriptedUpsert(true);
		}
		if (docAsUpsert) {
			updateRequest.docAsUpsert(true);
		}
		if (isNotBlank(routing)) {
			updateRequest.routing(routing);
		}
		if (isNotBlank(parent)) {
			updateRequest.parent(parent);
		}
		if (timeout != null) {
			updateRequest.timeout(TimeValue.timeValueSeconds(timeout));
		}
		if (fetchSource) {
			updateRequest.fetchSource(true);
		}
	}

	public static Builder fetchSource(boolean fetchSource) {
		return new Builder(fetchSource);
	}

	public boolean isUpsert() {
		return upsert;
	}

	public static final class Builder {
		private String routing;
		private String parent;
		private Long timeout;
		private boolean upsert;
		private boolean scriptedUpsert;
		private boolean docAsUpsert;
		private boolean fetchSource;

		private Builder(boolean fetchSource) {
			this.fetchSource = fetchSource;
		}

		public Builder routing(String routing) {
			this.routing = routing;
			return this;
		}

		public Builder parent(String parent) {
			this.parent = parent;
			return this;
		}

		public Builder timeout(long timeout) {
			this.timeout = timeout;
			return this;
		}

		public UpdateOption upsert(boolean upsert) {
			this.upsert = upsert;
			return new UpdateOption(this);
		}

		public UpdateOption scriptedUpsert(boolean scriptedUpsert) {
			this.scriptedUpsert = scriptedUpsert;
			return new UpdateOption(this);
		}

		public UpdateOption docAsUpsert(boolean docAsUpsert) {
			this.docAsUpsert = docAsUpsert;
			return new UpdateOption(this);
		}

		public UpdateOption build() {
			return new UpdateOption(this);
		}
	}
}
