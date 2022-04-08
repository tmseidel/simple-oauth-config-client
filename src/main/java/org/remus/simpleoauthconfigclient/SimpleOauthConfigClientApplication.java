package org.remus.simpleoauthconfigclient;

import org.jline.terminal.Terminal;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SimpleOauthConfigClientApplication {

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setConnectTimeout(TIMEOUT);
//		requestFactory.setReadTimeout(TIMEOUT);

		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}

	@Bean
	public ShellHelper shellHelper(@Lazy Terminal terminal) {
		return new ShellHelper(terminal);
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleOauthConfigClientApplication.class, args);
	}

}
