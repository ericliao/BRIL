package uk.ac.kcl.cerch.bril.test;

import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.service.queue.ADQueueReaderImpl;
import junit.framework.TestCase;

public class ADQueueReaderImplTest extends TestCase {

	public void testStartService() throws ADException, InterruptedException {
		ADQueueReaderImpl consumer = new ADQueueReaderImpl();

		while (true) {
			System.out
					.println("---------------Message Queue Started-----------");
			consumer.startService();
			Thread.sleep(200000);
			consumer.stopService();
			System.out
					.println("---------------Message Queue Stopped-----------");

		}
	}
}
