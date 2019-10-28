package cn.itrip.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 本类提供了对URL所指向的内容的加载操作
 * @author hduser
 *
 */
public class UrlUtils {

	/**
	 * 获取url网址返回的数据内容
	 * @param urlStr
	 * @return
	 */
	public static String loadURL(String urlStr){
		try{  
	        URL url = new URL(urlStr);  
	        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();	              
	        urlConnection.setRequestMethod("GET");  
		    urlConnection.connect(); 	          
		    InputStream inputStream = urlConnection.getInputStream(); 
		    String responseStr = ConvertToString(inputStream);  
		    return responseStr;
		}catch(IOException e){  
		    e.printStackTrace(); 
		    return null;
		}
	}
	private static String ConvertToString(InputStream inputStream) throws UnsupportedEncodingException {
	    InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
	    StringBuilder result = new StringBuilder();  
	    String line = null;  
	    try {  
	        while((line = bufferedReader.readLine()) != null){  
	            result.append(line + "\n");  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try{  
	            inputStreamReader.close();  
	            inputStream.close();  
	            bufferedReader.close();  
	        }catch(IOException e){  
	            e.printStackTrace();  
	        }  
	    }  
	    return result.toString();  
	}


	public static void main(String[] args) {
		System.out.println(System.getProperties().getProperty("file.encoding"));
	}
}
