package cn.lanyj.am.orm.domin.jsonhelper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cn.lanyj.am.orm.domin.User;

public class JsonUserSerialize extends JsonSerializer<User> {

	@Override
	public void serialize(User value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(value == null) {
			gen.writeNull();
		} else {
			gen.writeString(value.getUUID());
		}
	}
	
}
