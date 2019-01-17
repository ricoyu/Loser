package org.loser.serializer.jackson2.module;

import org.loser.serializer.mixins.SimpleKeyMixin;
import org.springframework.cache.interceptor.SimpleKey;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class CacheModule extends SimpleModule {

	private static final long serialVersionUID = 3446351506005523959L;

	@Override
	public void setupModule(SetupContext context) {
		super.setupModule(context);
		context.setMixInAnnotations(SimpleKey.class, SimpleKeyMixin.class);
	}

}