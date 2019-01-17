package com.loserico.methodref;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CarMethodReference {

	public static class Car {

		public static Car create(Supplier<Car> supplier) {
			return supplier.get();
		}

		public static void collide(final Car car) {
			System.out.println("Collided " + car.toString());
		}

		public void follow(Car another) {
			System.out.println("Following the " + another.toString());
		}

		public void repair() {
			System.out.println("Repaired " + this.toString());
		}
	}

	public static void main(String[] args) {
		Car car = Car.create(Car::new);
		List<Car> cars = Arrays.asList(car);

		cars.forEach(Car::collide);
		cars.forEach(Car::repair);
		cars.forEach(car::follow);
	}

}