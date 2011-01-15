package ElectronicVoting.Workshop.Tools;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.os.Environment;

public class Server {
	URLConnection m_urlConn;


	/**
	 * set the commaction to the server
	 * @param i_sIP the IP/Url to cinnect to
	 * @throws Exception - network ecception 
	 */
	public void setUrl(String i_sUrl) throws Exception{
		URL f_uurl = new URL(i_sUrl);
		this.m_urlConn=f_uurl.openConnection();
	}
	
	String getFile(String i_sFileName){
		m_urlConn.addRequestProperty("Filename", i_sFileName);
		 File m_fFile = new File(Environment.getExternalStorageDirectory(),i_sFileName);
		 try {
			DataInputStream dis = new DataInputStream(m_urlConn.getInputStream());	
			 FileWriter fstream = new FileWriter(m_fFile);
		        BufferedWriter out = new BufferedWriter(fstream);
			 
		
		 String s;
		while ((s = dis.readLine()) != null)
	    { 
			out.write(s); 
	    } 
	      dis.close(); 
	      out.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		return null;
	} 

		return m_fFile.getAbsolutePath();
	}
}
