package com.praveen.newsapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class NewsFileWriter implements Runnable
{
	private final BlockingQueue<News> queue;

	public NewsFileWriter(BlockingQueue<News> queue)
	{
		this.queue = queue;
	}

	private static void writeToFileSystem(News news) throws IOException
	{
		String directoryPath = news.getDirectoryPath();
		String story = news.getStory();
		String fileName = news.getFileName();
		String title = news.getTitle();
		System.out.println("Starting to write "+title);
		File file = new File(directoryPath);
		if(!file.exists())
		{
			file.mkdirs();
		}
		file = new File(directoryPath,fileName);
		if(file.exists())
		{
			System.out.println("File already exists.Skip writing");
			return;
		}
		BufferedWriter bw = null;
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(title + "\n\n" + story);
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

	@Override public void run()
	{
		System.out.println("Enterting NewsFileWriter");
		while (true)
		{
			try
			{
				News news = queue.take();
				if(news.getTitle() == null && news.getDirectoryPath() == null && news.getFileName() == null && news.getStory() == null)
				{
					break;
				}
				writeToFileSystem(news);
			}
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
