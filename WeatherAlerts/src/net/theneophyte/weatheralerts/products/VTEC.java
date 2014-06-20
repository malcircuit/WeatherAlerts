package net.theneophyte.weatheralerts.products;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for strings containing Valid Time Event Codes.  
 * <p><a href="http://www.nws.noaa.gov/om/vtec/">http://www.nws.noaa.gov/om/vtec/</a></p>
 * @author Matt Sutter
 */
public class VTEC {
	
	private static final String VTEC_REGEX = 
			"([OTEX])"
			+ "\\.(\\D{3})"
			+ "\\.(\\D{4})"
			+ "\\.(\\D{2})"
			+ "\\.([WAYSFON])"
			+ "\\.(\\d{4})"
			+ "\\.(\\d{6}T\\d{4}Z)"
			+ "-(\\d{6}T\\d{4}Z)";
	
	private static final int CLASS = 0;
	private static final int ACTION = 1;
	private static final int OFFICE_ID = 2;
	private static final int PHENOMENA = 3;
	private static final int SIGNIFICANCE = 4;
	private static final int ETN = 5;
	private static final int BEGIN_DATE = 6;
	private static final int END_DATE = 7;

	private static final String VTEC_DATE_FORMAT = "yyMMdd'T'HHmm'Z'";
	
	private final long mBeginDate;
	private final long mEndDate;
	private final char mProductClass;
	private final char mSignificance;
	private final String mAction;
	private final String mPhenomena;
	private final String mOfficeId;
	private final int mEventTrackingNum;
	
	/**
	 * Constructor 
	 * @param vtec - String containing a valid VTEC.
	 */
	public VTEC(String vtec){
		final Pattern vtecPattern = Pattern.compile(VTEC_REGEX);
		final Matcher vtecMatch = vtecPattern.matcher(vtec);
		
		if(vtecMatch.find()){
			mProductClass = vtecMatch.group(CLASS).charAt(0);
			mAction = vtecMatch.group(ACTION);
			mOfficeId = vtecMatch.group(OFFICE_ID);
			mPhenomena = vtecMatch.group(PHENOMENA);
			mSignificance = vtecMatch.group(SIGNIFICANCE).charAt(0);
			mEventTrackingNum = Integer.parseInt(vtecMatch.group(ETN));
			mBeginDate = parseVTECDate(vtecMatch.group(BEGIN_DATE));
			mEndDate = parseVTECDate(vtecMatch.group(END_DATE));
		}
		else {
			throw new IllegalArgumentException("Input String does not contain a valid VTEC identifier.");
		}
	}
	
	/**
	 * Determines whether two VTECs correspond to the same event.
	 * @param event - VTEC
	 * @param anotherEvent - Another VTEC
	 */
	public static boolean sameEvent(VTEC event, VTEC anotherEvent){
		if (event.mProductClass == anotherEvent.mProductClass
			&& event.mOfficeId.equals(anotherEvent.mOfficeId)
			&& event.mPhenomena.equals(anotherEvent.mPhenomena)
			&& event.mSignificance == anotherEvent.mSignificance
			&& event.mEventTrackingNum == anotherEvent.mEventTrackingNum){
				return true;
			}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this VTEC is a followup to an previously published event.
	 * @param initial - Initial VTEC for an event.
	 */
	public boolean isFollorup(VTEC initial){
		return !isNew() && (sameEvent(initial, this) && initial.isNew());
	}
	
	/**
	 * Returns <code>true</code> if this VTEC is the first issuance for this event.
	 * @return
	 */
	public boolean isNew(){
		return mAction.equals("NEW");
	}
	
	public long getBeginDate(){
		return mBeginDate;
	}
	
	public long getEndDate(){
		return mEndDate;
	}
	
	/**
	 * Turns the date/time string in a VTEC into a usable Unix Time date.
	 * @param vtecDate - Date/time stamp from a VTEC.
	 * @return The number of milliseconds from Jan. 1, 1970, midnight GMT to <code>vtecDate</code>.
	 */
	private long parseVTECDate(String vtecDate){
		final SimpleDateFormat sdf = new SimpleDateFormat(VTEC_DATE_FORMAT, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return sdf.parse(vtecDate).getTime();
		} catch (ParseException e) {
			return 0;
		}
	}
}
