package com.apps.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


public class SearchByUpload {
	private static String IMAGESEARCH_UPLOAD_URL = "https://www.google.co.in/searchbyimage/upload";
	private static String IMAGE_PATH = "data/lombard_street.png";

	public static String reverseImageSearch(String imageURL){
		String finalResult = "";
		String resultURL = "";
		int status;

		try {
			
			HttpClient client = new DefaultHttpClient();
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			HttpPost post = new HttpPost(IMAGESEARCH_UPLOAD_URL);

			MultipartEntity entity = new MultipartEntity();
			//MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("encoded_image", new FileBody(new File(imageURL)));
			/*entity.addPart("image_url", new StringBody(""));
			entity.addPart("image_content", new StringBody(""));
			entity.addPart("filename", new StringBody(""));
			entity.addPart("h1", new StringBody("en"));
			entity.addPart("bih", new StringBody("179"));
			entity.addPart("biw", new StringBody("1600"));*/

			post.setEntity(entity);
			HttpResponse response = client.execute(post, localContext);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.indexOf("HREF") > 0)
					System.out.println(line.substring(8));
				List resultSet = extractUrls(line.substring(8));
				if (resultSet.size() > 0) {
					resultURL = (String) resultSet.get(0);
				}
			}

			// Second part
			// String url2 =
			// "http://www.google.com/searchbyimage?hl=en&image_url=" +
			// URLEncoder.encode("http://upload.wikimedia.org/wikipedia/commons/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg");
			String url2 = resultURL;
			URL obj2 = new URL(url2);
			HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
			conn2.setReadTimeout(5000);
			// conn2.setRequestProperty("Cookie", cookies);
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

				// get the cookie if need, for login
				// cookies = conn2.getHeaderField("Set-Cookie");

				conn2 = (HttpURLConnection) new URL(newUrl).openConnection();
				// conn2.setRequestProperty("Cookie", cookies);
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
			finalResult = findBestGuess(html.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return finalResult;
	}
	
	public static void main(String[] args) {
		/*String resultURL = "";
		int status;

		try {
			HttpClient client = new DefaultHttpClient();
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			HttpPost post = new HttpPost(IMAGESEARCH_UPLOAD_URL);

			MultipartEntity entity = new MultipartEntity();
			entity.addPart("encoded_image", new FileBody(new File(IMAGE_PATH)));
			entity.addPart("image_url", new StringBody(""));
			entity.addPart("image_content", new StringBody(""));
			entity.addPart("filename", new StringBody(""));
			entity.addPart("h1", new StringBody("en"));
			entity.addPart("bih", new StringBody("179"));
			entity.addPart("biw", new StringBody("1600"));

			post.setEntity(entity);
			HttpResponse response = client.execute(post, localContext);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.indexOf("HREF") > 0)
					System.out.println(line.substring(8));
				List resultSet = extractUrls(line.substring(8));
				if (resultSet.size() > 0) {
					resultURL = (String) resultSet.get(0);
				}
			}

			// Second part
			// String url2 =
			// "http://www.google.com/searchbyimage?hl=en&image_url=" +
			// URLEncoder.encode("http://upload.wikimedia.org/wikipedia/commons/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg");
			String url2 = resultURL;
			URL obj2 = new URL(url2);
			HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
			conn2.setReadTimeout(5000);
			// conn2.setRequestProperty("Cookie", cookies);
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

				// get the cookie if need, for login
				// cookies = conn2.getHeaderField("Set-Cookie");

				conn2 = (HttpURLConnection) new URL(newUrl).openConnection();
				// conn2.setRequestProperty("Cookie", cookies);
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
		}
		*/
	}

	private static String findBestGuess(String html) {
		String result = "";
		System.out.println("finding best guess..");
		String pattern = "Best guess for this image:.*<a.*style=\"font-style:italic\">(.+?)</a>";
		Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		while (m.find()) {
			result = m.group(1);
		}

		return result;
	}

	private static List extractUrls(String value) {
		List result = new ArrayList();
		String urlPattern = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(value);
		while (m.find()) {
			result.add(value.substring(m.start(0), m.end(0)));
		}
		return result;
	}
}
