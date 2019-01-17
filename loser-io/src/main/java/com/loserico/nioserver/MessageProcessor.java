package com.loserico.nioserver;

/**
 * Created by jjenkov on 16-10-2015.
 */
public interface MessageProcessor {

    public void process(Message message, WriteProxy writeProxy);

}
