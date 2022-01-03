package com.example.todoapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder            //오브젝트 생성을 위한 디자인 패턴 중 하나. Builder클래스를 개발하지 않고도 패턴을 사용해 오브젝트 생성 가능
@NoArgsConstructor  //매개변수가 없는 생성자 구현
@AllArgsConstructor //모든 멤버변수를 매개변수로 받는 생성자 구현
@Data               //클래스 멤버 변수의 Getter/Setter 메서드를 구현
@Entity             //엔티티에 이름을 부여하고 싶다면 ("TodoEntity")처럼 매개변수를 이용한다.
@Table(name="Todo") //테이블 이름을 Todo로 지정해주었다.
public class TodoEntity {
    @Id     //id 필드를 오브젝트를 데이터베이스에 저장할 때마다 생성할 수도 있지만
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;      //이 오브젝트의 아이디
    private String userId;  //이 오브젝트를 생성한 사용자의 아이디
    private String title;   //타이틀(운동하기 등)
    private boolean done;   //완료 여부 체크
}
