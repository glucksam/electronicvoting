package tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

public class Server {
	/**
	 * Connect to the server and dwonlod files
	 * 
	 * @param i_sFileName
	 * @return the path to the file or the work "Exception" if exection
	 *         accured
	 */
	static public int getFile(String i_sUrl, String fileName, Context I_Contex)
			throws IOException {
		int lengthe = 0;
		String i_sToFile = "";
		String fallUral = i_sUrl + "/" + fileName;
		URL f_uurl = new URL(fallUral);
		// creating connection and request and sent it to the Server
		URLConnection urlConn = f_uurl.openConnection();
		urlConn = f_uurl.openConnection();
		urlConn.setDoOutput(true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				urlConn.getInputStream()));
		// Write a file to the disk
		FileOutputStream f_fsOut = I_Contex.openFileOutput(fileName,
				Context.MODE_WORLD_READABLE);
		OutputStreamWriter f_osSw = new OutputStreamWriter(f_fsOut);
		String line;
		// Prieprting the String to writ to the disk
		while ((line = reader.readLine()) != null) {
			i_sToFile += line;
			lengthe += line.length();
		}
		reader.close();
		f_osSw.write(i_sToFile);
		f_osSw.flush();
		f_osSw.close();
		f_fsOut.close();
		return lengthe;
	}
}
