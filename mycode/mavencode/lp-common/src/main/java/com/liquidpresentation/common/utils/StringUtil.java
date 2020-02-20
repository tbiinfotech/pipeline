package com.liquidpresentation.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liquidpresentation.common.Constants;

public final class StringUtil {
	
	public static boolean isNotEmpty(String...strings){
		boolean notEmpty = true;
		for (String string : strings) {
			if (string == null || "".equals(string)) {
				notEmpty = false;
			}
		}
		return notEmpty;
	}
	
	public static boolean isAllEmpty(String...strings){
		boolean allEmpty = true;
		for (String string : strings) {
			if (string != null && !"".equals(string)) {
				allEmpty = false;
			}
		}
		return allEmpty;
	}
	
	public static Date toDate(String string) {
		DateFormat format = new SimpleDateFormat(Constants.DATE_FORMATE, Locale.ENGLISH);
		try {
			return format.parse(string);
		} catch (ParseException e) {
			throw new RuntimeException("ParseException for date value [" + string + "]");
		}
	}

    public static boolean isNumber(String strNum) {
        return strNum.matches("^[0-9]+([.]*[0-9]*)?$");
    }

    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("[0-9]+([.]*[0-9]*)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("[A-Za-z]+$");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
}
