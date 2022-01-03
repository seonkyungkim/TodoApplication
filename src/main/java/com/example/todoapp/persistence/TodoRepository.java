package com.example.todoapp.persistence;

import com.example.todoapp.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, String> { //<엔티티 클래스, 그 기본키의 타입>

    //기본적으로 제공되는 것 말고 새로운 쿼리를 만듦.
    //스프링데이터 JPA가 메서드 이름을 파싱해서 select * from TodoRepository WHERE userId = '{userId}'와 같은 쿼리를 작성해 실행함
    //List<TodoEntity> findByUserId(String userId);

    //더 복잡한 쿼리는 @Query를 사용해 만듦.
    @Query("select * from Todo t where t.userId = ?1")
    List<TodoEntity> findByUserId(String userId);

}
