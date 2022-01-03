package com.example.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  //해당 클래스가 스프링부트를 설정하는 클래스임을 의미. 또한 이 어노테이션이 달린 클래스가 있는 패키지를 베이스로 간주한다.
                        //이 어노테이션이 @ComponentScan을 포함하고 있다.
public class TodoappApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoappApplication.class, args);
    }

}
