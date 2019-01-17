package com.loserico.concurrent.chapter4;

public interface ConfirmingMicroBlogNode {
	void propagateUpdate(Update update, ConfirmingMicroBlogNode backup);

	boolean tryConfirmUpdate(ConfirmingMicroBlogNode other, Update update);

	String getIdent();
}
