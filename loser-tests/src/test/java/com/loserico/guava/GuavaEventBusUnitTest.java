package com.loserico.guava;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

/**
 * http://www.baeldung.com/guava-eventbus
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-07-19 09:32
 * @version 1.0
 *
 */
public class GuavaEventBusUnitTest {

    private EventListener listener;
    private EventBus eventBus;

    @Before
    public void setUp() {
        eventBus = new EventBus();
        listener = new EventListener();

        eventBus.register(listener);
    }

    @After
    public void tearDown() {
        eventBus.unregister(listener);
    }

    @Test
    public void givenStringEvent_whenEventHandled_thenSuccess() {
        listener.resetEventsHandled();

        eventBus.post("String Event");
        assertEquals(1, listener.getEventsHandled());
    }

    @Test
    public void givenCustomEvent_whenEventHandled_thenSuccess() {
        listener.resetEventsHandled();

        CustomEvent customEvent = new CustomEvent("Custom Event");
        eventBus.post(customEvent);

        assertEquals(1, listener.getEventsHandled());
    }

    @Test
    public void givenUnSubscribedEvent_whenEventHandledByDeadEvent_thenSuccess() {
        listener.resetEventsHandled();

        eventBus.post(12345);
        assertEquals(1, listener.getEventsHandled());
    }

}