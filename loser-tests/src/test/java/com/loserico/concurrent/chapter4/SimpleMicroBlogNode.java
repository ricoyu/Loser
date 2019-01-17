package com.loserico.concurrent.chapter4;

public interface SimpleMicroBlogNode {
	
	void propagateUpdate(Update update, SimpleMicroBlogNode backup);

	void confirmUpdate(SimpleMicroBlogNode other, Update update);

	String getIdent();
}
