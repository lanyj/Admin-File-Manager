package cn.lanyj.am.orm.domin.jsonhelper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonTimestampDeserializer extends JsonDeserializer<Timestamp> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Timestamp deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Timestamp timestamp = null;
		try {
			timestamp = new Timestamp(dateFormat.parse(p.getValueAsString()).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
}