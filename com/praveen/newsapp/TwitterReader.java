package com.praveen.newsapp;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class TwitterReader implements Runnable
{
	private final BlockingQueue<News> queue;
	private final String handle;
	private final long stopTime;

	TwitterReader(BlockingQueue<News> queue, String handle, long stopTime)
	{
		this.queue = queue;
		this.handle = handle;
		this.stopTime = stopTime;
	}

	@Override public void run()
	{
		try {
			System.out.println("handle :" + handle + " stoptime :" + stopTime);
			Twitter twitter = TwitterFactory.getSingleton();
			Paging paging = new Paging();
			int page = 1;
			paging.setPage(page);
			paging.setCount(20);
			while (true)
			{
				if(!readTheTweets(twitter,queue, handle, paging, stopTime))
				{
					break;
				}
				paging.setPage(++page);
			}

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to process : " + te.getMessage());
			System.exit(-1);
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private static boolean readTheTweets(Twitter twitter, BlockingQueue<News> queue, String sourceName, Paging paging, Long stopTime) throws TwitterException, IOException, InterruptedException
	{
		ResponseList<Status> userTimeline = twitter.getUserTimeline(sourceName, paging);
		for(Status status : userTimeline)
		{
			Date createdAt = status.getCreatedAt();
			if(createdAt.getTime() < stopTime)
			{
				return false;
			}
			if(status.isRetweet())
			{
				continue;
			}
			System.out.println("\n\n*****"+status.getText());
			System.out.println("*****"+status.getId());

			String[] content = new String[]{};
			if(status.getURLEntities().length > 0)
			{
				content = Spider.crawl(sourceName, status.getURLEntities()[0]);
			}

			String directoryPath = sourceName + File.separatorChar + createdAt.getDate();

			String title = "";
			String story = "";
			if(content.length == 2)
			{
				title = content[0];
				story = content[1];
			}
			if(title.isEmpty())
			{
				title = String.valueOf(status.getId());
			}
			if(story.isEmpty())
			{
				story = status.getText();
			}
			String fileName = title.substring(0,10) + '-' + status.getId() + ".txt";
			queue.put(new News(title, story, fileName, directoryPath));
		}
		return true;
	}
}
