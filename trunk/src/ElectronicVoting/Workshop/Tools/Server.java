package ElectronicVoting.Workshop.Tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

public class Server {
	URLConnection m_urlConn;
	URL f_uurl;

	/**
	 * set the URL of the server
	 * 
	 * @param i_sIP
	 *            the IP/Url to cinnect to
	 * @throws MalformedURLException 
	 * 
	 */
	public void setUrl(String i_sUrl) throws MalformedURLException {
		
			f_uurl = new URL(i_sUrl);

	}

	/**
	 * Connect to the server and dwonlod files
	 * 
	 * @param i_sFileName
	 * @return the pathe to the file or the work "Exception" if execprion
	 *         addored
	 */
	int getFile(String i_sFileName,Context I_Contex) throws IOException  {
		int lengthe=0;
		String i_sToFile="";
		
		// creating connectaion and reqowest and sent it to the Server
		this.m_urlConn = f_uurl.openConnection();
		String params = "Filename=" + i_sFileName;
		m_urlConn = f_uurl.openConnection();
		m_urlConn.setDoOutput(true);
		OutputStreamWriter ow_writer = new OutputStreamWriter(m_urlConn.getOutputStream());
		ow_writer.write("Filename=build.properties");
		ow_writer.flush();
		StringBuffer response = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(m_urlConn.getInputStream()));

		//  Write a file to the disk
		FileOutputStream f_fsOut = I_Contex.openFileOutput(i_sFileName,I_Contex.MODE_WORLD_READABLE);
		OutputStreamWriter f_osSw = new OutputStreamWriter(f_fsOut);
		String line;
	//  Prieprting the String to writ to the disk
		while ((line = reader.readLine()) != null) {
			i_sToFile+=line;
			lengthe+=line.length();
		}
		reader.close();
		f_osSw.write(i_sToFile);
		f_osSw.flush();
		f_osSw.close();
		f_fsOut.close();	
		return lengthe;
	}
}
