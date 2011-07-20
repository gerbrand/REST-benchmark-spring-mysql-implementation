package com.xebia.rest.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver;

@Configuration
public class RestServiceContext {
     public @Bean HandlerAdapter messageAdapter() {
        AnnotationMethodHandlerAdapter handler=new AnnotationMethodHandlerAdapter();
        //Going to support JSON messages
        HttpMessageConverter<?>[] converters={new MappingJacksonHttpMessageConverter()};
        handler.setMessageConverters(converters);
        return handler;
    }
    
    public @Bean HandlerExceptionResolver exceptionMessageAdapter() {
        AnnotationMethodHandlerExceptionResolver handler=new AnnotationMethodHandlerExceptionResolver();
        HttpMessageConverter<?>[] converters={new MappingJacksonHttpMessageConverter()};
        handler.setMessageConverters(converters);
        return handler;
    }
}
