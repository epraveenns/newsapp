package com.praveen.newsapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import twitter4j.URLEntity;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Spider
{
	public static String[] crawl(String sourceName, URLEntity urlEntity)
	{
		if(Main.NEW_INDIAN_XPRESS.equals(sourceName))
		{
			return extractNewIndianXpress(urlEntity.getURL());
		}
		else if(Main.THE_HINDU.equals(sourceName))
		{
			return extractTheHindu(urlEntity.getURL());
		}
		return new String[]{};
	}

	private static String[] extractTheHindu(String url)
	{
		try
		{
			Document doc = getWebContent(url);

			String title = doc.getElementsByClass("article").get(0).getElementsByClass("title").text();
			title = title.isEmpty()? doc.getElementsByClass("article").get(0).getElementsByClass("special-article-heading").text() : title;
			String text = doc.getElementsByClass("article").get(0).select("[id^=\"content-body\"]").get(0).text();

			return new String[]{title, text};
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new String[]{};
		}
	}

	private static String[] extractNewIndianXpress(String url)
	{
		try
		{
			Document doc = getWebContent(url);

			String title = doc.getElementById("content_head").getAllElements().select("h1").text();
			String storyContent = doc.getElementById("storyContent").getAllElements().text();

			return new String[]{title, storyContent};
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new String[]{};
		}

	}


	public static Document getWebContent(String link) throws Exception
	{
		HttpURLConnection conn = null;
		try
		{

			URL url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");

			boolean redirect = false;

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
					|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
				System.out.println("Original URL : "+ url);
			}

			if (redirect) {
				link = conn.getHeaderField("Location");
				System.out.println("Redirect to URL : " + link);
			}

			return Jsoup.connect(link).get();

		}
		catch (MalformedURLException e)
		{
			System.out.println("Exception for url " + link);
			throw new Exception(e);
		}
		finally
		{
			if(conn != null)
			{
				conn.disconnect();
			}
		}

	}
}
