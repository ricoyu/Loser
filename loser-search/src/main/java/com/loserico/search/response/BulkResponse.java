package com.loserico.search.response;

import java.util.ArrayList;
import java.util.List;

import com.loserico.search.enums.OPType;

public class BulkResponse implements Response{

	private int created;
	
	private int updated;
	
	private int deleted;
	
	private List<Response> responses = new ArrayList<>();
	
	public int createdPlusPlus() {
		return this.created++;
	}
	
	public int updatedPlusPlus() {
		return this.created++;
	}
	
	public int deletedPlusPlus() {
		return this.created++;
	}
	
	public void add(Response response) {
		responses.add(response);
	}

	@Override
	public OPType operateType() {
		return OPType.BULK;
	}

	public int getCreated() {
		return created;
	}

	public int getUpdated() {
		return updated;
	}

	public int getDeleted() {
		return deleted;
	}

	public List<Response> getResponses() {
		return responses;
	}
	
}
