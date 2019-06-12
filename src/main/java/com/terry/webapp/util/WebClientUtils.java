package com.terry.webapp.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
class WebClientUtils {

    private final WebClient webClient;

	public static void main(String arg[]) {
		WebClient.Builder webClientBuilder = WebClient.builder();
		WebClientUtils webClientUtils = new WebClientUtils(webClientBuilder);
		//webClientUtils.testWithCookie();
		webClientUtils.testPostJson();
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
                .retrieve().bodyToMono(String.class);
        System.out.println("result:{}" + resp.block());
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
