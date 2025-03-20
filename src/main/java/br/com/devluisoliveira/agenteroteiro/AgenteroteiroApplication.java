package br.com.devluisoliveira.agenteroteiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.devluisoliveira.agenteroteiro")
public class AgenteroteiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgenteroteiroApplication.class, args);
	}

}
