package com.example.todoapp.controller;

import com.example.todoapp.dto.ResponseDTO;
import com.example.todoapp.dto.TestRequestBodyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController     //REST API를 구현하므로 이 컨트롤러가 RestController임을 명시. http와 관련된 코드 및 요청,응답 매핑을 스프링이 알아서 해준다.
@RequestMapping("test")     //리소스. URI 경로에 매핑
public class TestController {

    //localhost:8080/test
    @GetMapping             //HTTP 메서드에 매핑. URI경로를 지정해 줄 수도 있다.
    public String testController(){
        return "Hello, test";
    }

    //localhost:8080/test/testGetMapping
    @GetMapping("/testGetMapping")
    public String testControllerWithPath(){
        return "Hello, testGetMapping!";
    }

    //localhost:8080/test/123
    @GetMapping("/{id}")
    public String testControllerWithPAthVariables(@PathVariable(required = false) int id){
        return "Hello, ID " + id;
    }

    //localhost:8080/test/testRequestParam?id=123
    @GetMapping("/testRequestParam")
    public String testControllerRequestParam(@RequestParam(required = false) int id){
        return "Hello, ID " + id;
    }

    //localhost:8080/test/testRequestBody
    @GetMapping("/testRequestBody")
    public String testControllerRequestBody(@RequestBody TestRequestBodyDTO testRequestBodyDTO){
        return "Hello, ID " + testRequestBodyDTO.getId() + ". Message: " + testRequestBodyDTO.getMessage();
    }

    //ResponseDTO(error와 리스트형태의 data로 구성)를 리턴하는 컨트롤러
    //localhost:8080/test/testReponse
    @GetMapping("/testResponseBody")
    public ResponseDTO<String> testControllerResponseBody(){
        List<String> list = new ArrayList<>();
        list.add("Hello World! I'm ResponseDTO");
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return response;    //{"error":null, "data":["Hello World!"]}같은 JSON이 출력된다.
    }

    //localhost:8080/test/testResponseEntity
    @GetMapping("/testResponseEntity")
    public ResponseEntity<?> testControllerResponseEntity(){
        List<String> list = new ArrayList<>();
        list.add("Hello World! I'm ResponseEntity and you just got 400.");
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.badRequest().body(response);  //일반적으로는 ok()메서드를 사용하면 된다.
    }
}
