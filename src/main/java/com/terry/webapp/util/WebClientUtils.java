package com.terry.webapp.util;

import java.util.List;
import java.time.Duration;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.terry.webapp.features.auth.db.Role;

import reactor.core.publisher.Flux;
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
		
//		Book book = new Book();
//        book.setName("name");
//        book.setTitle("this is title");
//        String ret = webClientUtils.post(book, Book.class, String.class);
//        System.out.println(ret);
        
		//List<Role> ret = webClientUtils.getRoleList();
		
		webClientUtils.testFilter();
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
	
	/**
	 * 请求Body为对象
	 */
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
	 * response 中是list型数据
	 * @return
	 */
	public List<Role> getRoleList() {
		String baseUrl = "http://localhost:8809";
		WebClient webClient = WebClient.create(baseUrl);
		Flux<Role> userFlux = webClient.get()
				.uri("/api/v1/roles/list")
				.retrieve()
				.bodyToFlux(Role.class);
		userFlux.subscribe(System.out::println);
		List<Role> roles = userFlux.collectList().block();
		return roles;
	}
	
	/**
	 * 封装WebClient API
	 * @param <T>
	 * @param <R>
	 * @param input
	 * @param inputClazz
	 * @param resultClazz
	 * @return
	 */
	public <T,R> R post(T input, Class<T> inputClazz, Class<R> resultClazz) {
		Mono<R> resp = webClient.post()
                .uri("https://httpbin.org/anything")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(input), inputClazz)
                .retrieve().bodyToMono(resultClazz);
        return resp.block();   
	}
	
	public void testFilter() {
		String baseUrl = "https://httpbin.org/anything";
		WebClient webClient = WebClient.builder().baseUrl(baseUrl).filter((request, next) -> {
			ClientRequest newRequest = ClientRequest.from(request).header("header1", "value1").build();
			Mono<ClientResponse> responseMono = next.exchange(newRequest);
			return Mono.fromCallable(() -> {
				ClientResponse response = responseMono.block();
				ClientResponse newResponse = ClientResponse.from(response).header("responseHeader1", "Value1").build();
				return newResponse;
			});
		}).build();
		
		Mono<ClientResponse> resp = webClient.post().exchange();
		System.out.println(resp.block().headers().header("responseHeader1"));
	}
	
	/*
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
