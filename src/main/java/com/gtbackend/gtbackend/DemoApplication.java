package com.gtbackend.gtbackend;

import com.gtbackend.gtbackend.config.SecurityConfig;
import com.gtbackend.gtbackend.runner.MyCommandLineRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//ctrl+Alt+O remove unused import
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }, scanBasePackages={"com.gtbackend.gtbackend"})
@EnableConfigurationProperties(SecurityConfig.class)
@EnableJpaRepositories(basePackages = {"com.gtbackend.gtbackend.dao"})
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(MyCommandLineRunner myCommandLineRunner) {
		return args -> {
			myCommandLineRunner.run(args);
		};
	}
}
