package br.pucminas.lumen_coin_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LumenCoinApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LumenCoinApiApplication.class, args);
	}
}
