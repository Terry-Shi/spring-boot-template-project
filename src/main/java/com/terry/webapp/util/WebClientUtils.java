package com.terry.webapp.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class WebClientUtils {

	public static void main(String arg[]) {
		//WebClient client1 = WebClient.create();
		//WebClientUtils.testWithCookie();
		
		WebClientUtils.testPostJson();
	}
	
	public static void testWithCookie(){
        Mono<String> resp = WebClient.create()
                .method(HttpMethod.GET)
                .uri("https://httpbin.org/cookies")
                .cookie("token","xxxx")
                .cookie("JSESSIONID","XXXX")
                .retrieve()
                .bodyToMono(String.class);
        System.out.println("result:{}" + resp.block());
    }
	
	public static void testPostJson(){
        Book book = new Book();
        book.setName("name");
        book.setTitle("this is title");
        Mono<String> resp = WebClient.create().post()
                .uri("https://httpbin.org/anything")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book),Book.class)
                .retrieve().bodyToMono(String.class);
        System.out.println("result:{}" + resp.block());
    }

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
