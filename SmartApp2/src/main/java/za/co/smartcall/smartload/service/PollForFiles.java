package za.co.smartcall.smartload.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.MainApp;
/**
 * Setup the polling for any outstanding requests for files
 * @author rudig
 *
 */
@Log4j
public class PollForFiles {

	private ScheduledExecutorService scheduledExecutorService;
	
	private ScheduledFuture scheduledFuture;
	
	private Poll poll;
	
	public void schedule(MainApp mainApp){
		poll = new Poll(mainApp);
		scheduledFuture =
		    scheduledExecutorService.scheduleWithFixedDelay(poll,
		    20L,
		    60L,
		    TimeUnit.SECONDS);
	}

	public PollForFiles() {
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);

	}
	
	public void stop(){
		log.info("Shutting down executor poll for files");
		poll.stop();
		scheduledFuture.cancel(true);
		scheduledExecutorService.shutdown();
	}
	
}
