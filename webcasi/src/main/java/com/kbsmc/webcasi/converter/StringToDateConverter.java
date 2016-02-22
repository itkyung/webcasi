package com.kbsmc.webcasi.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class StringToDateConverter implements Converter<String, Date> {
	private DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Date convert(String src) {
		try{
			if(src.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}")){
				//yyyy-MM-dd인 경우
				return dateFormat.parse(src);
			}else if(src.matches("^[0-9]{4}-[0-9]{2}")){
				//yyyy-MM인 경우
				return monthFormat.parse(src);
			}
		}catch(ParseException e){
			e.printStackTrace();
			
		}
		
		return null;
	}

	
	
}
