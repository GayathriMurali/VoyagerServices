package com.apps.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchByURL {
	private static String DUMMY_URL = "http://www.google.com/imghp?hl=en&tab=wi";
	private static String IMAGESEARCH_URL_URL = "http://www.google.com/searchbyimage?hl=en&image_url=";
	private static String IMAGE_PATH = "http://www.asce.org/uploadedImages/People_and_Projects/Projects/Landmarks/c%20and%20r_Golden%20Gate%20Bridge_flickr_402620871_b023a6a8a9_b.jpg";
	
	public static String reverseImageSearch(String imageURL){
		String finalResult = "";
		String resultURL = "";
		
		int status;

		try {
			// First part
			URL dummyURL = new URL(DUMMY_URL);
			HttpURLConnection dummyConn = (HttpURLConnection) dummyURL.openConnection();
			dummyConn.setReadTimeout(5000);
			dummyConn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			dummyConn.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
			dummyConn.addRequestProperty("Referer", "http://www.google.com/");

			String cookies = dummyConn.getHeaderField("Set-Cookie");
			//System.out.println("cookies: " + cookies);

			System.out.println("Request URL ... " + DUMMY_URL);
			status = dummyConn.getResponseCode();
			//System.out.println("Response Code ... " + status);

			// Second part
			String url2 = IMAGESEARCH_URL_URL + imageURL;
			System.out.println("Normal URL: " + url2);
			
			//url2 = IMAGESEARCH_URL_URL + URLEncoder.encode(imageURL);
			System.out.println("Encoded URL: " + url2);
			
			URL obj2 = new URL(url2);
			HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
			conn2.setReadTimeout(5000);
			conn2.setRequestProperty("Cookie", cookies);
			conn2.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn2.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
			conn2.addRequestProperty("Referer",
					"http://www.google.com/imghp?hl=en&tab=wi");

			//System.out.println("Request URL ... " + url2);
			status = conn2.getResponseCode();
			boolean redirect = false;

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			System.out.println("Response Code ... " + status);

			if (redirect) {
				String newUrl = conn2.getHeaderField("Location");
				System.out.println("newUrl: " + newUrl);

				cookies = conn2.getHeaderField("Set-Cookie");
				System.out.println("cookies: " + cookies);

				conn2 = (HttpURLConnection) new URL(newUrl).openConnection();
				conn2.setRequestProperty("Cookie", cookies);
				conn2.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				conn2.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
				// conn2.addRequestProperty("Referer", "google.com");
				System.out.println("Redirect to URL : " + newUrl);
			}

			System.out.println("Request URL ... " + url2);
			status = conn2.getResponseCode();
			redirect = false;

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			System.out.println("Response Code ... " + status);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn2.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();

			//System.out.println("URL Content... \n" + html.toString());
			//System.out.println("Best Guess: " + findBestGuess(html.toString()));
			finalResult = findBestGuess(html.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalResult;
	}
	
	public static void main(String[] args) {
		/*int status;

		try {
			// First part
			URL dummyURL = new URL(DUMMY_URL);
			HttpURLConnection dummyConn = (HttpURLConnection) dummyURL.openConnection();
			dummyConn.setReadTimeout(5000);
			dummyConn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			dummyConn.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
			dummyConn.addRequestProperty("Referer", "http://www.google.com/");

			String cookies = dummyConn.getHeaderField("Set-Cookie");
			System.out.println("cookies: " + cookies);

			System.out.println("Request URL ... " + DUMMY_URL);
			status = dummyConn.getResponseCode();
			System.out.println("Response Code ... " + status);

			// Second part
			String url2 = IMAGESEARCH_URL_URL
					+ URLEncoder
							.encode(IMAGE_PATH);
			URL obj2 = new URL(url2);
			HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
			conn2.setReadTimeout(5000);
			conn2.setRequestProperty("Cookie", cookies);
			conn2.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn2.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
			conn2.addRequestProperty("Referer",
					"http://www.google.com/imghp?hl=en&tab=wi");

			System.out.println("Request URL ... " + url2);
			status = conn2.getResponseCode();
			boolean redirect = false;

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			System.out.println("Response Code ... " + status);

			if (redirect) {
				String newUrl = conn2.getHeaderField("Location");
				System.out.println("newUrl: " + newUrl);

				cookies = conn2.getHeaderField("Set-Cookie");
				System.out.println("cookies: " + cookies);

				conn2 = (HttpURLConnection) new URL(newUrl).openConnection();
				conn2.setRequestProperty("Cookie", cookies);
				conn2.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				conn2.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0");
				// conn2.addRequestProperty("Referer", "google.com");
				System.out.println("Redirect to URL : " + newUrl);
			}

			System.out.println("Request URL ... " + url2);
			status = conn2.getResponseCode();
			redirect = false;

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			System.out.println("Response Code ... " + status);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn2.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();

			System.out.println("URL Content... \n" + html.toString());
			System.out.println("Best Guess: " + findBestGuess(html.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private static String findBestGuess(String html) {
		String result = "";
		//System.out.println("finding best guess..");
		String pattern = "Best guess for this image:.*<a.*style=\"font-style:italic\">(.+?)</a>";
		Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		while (m.find()) {
			result = m.group(1);
		}

		return result;
	}
}
