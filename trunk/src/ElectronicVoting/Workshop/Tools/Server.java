package ElectronicVoting.Workshop.Tools;

//import java.io.BufferedWriter;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.os.Environment;

public class Server {
	URLConnection m_urlConn;
	URL f_uurl;

	/**
	 * set the URL of the server
	 * 
	 * @param i_sIP
	 *            the IP/Url to cinnect to
	 * 
	 */
	public void setUrl(String i_sUrl) {
		try {
			f_uurl = new URL(i_sUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Connect to the server and dwonlod files
	 * 
	 * @param i_sFileName
	 * @return the pathe to the file or the work "Exception" if execprion
	 *         addored
	 */
	String getFile(String i_sFileName) {
		String filePath;
		try {
			this.m_urlConn = f_uurl.openConnection();

			// Creating the file. If the file is aleady exsist,delet it and
			// create new file
			File m_fFile = new File(Environment.getExternalStorageDirectory(),
					i_sFileName);
			if (m_fFile.exists()) {
				m_fFile.delete();
			}
			m_fFile.createNewFile();
			FileWriter fW_fstream = new FileWriter(m_fFile);
			BufferedWriter bW_out = new BufferedWriter(fW_fstream);

			// creating recrast and sent it to the Server
			String params = "Filename=" + i_sFileName;
			m_urlConn = f_uurl.openConnection();
			m_urlConn.setDoOutput(true);
			OutputStreamWriter ow_writer = new OutputStreamWriter(m_urlConn
					.getOutputStream());
			ow_writer.write("Filename=build.properties");
			ow_writer.flush();
			StringBuffer response = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(m_urlConn.getInputStream()));

			// Wried the file from the SErver and save in the android
			String line;
			while ((line = reader.readLine()) != null) {
				bW_out.write(line);
			}
			fW_fstream.close();
			// bW_out.close();
			reader.close();
			filePath = m_fFile.getAbsolutePath();
		} catch (IOException e) {

			return "Exception";
		}
		return filePath;
	}
}
