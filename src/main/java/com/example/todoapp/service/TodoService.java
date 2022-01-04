package com.example.todoapp.service;

import com.example.todoapp.model.TodoEntity;
import com.example.todoapp.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j      //로그 어노테이션
@Service    //스테레오타입 어노테이션. 단지 기능적으로 비지니스 로직을 수행하는 서비스레이어임을 나타낸다.
public class TodoService {

    @Autowired
    private TodoRepository repository;

    /*
        TEST ============
     */
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
        CREATE ============
     */
    public List<TodoEntity> create(final TodoEntity entity){

        //Validations: 검증부분은 다른 메서드에서도 계속 쓰일 예정이므로 private method로 리팩토링한다.
        validate(entity);

        repository.save(entity);

        log.info("Entity Id: {} is saved.", entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    /*
        VALIDATE ============
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

    /*
        RETRIEVE ============
     */
    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }

    /*
        UPDATE ============
     */
    public List<TodoEntity> update(final TodoEntity entity){
        //(1) 저장할 엔티티가 유효한지 검증
        validate(entity);

        //(2) 넘겨받은 엔티티 아이디를 이용해 TodoEntity를 가져온다.
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo -> {
            //(3) 반환된 TodoEntity가 존재하면 값을 새 엔티티로 덮어쓴다.
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());

            //(4) 데이터베이스에 새 값을 저장한다.
            repository.save(todo);
        });

        // retrieve메서드를 이용해 사용자의 모든 Todo리스트 반환
        return retrieve(entity.getUserId());
    }

    /*
        DELETE ============
     */
    public List<TodoEntity> delete(final TodoEntity entity){
        //(1)저장할 엔티티가 유효한지 검증
        validate(entity);

        try {
            //(2)엔티티 삭제
            repository.delete(entity);

        } catch(Exception e) {
            //(3)예외발생시 id와 exception을 로깅
            log.error("error deleting entity", entity.getId(), e);

            //(4)컨트롤러로 예외를 보낸다. 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고 새 예외 오브젝트를 리턴한다.
            throw new RuntimeException("error deleting entity " + entity.getId());
        }
        //(5)새 리스트를 가져와 리턴
        return retrieve(entity.getUserId());
    }
}
