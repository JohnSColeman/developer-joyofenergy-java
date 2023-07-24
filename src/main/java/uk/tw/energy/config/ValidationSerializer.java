package uk.tw.energy.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.io.IOException;

public class ValidationSerializer extends JsonSerializer<Validation> {

    @Override
    public void serialize(Validation value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.isValid()) {
            gen.writeObject(value.get());
        } else {
            gen.writeStartObject();
            writeError(gen, value.getError());
            gen.writeEndObject();
        }
    }

    private <E> void writeError(JsonGenerator gen, E error) throws IOException {
        if (error instanceof Seq) {
            gen.writeArrayFieldStart("errors");
            for (String message : ((Seq<?>) error).map(Object::toString)) {
                gen.writeString(message);
            }
            gen.writeEndArray();
        }
    }
}
