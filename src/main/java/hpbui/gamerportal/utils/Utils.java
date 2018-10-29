package hpbui.gamerportal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    private final static String USER_AGENT = "Mozilla/5.0";
	public static String callGetApi(String url) {
		URL obj;
        StringBuffer response = new StringBuffer();
		try {
			obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	        //Request header
	        con.setRequestProperty("User-Agent", USER_AGENT);

	        int responseCode = con.getResponseCode();
	        System.out.println("\nSending 'GET' request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);

	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(con.getInputStream()));
	        String inputLine;

	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();
	}
}
