package com.loserico.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * The biggest difference between a non-blocking and a blocking IO pipeline is
 * how data is read from the underlying Channel (socket or file).
 * 
 * IO pipelines typically read data from some stream (from a socket or file) and
 * split that data into coherent messages. This is similar to breaking a stream
 * of data into tokens for parsing using a tokenizer. Instead, you break the
 * stream of data into bigger messages. I will call the component breaking the
 * stream into messages for a Message Reader. Here is an illustration of a
 * Message Reader breaking a stream into messages:
 * 
 * A blocking IO pipeline can use an InputStream-like interface where one byte
 * at a time can be read from the underlying Channel, and where the
 * InputStream-like interface blocks until there is data ready to read. This
 * results in a blocking Message Reader implementation.
 * 
 * Created by jjenkov on 16-10-2015.
 */
public interface MessageReader {

	public void init(MessageBuffer readMessageBuffer);

	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

	public List<Message> getMessages();

}
