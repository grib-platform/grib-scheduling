package kr.co.grib.scheduling.controller;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class AuthController {

	@GetMapping("/callback")
	public String oauthCallback(@RequestParam("code") String code) {
		try{
			WebClient client = WebClient.builder()
								.baseUrl("http://192.168.0.240:9999")
								.defaultHeaders(headers -> headers.setBasicAuth("scheduling", "grib12!@"))
								.build();
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("grant_type", "authorization_code");
			formData.add("code", code);
			formData.add("scope", "user");
			formData.add("redirect_uri", "http://huring.grib-iot.com:9512/oauth/callback");
			String response = client.post()
								.uri("/oauth2/token")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.body(BodyInserters.fromFormData(formData))
								.retrieve()
								.bodyToMono(String.class)
								.block();
			return response;
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("WebClient Error, Check your parameter.", e);
		}
	}
}