package com.loserico.concurrent.semaphore;

/**
 * If you use a semaphore to send signals between threads, then you would typically have one thread call the acquire() method, 
 * and the other thread to call the release() method.
 * If no permits are available, the acquire() call will block until a permit is released by another thread. Similarly, 
 * a release() calls is blocked if no more permits can be released into this semaphore.
 * Thus it is possible to coordinate threads. For instance, if acquire was called after Thread 1 had inserted an object in a shared list, 
 * and Thread 2 had called release() just before taking an object from that list, 
 * you had essentially created a blocking queue. The number of permits available in the semaphore would correspond to the maximum number of 
 * elements the blocking queue could hold.
 * 
 * A Semaphore is a thread synchronization construct that can be used either to send signals between threads to avoid missed signals, 
 * or to guard a critical section like you would with a lock. 
 * Java 5 comes with semaphore implementations in the java.util.concurrent package so you don't have to implement your own semaphores. 
 * Still, it can be useful to know the theory behind their implementation and use.
 * @author Rico Yu
 * @since 2016-11-08 14:30
 * @version 1.0
 *
 */
public class SemaphoreSendingSignalsBetweenThreadsTest {

}
