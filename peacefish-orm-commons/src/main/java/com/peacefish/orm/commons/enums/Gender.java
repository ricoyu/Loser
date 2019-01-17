package com.peacefish.orm.commons.enums;

public enum Gender {

	FEMALE {

		@Override
		public String desc() {
			return "女";
		}

	},
	MALE {

		@Override
		public String desc() {
			return "男";
		}

	};

	public abstract String desc();
}
