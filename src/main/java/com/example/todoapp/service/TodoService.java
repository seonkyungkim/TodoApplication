package com.example.todoapp.service;

import com.example.todoapp.model.TodoEntity;
import com.example.todoapp.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j      //로그 어노테이션
@Service    //스테레오타입 어노테이션. 단지 기능적으로 비지니스 로직을 수행하는 서비스레이어임을 나타낸다.
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService(){
        //TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        //TodoEntity 저장
        repository.save(entity);
        //TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();

        return savedEntity.getTitle();
    }

    /*
        CREATE 메서드
     */
    public List<TodoEntity> create(final TodoEntity entity){

        //Validations: 검증부분은 다른 메서드에서도 계속 쓰일 예정이므로 private method로 리팩토링한다.
        validate(entity);

        repository.save(entity);

        log.info("Entity Id: {} is saved.", entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    /*
        검증 메서드 리팩토링
     */
    private void validate(final TodoEntity entity){
        //엔티티가 null인 경우
        if(entity == null){
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        //사용자 Id가 null인 경우
        if(entity.getUserId() == null){
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }
}
