package com.praveen.newsapp;

public class News
{
	private String title;
	private String story;
	private String fileName;
	private String directoryPath;

	public News(String title, String story, String fileName, String directoryPath)
	{
		this.title = title;
		this.story = story;
		this.fileName = fileName;
		this.directoryPath = directoryPath;
	}

	public String getTitle()
	{
		return title;
	}

	public String getStory()
	{
		return story;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getDirectoryPath()
	{
		return directoryPath;
	}
}
