package com.awad.tmdb;

import com.awad.tmdb.constant.AppPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		AppPropertiesConfig.class
})
public class TmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmdbApplication.class, args);
	}

}
