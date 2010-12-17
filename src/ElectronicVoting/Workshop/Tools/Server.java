package ElectronicVoting.Workshop.Tools;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Server {
	URLConnection m_urlConn;
	/**
	 * set the commaction to the server
	 * @param i_sIP the IP/Url to cinnect to
	 * @throws Exception - network ecception 
	 */
	public void setUrl(String i_sIP) throws Exception{
		URL f_uurl = new URL(i_sIP);
		this.m_urlConn=f_uurl.openConnection();
	}
}
