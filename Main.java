
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public final class Main extends Thread {


	public static final String NEW_INDIAN_XPRESS = "NewIndianXpress";
	public static final String THE_HINDU = "the_hindu";

	private final String handle;
	private final long stopTime;
	private Main(String handle, long stopTime)
	{
		this.handle = handle;
		this.stopTime = stopTime;
	}

	public static void main(String[] args) {

		if(args.length < 1)
		{
			System.out.println("Number of hours required");
			System.exit(-1);
		}
		String hours = args[0];
		long currentTime = System.currentTimeMillis();
		long stopTime = currentTime - (Integer.valueOf(hours) * 3600000);

		Main thread1 = new Main(NEW_INDIAN_XPRESS, stopTime);
		thread1.setName("NewIndianExpress");
		thread1.start();

		Main thread2 = new Main(THE_HINDU, stopTime);
		thread2.setName("TheHindu");
		thread2.start();

		try
		{
			thread1.join();
			thread2.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try {
			System.out.println(currentThread().getName() + " handle" + handle + " stoptime" + stopTime);
			Twitter twitter = TwitterFactory.getSingleton();
			Paging paging = new Paging();
			int page = 1;
			paging.setPage(page);
			paging.setCount(20);
			while (true)
			{
				if(!printTweets(twitter, handle, paging, stopTime))
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
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static boolean printTweets(Twitter twitter, String sourceName, Paging paging, Long stopTime) throws TwitterException, IOException
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
			String text = "";
			if(content.length == 2)
			{
				title = content[0];
				text = content[1];
			}
			if(title.isEmpty())
			{
				title = String.valueOf(status.getId());
			}
			if(text.isEmpty())
			{
				text = status.getText();
			}
			String fileName = title.substring(0,10) + '-' + status.getId() + ".txt";

			File file = new File(directoryPath);
			if(!file.exists())
			{
				file.mkdirs();
			}
			file = new File(directoryPath,fileName);
			if(file.exists())
			{
				System.out.println("File already exists.Skip writing");
				continue;
			}
			BufferedWriter bw = null;
			FileWriter fw = null;
			try
			{
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				bw.write(title + "\n\n" + text);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try {

					if (bw != null)
					{
						bw.close();
					}

					if (fw != null)
					{
						fw.close();
					}

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
		}
		return true;
	}
}
