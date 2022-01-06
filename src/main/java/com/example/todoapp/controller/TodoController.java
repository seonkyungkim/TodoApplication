package com.example.todoapp.controller;

import com.example.todoapp.dto.ResponseDTO;
import com.example.todoapp.dto.TodoDTO;
import com.example.todoapp.model.TodoEntity;
import com.example.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired  //스프링은 TodoController를 생성할 때 내부에 @Autowired가 있는걸 확인하면, Autowired가 알아서 빈을 찾고 이 인스턴스 멤버변수에 연결한다.
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo(){
        String str = service.testService(); //리턴값 "Test Service"를 str변수에 담음
        List<String> list = new ArrayList<>();  //새로운 어레이리스트 list를 생성
        list.add(str);                      // list에 str변수에 담긴 값을 넣음

        //ResponseDTO를 제너릭으로 작성했으므로 <String>형으로 쓰겠다고 알림
        //<String>형으로 builder를 사용하고 -> error값에 문자열 "err"를, data값에 앞서 담아둔 문자열 리스트 list를 넣은 뒤 -> 빌드
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();

        // { "error" : "err", "data" : ["Test Service"] }가 출력된다.
        return ResponseEntity.ok(response);
    }

    /*
        CREATE (Post메서드)
     */
    @PostMapping
    public ResponseEntity<?> createTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto){
        try {
            //(1) TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);

            //(2) id null로 초기화. 생성 당시에는 id가 없어야 하기 때문에
            entity.setId(null);

            //(3) 임시사용자 아이디 설정
            //  기존의 temporary-user 대신 @AuthenticationPrincipal에서 넘어온 userId로 설정해준다.
            entity.setUserId(userId);

            //(4) 서비스를 이용해 Todo엔티티를 생성
            List<TodoEntity> entities = service.create(entity);

            //(5) 자바스트림을 이용해 리턴된 엔티티리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //(6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //(7) ResponseDTO 리턴
            return ResponseEntity.ok(response);

        } catch (Exception e){
            //(8) 혹시 예외가 있는 경우 dto 대신 error 메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }


    /*
        RETRIEVE (Get메서드)
     */
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId){

        //(1) 서비스메서드의 retrieve()를 사용해 Todo리스트를 가져옴
        List<TodoEntity> entities = service.retrieve(userId);

        //(2) 자바스트림을 이용해 리턴된 엔티티리스트를 TodoDTO 리스트로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        //(3) 변환된 TodoDTO리스트를 이용해 ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //(4) ResponseDTO를 리턴
        return ResponseEntity.ok(response);
    }

    /*
        UPDATE (Put메서드)
     */
    @PutMapping
    public ResponseEntity<?> updateTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto){

        //(1)dto를 entity로 변환
        TodoEntity entity = TodoDTO.toEntity(dto);

        //(2)id를 userId로 초기화.
        entity.setUserId(userId);

        //(3)서비스를 이용해 entity업데이트
        List<TodoEntity> entities = service.update(entity);

        //(4)자바스트림을 이용해 리턴된 엔티티리스트를 TodoDTO리스트로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        //(5)변환된 TodoDTO리스트를 이용해 ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //(6)ResponseDTO를 리턴
        return ResponseEntity.ok(response);
    }

    /*
        DELETE
     */
    @DeleteMapping
    public ResponseEntity<?> deleteTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto){
        try {
            //(1)TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);

            //(2)임시사용자아이디 설정
            entity.setUserId(userId);

            //(3)서비스를 이용해 entity 삭제
            List<TodoEntity> entities = service.delete(entity);

            //(4)자바스트림을 사용해 리턴된 엔티티리스트를 TodoDTO리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //(5)변환된 TodoDTO리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //(6)ResponseDTO 리턴
            return ResponseEntity.ok(response);

        } catch(Exception e) {
            //(7)예외가 있는 경우 dto대신 error메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }

    }
}
