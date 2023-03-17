package sweetlife.android10.gps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sweetlife.android10.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utilities
{
	/**
	 * Converts seconds into friendly, understandable description of time.
	 * 
	 * @param numberOfSeconds
	 * @return
	 */
	public static String GetDescriptiveTimeString(int numberOfSeconds,
			Context context)
	{
		
		String descriptive;
		int hours;
		int minutes;
		int seconds;
		
		int remainingSeconds;
		
		// Special cases
		if (numberOfSeconds == 1)
		{
			return context.getString(R.string.time_onesecond);
		}
		
		if (numberOfSeconds == 30)
		{
			return context.getString(R.string.time_halfminute);
		}
		
		if (numberOfSeconds == 60)
		{
			return context.getString(R.string.time_oneminute);
		}
		
		if (numberOfSeconds == 900)
		{
			return context.getString(R.string.time_quarterhour);
		}
		
		if (numberOfSeconds == 1800)
		{
			return context.getString(R.string.time_halfhour);
		}
		
		if (numberOfSeconds == 3600)
		{
			return context.getString(R.string.time_onehour);
		}
		
		if (numberOfSeconds == 4800)
		{
			return context.getString(R.string.time_oneandhalfhours);
		}
		
		if (numberOfSeconds == 9000)
		{
			return context.getString(R.string.time_twoandhalfhours);
		}
		
		// For all other cases, calculate
		
		hours = numberOfSeconds / 3600;
		remainingSeconds = numberOfSeconds % 3600;
		minutes = remainingSeconds / 60;
		seconds = remainingSeconds % 60;
		
		// Every 5 hours and 2 minutes
		// XYZ-5*2*20*
		
		descriptive = context.getString(R.string.time_hms_format,
				String.valueOf(hours), String.valueOf(minutes),
				String.valueOf(seconds));
		
		return descriptive;
		
	}
	
	/**
	 * Converts given bearing degrees into a rough cardinal direction that's
	 * more understandable to humans.
	 * 
	 * @param bearingDegrees
	 * @return
	 */
	public static String GetBearingDescription(float bearingDegrees,
			Context context)
	{
		
		String direction;
		String cardinal;
		
		if (bearingDegrees > 348.75 || bearingDegrees <= 11.25)
		{
			cardinal = context.getString(R.string.direction_north);
		}
		else if (bearingDegrees > 11.25 && bearingDegrees <= 33.75)
		{
			cardinal = context.getString(R.string.direction_northnortheast);
		}
		else if (bearingDegrees > 33.75 && bearingDegrees <= 56.25)
		{
			cardinal = context.getString(R.string.direction_northeast);
		}
		else if (bearingDegrees > 56.25 && bearingDegrees <= 78.75)
		{
			cardinal = context.getString(R.string.direction_eastnortheast);
		}
		else if (bearingDegrees > 78.75 && bearingDegrees <= 101.25)
		{
			cardinal = context.getString(R.string.direction_east);
		}
		else if (bearingDegrees > 101.25 && bearingDegrees <= 123.75)
		{
			cardinal = context.getString(R.string.direction_eastsoutheast);
		}
		else if (bearingDegrees > 123.75 && bearingDegrees <= 146.26)
		{
			cardinal = context.getString(R.string.direction_southeast);
		}
		else if (bearingDegrees > 146.25 && bearingDegrees <= 168.75)
		{
			cardinal = context.getString(R.string.direction_southsoutheast);
		}
		else if (bearingDegrees > 168.75 && bearingDegrees <= 191.25)
		{
			cardinal = context.getString(R.string.direction_south);
		}
		else if (bearingDegrees > 191.25 && bearingDegrees <= 213.75)
		{
			cardinal = context.getString(R.string.direction_southsouthwest);
		}
		else if (bearingDegrees > 213.75 && bearingDegrees <= 236.25)
		{
			cardinal = context.getString(R.string.direction_southwest);
		}
		else if (bearingDegrees > 236.25 && bearingDegrees <= 258.75)
		{
			cardinal = context.getString(R.string.direction_westsouthwest);
		}
		else if (bearingDegrees > 258.75 && bearingDegrees <= 281.25)
		{
			cardinal = context.getString(R.string.direction_west);
		}
		else if (bearingDegrees > 281.25 && bearingDegrees <= 303.75)
		{
			cardinal = context.getString(R.string.direction_westnorthwest);
		}
		else if (bearingDegrees > 303.75 && bearingDegrees <= 326.25)
		{
			cardinal = context.getString(R.string.direction_northwest);
		}
		else if (bearingDegrees > 326.25 && bearingDegrees <= 348.75)
		{
			cardinal = context.getString(R.string.direction_northnorthwest);
		}
		else
		{
			direction = context.getString(R.string.unknown_direction);
			return direction;
		}
		
		direction = context.getString(R.string.direction_roughly, cardinal);
		return direction;
		
	}
	
	/**
	 * Makes string safe for writing to XML file. Removes lt and gt. Best used
	 * when writing to file.
	 * 
	 * @param desc
	 * @return
	 */
	public static String CleanDescription(String desc)
	{
		desc = desc.replace("<", "");
		desc = desc.replace(">", "");
		desc = desc.replace("&", "&amp;");
		desc = desc.replace("\"", "&quot;");
		
		return desc;
	}
	

	
	/**
	 * Given a Date object, returns an ISO 8601 date time string in UTC.
	 * Example: 2010-03-23T05:17:22Z but not 2010-03-23T05:17:22+04:00
	 * 
	 * @param dateToFormat
	 *            The Date object to format.
	 * @return The ISO 8601 formatted string.
	 */
	public static String GetIsoDateTime(Date dateToFormat)
	{
		// GPX specs say that time given should be in UTC, no local time.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(dateToFormat);
	}
	
	public static String GetReadableDateTime(Date dateToFormat)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		return sdf.format(dateToFormat);
	}
	
	/**
	 * Converts given meters to feet.
	 * 
	 * @param m
	 * @return
	 */
	public static int MetersToFeet(int m)
	{
		return (int) Math.round(m * 3.2808399);
	}
	
	/**
	 * Converts given feet to meters
	 * 
	 * @param f
	 * @return
	 */
	public static int FeetToMeters(int f)
	{
		return (int) Math.round(f / 3.2808399);
	}
	
	/**
	 * Converts given meters to feet and rounds up.
	 * 
	 * @param m
	 * @return
	 */
	public static int MetersToFeet(double m)
	{
		return MetersToFeet((int) m);
	}
	
	public static boolean IsOsmAuthorized(Context ctx)
	{
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String oAuthAccessToken = prefs.getString("osm_accesstoken", "");
		
		return (oAuthAccessToken != null && oAuthAccessToken.length() > 0);
	}
	
	public static Intent GetOsmSettingsIntent(Context ctx)
	{
		Intent intentOsm;
		
		if (!IsOsmAuthorized(ctx))
		{
			intentOsm = new Intent(ctx.getPackageName() + ".OSM_AUTHORIZE");
			intentOsm.setData(Uri.parse("gpslogger://authorize"));
		}
		else
		{
			intentOsm = new Intent(ctx.getPackageName() + ".OSM_SETUP");
			
		}
		
		return intentOsm;
	}

   /**
   * Parses XML Nodes and returns a string. This method exists due to a problem in the Android framework;
   * no transformers!
   * http://stackoverflow.com/questions/2290945/writing-xml-on-android
   * @param root
   * @return
   */
  public static String GetStringFromNode(Node root)  {

      StringBuilder result = new StringBuilder();

      if (root.getNodeType() == Node.TEXT_NODE)
      {
          result.append(root.getNodeValue());
      }
      else
      {
          if (root.getNodeType() != Node.DOCUMENT_NODE)
          {
              StringBuffer attrs = new StringBuffer();
              for (int k = 0; k < root.getAttributes().getLength(); ++k)
              {
                  attrs.append(" ")
                      .append(root.getAttributes().item(k).getNodeName())
                      .append("=\"")
                      .append(root.getAttributes().item(k).getNodeValue())
                      .append("\" ");
              }
              result.append("<")
                  .append(root.getNodeName());

              if(attrs.length() > 0)
              {
                  result.append(" ")
                  .append(attrs);
              }

                  result.append(">");
          }
          else
          {
              result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
          }

          NodeList nodes = root.getChildNodes();
          for (int i = 0, j = nodes.getLength(); i < j; i++)
          {
              Node node = nodes.item(i);
              result.append(GetStringFromNode(node));
          }

          if (root.getNodeType() != Node.DOCUMENT_NODE)
          {
              result.append("</").append(root.getNodeName()).append(">");
          }
      }
      return result.toString();
  }
	
}
