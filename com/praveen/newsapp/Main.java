package com.praveen.newsapp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class Main
{
	public static final String NEW_INDIAN_XPRESS = "NewIndianXpress";
	public static final String THE_HINDU = "the_hindu";

	public static void main(String[] args) {

		if(args.length < 1)
		{
			System.out.println("Number of hours required");
			System.exit(-1);
		}
		String hours = args[0];
		long currentTime = System.currentTimeMillis();
		long stopTime = currentTime - (Integer.valueOf(hours) * 3600000);

		BlockingQueue<News> blockingQueue = new ArrayBlockingQueue<>(20);

		TwitterReader indianExpressObject = new TwitterReader(blockingQueue, NEW_INDIAN_XPRESS, stopTime);
		TwitterReader hinduObject = new TwitterReader(blockingQueue, THE_HINDU, stopTime);

		NewsFileWriter newsFileWriter = new NewsFileWriter(blockingQueue);

		Thread indianExpressThread = new Thread(indianExpressObject);
		Thread hinduThread = new Thread(hinduObject);
		Thread writerThread = new Thread(newsFileWriter);

		long l = System.currentTimeMillis();
		indianExpressThread.start();
		hinduThread.start();
		writerThread.start();
		try
		{
			indianExpressThread.join();
			hinduThread.join();
			blockingQueue.put(new News(null,null, null, null));
			writerThread.join();
			System.out.println("Total time " + (System.currentTimeMillis() - l));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
