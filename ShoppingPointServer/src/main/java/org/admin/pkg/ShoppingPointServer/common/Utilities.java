package org.admin.pkg.ShoppingPointServer.common;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;

import javax.mail.internet.InternetAddress;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class Utilities {
	
	private static String STRING_AT = "@";
	private static String STRING_DOT = ".";
	private static byte LOWER_CASE_A = 'a';
	private static byte LOWER_CASE_Z = 'z';
	private static byte UPPER_CASE_A = 'A';
	private static byte UPPER_CASE_Z = 'Z';

	private static byte CHAR_ZERO = '0';
	private static byte CHAR_NINE = '9';
	private static byte UNDERSCORE = '_';
	private static byte DASH = '-';
	private static byte DOT = '.';
	private static byte AT = '@';
	private static String EMPTY_STRING = "";
	
	private static Random randomNumberGenerator = new Random();
	
	/**
	 * Validates given string as an email address. Return true if validation passes,
	 * otherwise returns false.
	 * Validation includes mandatory characters validation (such as "." and "@") as well 
	 * as domain existance validation. 
	 * 
	 * @param emailAddress
	 * @return
	 */
	public static boolean validateEmailAddress(String emailAddress){
		
		if ((emailAddress == null) || (emailAddress.trim().equals(EMPTY_STRING)))
			return false;
		byte[] address = emailAddress.getBytes();
		int length = address.length;
		byte letter = 0;
			
		for (int i = 0; i < length; i++) {
			letter = address[i];
			if (!
				(((letter >= UPPER_CASE_A) && (letter <= UPPER_CASE_Z))   || 
				((letter >= LOWER_CASE_A) && (letter <= LOWER_CASE_Z)) || 
				((letter >= CHAR_ZERO) && (letter <= CHAR_NINE) && (i != 0) && ((i+1) != length)) || 
				(((letter==UNDERSCORE) || (letter==DASH) || (letter==DOT) || (letter==AT))
					&& (i != 0) && ((i+1) != length))))
				return false;
		}
			
		//Email address must have an . character
		if (emailAddress.indexOf(STRING_DOT) < 0) 
			return false;
		
		//Email address must have an @ character
		if (emailAddress.indexOf(STRING_AT) < 0) 
			return false;
		
		try {
			//try to create valid InternetAddress with java's mail API
			//If it throws an exception then bad address
			new InternetAddress(emailAddress);
				
			// create a web URL from the domain name and try to get a IP address
			String domainName	= emailAddress.substring(emailAddress.indexOf(STRING_AT)+1);
				
			/*try {
				long tmp = Long.parseLong(StringUtil.replace(domain, ".", ""));
				long tmp = Long.parseLong(domainName.replaceAll(STRING_DOT, EMPTY_STRING));
			} catch (Exception e) {
				webURL	= (!domainName.startsWith(STRING_WWW_DOT)) ? STRING_WWW_DOT+domainName : domainName;	
			} */
				
			//get the IP address, if exception thrown the email address is bad
			InetAddress.getByName(domainName).toString();
			return true;
		} catch (Exception e) {
			return false;	
		}  	
	}
	
	/**
	 * Returns current time, obtained from Calendar's instance, as a string.
	 * 
	 * @return
	 */
	public static String currentTimeAsString() {
		
		Calendar calendar = Calendar.getInstance();	
		return timeAsString(new Timestamp(calendar.getTimeInMillis()));
	}
	
	/**
	 * Returns string time, obtained from Calendar's instance, as a string for today.
	 * 
	 * @return
	 */
	public static String startOfDayTimeAsString() {
		Calendar calendar = Calendar.getInstance();
		StringBuffer buffer = new StringBuffer();
		buffer.append(calendar.get(Calendar.YEAR));
		buffer.append("-");
		int month = calendar.get(Calendar.MONTH)+ 1;
		if (month < 10)
			buffer.append("0");
		buffer.append(month);
		buffer.append("-");
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10)
			buffer.append("0"); 
		buffer.append(day);
		buffer.append(" 00:00:00.00");
		return buffer.toString();
	}
	
	/**
	 * Returns current date, obtained from Calendar's instance, as a string.
	 * 
	 * @return
	 */
	public static String currentDateAsString() {
		
		Calendar calendar = Calendar.getInstance();	
		return dateAsString(new Timestamp(calendar.getTimeInMillis()));
	}
	
	/**
	 * Returns non weekend date, obtained from Calendar's instance, as a string.
	 * 
	 * @return
	 */
	public static String nonWeekendDateAsString() {
		
		Calendar calendar = Calendar.getInstance();	
		if ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ||
				(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
		 calendar.add(Calendar.DAY_OF_MONTH, 2);
		}
		return dateAsString(new Timestamp(calendar.getTimeInMillis()));
	}
	
	/**
	 * Returns next Saturday date, obtained from Calendar's instance, as a string.
	 * 
	 * @return
	 */
	public static String nextSaturdayDateAsString() {
		
		Calendar calendar = Calendar.getInstance();	
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		int dayIncrement = Calendar.SATURDAY - day;
		calendar.add(Calendar.DAY_OF_MONTH, dayIncrement);
		return dateAsString(new Timestamp(calendar.getTimeInMillis()));
	}
	
	/**
	 * Returns given date as a string.
	 * 
	 * @return
	 */
	public static String dateAsString(Timestamp timeStamp) {
		
		String timeAsString = timeStamp.toString();
		int i = timeAsString.indexOf(" ");
		timeAsString = (String) timeAsString.subSequence(0, i);
		return timeAsString;
	}  
	
	/**
	 * Returns given time as a string.
	 * 
	 * @return
	 */
	public static String timeAsString(Timestamp timeStamp) {
		String timeAsString = timeStamp.toString();
		int i = timeAsString.lastIndexOf(".", timeAsString.length()-1);
		timeAsString = (String) timeAsString.subSequence(0, i);
		return timeAsString;
	}
	
	/**
	 * Returns given float as a dollar string (with $0.00)
	 * 
	 * @param floatNumber
	 * @return
	 */
	public static String floatAsDollarString (float floatNumber) {
		
		DecimalFormat decimalFormat = new DecimalFormat("$#0.00");
		return decimalFormat.format(floatNumber);
	}
	
	/**
	 * Returns given float as a two digits decimal.
	 * 
	 * @param floatNumber
	 * @return
	 */
	public static float floatAsTwoDecimals (float floatNumber) {
		
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		return new Float(decimalFormat.format(floatNumber)).floatValue();
	}
	
	/**
	 * Opens dialog with a given message.
	 * 
	 * @param message
	 */
	public static void showMessage(String message) {
		
		try {
			MessageDialog.openInformation( 
					Display.getCurrent().getActiveShell(), 
					"Booking",
			message);
		}
		catch (NoClassDefFoundError exception) {
			//this happens when we are in a non UI application
		}
		catch (NullPointerException exception) {
			//this happens when we are in a non UI application
		}
	}
	
	/**
	 * Return a String containing last 4 characters in the parameter String.
	 * 
	 * @param message
	 */
	public static String lastFourCharactersString(String message) {
		if (message.length() <= 4)
			return message;
		else
			return message.substring(message.length()-4, message.length());
		
	}

	/**
	 * Return a random int.
	 * 
	 * @param message
	 */
	public static int getRandomNumber() {
		return randomNumberGenerator.nextInt(100000);
	}
}
