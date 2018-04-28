package cn.lanyj.am.orm.domin.jsonhelper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cn.lanyj.am.orm.domin.File;

public class JsonFileSerialize extends JsonSerializer<File> {

	@Override
	public void serialize(File value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(value == null) {
			gen.writeNull();
		} else {
			gen.writeStringField("uuid", value.getUUID());
			gen.writeStringField("filename", value.getName());
			gen.writeStringField("parent", value.getParent().getUUID());
			gen.writeObjectField("uploadeTime", value.getUploadTime());
		}
	}
	
}
