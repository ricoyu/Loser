package com.peacefish.orm.commons.enums;

public enum ActiveStatus {

	DISABLED {

		@Override
		public String desc() {
			return "禁用";
		}

	},
	ENABLED {

		@Override
		public String desc() {
			return "启用";
		}
		
		

	};

	public abstract String desc();
	
}
