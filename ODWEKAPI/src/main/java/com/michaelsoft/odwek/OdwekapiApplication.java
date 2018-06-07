package com.michaelsoft.odwek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/*
 * Modified to allow war creation
 */
@SpringBootApplication
public class OdwekapiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OdwekapiApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(OdwekapiApplication.class, args);
    }

}

