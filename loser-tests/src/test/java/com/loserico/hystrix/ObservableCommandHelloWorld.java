package com.loserico.hystrix;

import org.junit.Test;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ObservableCommandHelloWorld extends HystrixObservableCommand<String> {

	private final String name;

	public ObservableCommandHelloWorld(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.name = name;
	}

	@Override
	protected Observable<String> construct() {
		return Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> observer) {
				try {
					if (!observer.isUnsubscribed()) {
						// a real example would do work like a network call here
						observer.onNext("Hello");
						observer.onNext(name + "!");
						observer.onCompleted();
					}
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		}).subscribeOn(Schedulers.io());
	}

	public static void main(String[] args) {
		ObservableCommandHelloWorld observableCommandHelloWorld = new ObservableCommandHelloWorld("三少爷");
		/*
		 * observe() — returns a “hot” Observable that executes the command immediately,
		 * though because the Observable is filtered through a ReplaySubject you are not
		 * in danger of losing any items that it emits before you have a chance to
		 * subscribe
		 */
		Observable<String> observable = observableCommandHelloWorld.observe();

		/*
		 * toObservable() — returns a “cold” Observable that won’t execute the command
		 * and begin emitting its results until you subscribe to the Observable
		 */
		//observableCommandHelloWorld.toObservable();
		
		observable.subscribe(System.out::println);

	}
}