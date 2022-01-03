package com.example.todoapp.controller;

import com.example.todoapp.dto.ResponseDTO;
import com.example.todoapp.dto.TodoDTO;
import com.example.todoapp.model.TodoEntity;
import com.example.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
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
        String err = "err";

        //ResponseDTO를 제너릭으로 작성했으므로 <String>형으로 쓰겠다고 알림
        //<String>형으로 builder를 사용하고 -> error값에 문자열 "err"를, data값에 앞서 담아둔 문자열 리스트 list를 넣은 뒤 -> 빌드
        ResponseDTO<String> response = ResponseDTO.<String>builder().error(err).data(list).build();

        // { "error" : "err", "data" : ["Test Service"] }가 출력된다.
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto){
        try {
            String temporaryUserId = "temporary-user"; //temporary user id.

            //(1) TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);
            
            //(2) id null로 초기화. 생성 당시에는 id가 없어야 하기 때문에
            entity.setId(null);

            //(3) 임시사용자 아이디 설정
            entity.setUserId(temporaryUserId);

            //(4) 서비스를 이용해 Todo엔티티를 생성
            List<TodoEntity> entities = service.create(entity);

            //(5) 자바스트림을 이용해 리턴된 엔티티리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //(6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //(7) ResponseDTO 리턴
            return ResponseEntity.ok().body(response);

        } catch (Exception e){
            //(8) 혹시 예외가 있는 경우 dto 대신 error 메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}
