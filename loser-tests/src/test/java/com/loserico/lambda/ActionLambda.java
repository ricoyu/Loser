package com.loserico.lambda;

public class ActionLambda {

	@FunctionalInterface
	interface Action {
		void run(String s);
	}

	public void action(Action action) {
		action.run("Hello!");
	}

	public static void main(String[] args) {
		new ActionLambda().action((String s) -> System.out.print("*" + s + "*"));
	}

}
