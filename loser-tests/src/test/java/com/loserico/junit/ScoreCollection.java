package com.loserico.junit;

import java.util.ArrayList;
import java.util.List;

public class ScoreCollection {
	private List<Scoreable> scores = new ArrayList<>();

	public void add(Scoreable scoreable) {
		if (scoreable == null)
			throw new IllegalArgumentException();
		scores.add(scoreable);
	}

	public int arithmeticMean() {
		if (scores.size() == 0)
			return 0;
		// ...

		int total = scores.stream().mapToInt(Scoreable::getScore).sum();
		return total / scores.size();
	}
}
