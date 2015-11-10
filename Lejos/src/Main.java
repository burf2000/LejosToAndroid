

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Main {
	
	private static Brick brick;
	private static Socket s;
	
	private static final int PORT = 5678;
	private static final String IP = "10.0.1.11"; // PAN address, use OS monitor on Android to find it out
	
	public static void main(String[] args) {
		
		brick = BrickFinder.getDefault();
		
		try {
			s = new Socket(IP,PORT);//BC:F5:AC:73:CB:00
			s.setTcpNoDelay(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Sound.systemSound(true, Sound.DESCENDING);
		
		while (true)
		{
			Delay.msDelay(1000);			
			
			if (Button.DOWN.isDown() )
			{
				sendMessage();
				Sound.systemSound(true, Sound.BEEP);
			}
			
			if (Button.ESCAPE.isDown() )
			{
				try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
	}
	
	public static void sendMessage()
	{
		try
		   {
			
//		    OutputStreamWriter osw;
//		    String str = "Hello World";
//		   
//		    osw =new OutputStreamWriter(s.getOutputStream(), "UTF-8");
//		    osw.write(str, 0, str.length());
		    
//			OutputStream outstream = s.getOutputStream(); 
//			PrintWriter out = new PrintWriter(outstream);
//
//			String toSend = "String to send";
//
//			out.print(toSend );
			 //
			
		    String string = "simon";
		    //byte[] b = string.getBytes();
		    byte[] b = string.getBytes(Charset.forName("UTF-8"));
		    
			s.getOutputStream().write(b);
			s.getOutputStream().flush();
			
			 //
			 //int pos = s.getInputStream().read();
			 
		   } catch (IOException e)
		   {
		       // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	}
}
