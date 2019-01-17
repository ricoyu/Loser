package com.loserico.message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;

public class EmbeddedBrokerTest {

	public static void main(String[] args) throws Exception {
		BrokerService broker = createEmbeddedBroker();
		broker.start();
	}

	private static BrokerService createEmbeddedBroker() throws IOException, Exception {
		BrokerService broker = new BrokerService();
		File dataDir = Paths.get("target/activemq-in-action/kahadb").toFile();
		KahaDBStore kahaDBStore = new KahaDBStore();
		kahaDBStore.setDirectory(dataDir);

		//Using a big journal file
		kahaDBStore.setJournalMaxFileLength(1024 * 100);
		//small batch means more frequent and smaller writes
		kahaDBStore.setIndexWriteBatchSize(100);
		//do the index write in a separate thread
		kahaDBStore.setEnableIndexWriteAsync(true);
		
		broker.setPersistenceAdapter(kahaDBStore);
		//create a transport connector
		broker.addConnector("tcp://localhost:61616");
		return broker;
	}
}
