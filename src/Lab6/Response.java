package Lab6;

import source.Protagonist;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Response implements Serializable {
    private String message;
    private Set<Protagonist> collection;
    private boolean LoggingResponse;

    private Response(String message, Set<Protagonist> collection) {
        this.message = message;
        if(collection != null) {
            this.collection = Collections.synchronizedSet(collection).stream().sorted().collect(Collectors.toSet());
        }
    }

    static Response createStringResponse(String message){
        return new Response(message, null);
    }

    public static Response createFullResponse(String message, Set<Protagonist> collection){
        return new Response(message, collection);
    }

    static Response createLoggingResponse(String message){
        return Response.createStringResponse(message).withLogging();
    }
    @Override
    public String toString() {
        return message.equals("collection") ? collection.toString() : message;
    }

    Response withLogging(){
        this.setLoggingResponse(true);
        return this;
    }

    public boolean isLoggingResponse() {
        return LoggingResponse;
    }

    public void setLoggingResponse(boolean loggingResponse) {
        LoggingResponse = loggingResponse;
    }
}
