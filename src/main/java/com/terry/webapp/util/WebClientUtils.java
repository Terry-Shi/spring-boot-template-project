package com.terry.webapp.util;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.retry.Backoff;
import reactor.retry.Retry;

@Service
class WebClientUtils {

    private final WebClient webClient;

	public static void main(String arg[]) {
		WebClient.Builder webClientBuilder = WebClient.builder();
		WebClientUtils webClientUtils = new WebClientUtils(webClientBuilder);
		//webClientUtils.testWithCookie();
		//webClientUtils.testPostJson();
		//webClientUtils.testPostJsonTimeout();
		//webClientUtils.testPostJsonRetry();
		webClientUtils.testFormParam();
	}
	
    public WebClientUtils(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
	
	public void testWithCookie(){
        Mono<String> resp = this.webClient
                .method(HttpMethod.GET)
                .uri("https://httpbin.org/cookies")
                .cookie("token","xxxx")
                .cookie("JSESSIONID","XXXX")
                .retrieve()
                .bodyToMono(String.class);
        System.out.println("result:{}" + resp.block());
    }
	
	public void testPostJson(){
        Book book = new Book();
        book.setName("name");
        book.setTitle("this is title");
        Mono<String> resp = webClient.post()
                .uri("https://httpbin.org/anything")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book), Book.class)
                .retrieve()
                .bodyToMono(String.class);
        System.out.println("result:{}" + resp.block());
    }
	
	/**
	 * Timeout
	 * @throw java.util.concurrent.TimeoutException
	 */
	public void testPostJsonTimeout(){
        Book book = new Book();
        book.setName("name");
        book.setTitle("this is title");
        Mono<String> resp = webClient.post()
                .uri("https://httpbin.org/delay/5")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book), Book.class)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3)); // <-- 本质上是处理response的环节发生timeout;
        System.out.println("result:{}" + resp.block());
    }
	
	public void testPostJsonRetry() {
        Book book = new Book();
        book.setName("name");
        book.setTitle("this is title");
        Retry<?> retry = Retry.any() //
                .retryMax(2) //
                .backoff(Backoff.fixed(Duration.ofMillis(500)));
        
        Mono<String> resp = webClient.post()
                .uri("http://localhost/delay/6")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book), Book.class)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(retry)  // <-- retry;
                ;
//        resp.log().subscribe();
//        try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

        System.out.println("result:{}" + resp.block());
    }
	
	public void testFormParam(){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name1","value1");
        formData.add("name2","value2");
        Mono<String> resp = WebClient.create().post()
                .uri("http://localhost/anything")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve().bodyToMono(String.class);
        System.out.println("result:" + resp.block());
    }
	
//	public <T> void post(T input) {
//		Mono<String> resp = webClient.post()
//                .uri("https://httpbin.org/anything")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .body(Mono.just(input), input.getClass())
//                .retrieve().bodyToMono(String.class);
//        System.out.println("result:{}" + resp.block());
//	}
}

class Book {
    String name;
    String title;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
