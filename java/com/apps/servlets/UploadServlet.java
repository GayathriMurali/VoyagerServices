package com.apps.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.apps.services.SearchByURL;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;

public class UploadServlet extends HttpServlet {
	private String filePath;
	private File file;
	private String bestGuessString;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		bestGuessString = "";

		filePath = getServletContext().getInitParameter("file-upload");
		System.out.println("filePath: " + filePath);
		filePath = getServletContext().getRealPath(filePath);
		System.out.println("filePath2: " + filePath);

		String fileName = "";
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				OutputStream outputStream = null;

				if (item.isFormField()) {
					System.out.println("Got a form field: "
							+ item.getFieldName());
				} else {
					System.out.println("Got an uploaded file: "
							+ item.getFieldName() + ", name = "
							+ item.getName());
					fileName = item.getName();
					System.out.println("Absolute path: " + filePath + "/"
							+ fileName);

					outputStream = new FileOutputStream(new File(fileName));
					int len;
					byte[] buffer = new byte[8192];
					while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
						response.getOutputStream().write(buffer, 0, len);
						outputStream.write(buffer, 0, len);
					}
				}
			}
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		//String finalGuess = SearchByURL
		//		.reverseImageSearch("http://lh6.ggpht.com/SXpx8YonA5CgZrRPOswP3N7KE_s3e3TiJ0d2fEZHiqrwVpsKN0HUHzxApJ1clBQnagLvR5eCBbwHzhvUZyMx7SUkdgU");
		String finalGuess = SearchByURL.reverseImageSearch(fileName);
		if (finalGuess.equals(""))
			bestGuessString = "Sorry! Google did not find the image interesting enough.. :)";
		else
			bestGuessString = finalGuess;

		response.sendRedirect("/UploadServlet?imageUrl=" + bestGuessString);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		String imageUrl = request.getParameter("imageUrl");
		response.setHeader("Content-Type", "text/html");
		System.out.println("in doGet(), imageUrl: " + imageUrl);
		response.getWriter().println("Best Guess: " + bestGuessString);
	}

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getInitParameter("file-upload");
	}
}