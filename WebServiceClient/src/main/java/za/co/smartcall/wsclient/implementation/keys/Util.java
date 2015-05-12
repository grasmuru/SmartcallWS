package za.co.smartcall.wsclient.implementation.keys;

public class Util {

	public static String formatMsisdn(String msisdn){
		msisdn = msisdn.replace("+", "");
		if (msisdn.startsWith("0")) msisdn = msisdn.replaceFirst("0", "27");
		return msisdn;
	}
	
}
