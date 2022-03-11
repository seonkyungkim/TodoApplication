# Web Development 101

React.js, 스프링 부트, AWS

<aside>
💡 Todo Application: basic features → authentication login for users → AWS distribution
   - REST API: springboot, gradle, maven repository, lombok, JPA etc.
   - JWT: authentication 이론과 구현
   - AWS: 일라스틱 빈스톡(EC2, 오토스케일링 그룹, 로드밸런서, RDS etc.)
   - Route 53: DNS 등록 및 로드밸런서 연결

</aside>

---

# TODO List

<aside>
📌 **기능 요약**

- 리스트 생성/수정/출력/삭제, 회원가입, 로그인, 로그아웃

**배포할 애플리케이션의 아키텍처**

![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled.png)

</aside>

# 1. 개발 준비

### 1.1 사용할 기술들

- HTML/CSS/React.js
: 프론트엔드 애플리케이션 개발. 이 애플리케이션에는 프론트엔드 클라이언트를 반환하는 서버가 하나 있으며,  React.js 애플리케이션을 반환하는 역할 하나만을 수행한다. 이런 방식으로 프론트엔드와 백엔드를 분리(decouple)할 수 있다. 분리하는 이유에 대해서는 더 생각해보자.
- 스프링부트
: 백엔드 애플리케이션 개발. 프론트엔드 애플리케이션이 사용할 REST API를 스프링부트로 구현한다. 프로젝트를 확장하여 모바일 앱을 만들게 될 경우 별도의 백엔드 개발 없이 REST API를 사용할 수 있다. REST API를 구현하고 프론트엔드를 분리하면 마이크로서비스 아키텍처로 서비스를 확장하기 쉽다.
- AWS
: 프론트엔드와 백엔드 애플리케이션이 실행될 프로덕션 환경을 구축하는데 사용.

# 2. 백엔드 개발

### 2.1 백엔드 개발 환경

- 2.1.1 어노테이션
    
    
    | @SpringBootApplication | - 해당 클래스가 스프링부트를 설정하는 클래스임을 의미
    - 이 어노테이션이 달린 클래스가 있는 패키지를 베이스 패키지로 간주
    - @ComponentScan을 내부에 포함 |
    | --- | --- |
    | @Autowired | - 스프링이 베이스 패키지와 그 하위 패키지에서 자바Bean을 찾아 스프링의 의존성 주입 컨테이너 프로젝트인 ApplicationContext에 등록
    - 애플리케이션 실행 중 어떤 오브젝트가 필요한 경우 이 어노테이션이 의존하는 다른 오브젝트를 찾아 연결 |
    | @Component | - 스프링에게 이 클래스를 자바Bean으로 등록시키라고 알려줌
    - @Service도 내부에 이 어노테이션을 달고 있으며 @ComponentScan이 특정 클래스에 있어야 컴포넌트를 스캔할 수 있음
    - @SpringBootApplication에 포함되어 있는 어노테이션 |
- 2.1.2 어노테이션 작동 흐름
    1. 스프링부트 애플리케이션 시작
    2. @Component가 있는 경우 베이스 패키지와 그 하위패키지에서 @Component가 달린 클래스 찾기
    3. 필요한 경우 @Component가 달린 클래스의 오브젝트를 생성. 단, 생성하려는 오브젝트가 다른 오브젝트에 의존하는 경우(멤버 변수로 다른 클래스를 가진 경우), 그 멤버 변수 오브젝트를 찾아 넣어주어야 하는데 @Autowired를 사용하면 스프링이 자동으로 찾아 오브젝트를 생성해준다.
        1. @Autowired에 연결된 변수의 클래스가 @Compnent에 달린 클래스인 경우 스프링이 오브젝트를 생성해 넘겨줌
        2. @Bean으로 생성하는 오브젝트인 경우 @Bean이 달린 메서드를 불러 생성해 넘겨줌
- 2.1.3 프로젝트 실행여부 테스트
    1. IntelliJ의 우상단에 있는 버튼으로 Application 실행 → 아래 Run탭에 Spring이 출력되는지 확인
    2. 브라우저에서 localhost:8080으로 접근 → Whitelabel Error Page가 뜨는지 확인
- 2.1.4 포스트맨 API 테스트
    - 이 프로젝트에서 개발할 REST API를 테스트하기 위한 툴.
    - URI, HTTP 메서드, 요청 매개변수 또는 요청 바디 등을 일일이 브라우저에서 테스팅하기 힘들기 때문에 간단히 RESTful API를 테스트하고, 테스트를 저장해 API 스모크 테스팅 용으로 사용하기 위함.

### 2.2 백엔드 서비스 아키텍처

<aside>
📌 학습내용
    1. 레이어드 아키텍처 패턴
    2. REST 아키텍처 스타일
    3. 스프링 어노테이션
    4. JPA와 스프링 Data JPA
실습내용
    1. Model/Entity와 DTO클래스
    2. Controller, Service, Persistence 클래스
    3. 테스팅용 REST API

</aside>

- 2.2.1 Model, Entity, DTO
    - Model & Entity (TodoEntity.java)
        
        : 이 프로젝트에서는 둘을 한 클래스로 구현. 따라서 모델은 비즈니스 데이터를 담기도 하고 데이터베이스의 테이블과 스키마를 표현하는 역할을 하기도 한다.
        
    - DTO (TodoDTO.java, ResponseDTO.java)
        
        : 서비스가 요청을 처리하고 클라이언트로 반환할 때 모델 자체를 리턴하는 대신 DTO로 변환해 리턴
        
        1. 비즈니스 로직을 Encapsulation하기 위하여
        2. 클라이언트가 필요한 정보를 모델이 전부 포함하지 않는 경우가 많기 때문(Ex. 에러메시지)
- 2.2.2 REST API
    
    : REST 제약조건 → 만족하는 API는 RESTful API
    
    - 클라이언트 서버
        
        : 리소스를 관리하는 서버 - 클라이언트들은 네트워크를 통해 서버에 접근하여 리소스 소비
        
    - 상태가 없는
        
        : 클라이언트가 서버에 요청을 보낼 때 이전 요청의 영향을 받지 않는 상태를 의미한다.
        
        예를 들어, login페이지에서 로그인하여 다음 페이지로 이동하는 경우에 다음 페이지로 리소스를 불러올 때 이전 요청에서 로그인한 사실을 서버가 알고있어야 하는 경우는 상태가 있다. 따라서 서버가 로그인 상태를 유지하지 못하므로 요청을 보낼때마다 로그인 정보를 항상 함께 보내야 한다. 리소스 수정 후 상태를 유지시키기 위해서는 서버가 아닌 퍼시스턴스(데이터베이스 등)에 상태를 저장해야 한다.
        
    - 캐시되는 데이터
        
        : 리소스 리턴 시에 캐시가 가능한지 여부를 명시할 수 있어야 한다. (HTTP의 cache-control)
        
    - 일관적 인터페이스
        
        : 리소스에 접근하는 방식, 요청 형식, 응답 형식과 같은 URI, 요청의 형태와 응답의 형태가 애플리케이션 전반에 걸쳐 일관적이어야 한다.
        
    - 레이어 시스템
        
        : 클라이언트가 서버에 요청을 할 때 여러개의 레이어로 구성된 서버를 거칠 수 있으나, 클라이언트는 서버 레이어의 존재를 알지 못하며 요청과 응답에 어떤 영향을 미치지도 않는다.
        
    - 코드-온-디맨드(선택사항)
        
        : 클라이언트는 서버에 코드를 요청할 수도, 서버가 리턴한 코드를 실행할 수도 있다.
        
- 2.2.3 컨트롤러 레이어: 스프링 REST API 컨트롤러
    - TestController
        - @RequestMapping(”test”)는 URI경로에 매핑하고
        - @GetMapping은 HTTP Get 메서드에 매핑
        - 만약 RequestMapping(”test”)가 없다면 단순히 :8080/testGetMapping에 매핑됨
        - @PostMapping, @PutMapping, @DeleteMapping는 HTTP의 각 메서드에 연결
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%201.png)
        
    - Parameter를 넘겨받는 방법
        - @PathVariable
            
            :  **/{id}**와 같이 URI의 경로로 넘어오는 값을 변수로 받을 수 있다. (required = false)는 이 매개변수가 꼭 필요한 것은 아니라는 의미
            
        - @RequestParam
            
            : **?id={id}**와 같이 요청 매개변수로 넘어오는 값을 변수로 받을 수 있다.
            
        - @RequestBody
            
             : 여러 데이터의 묶음처럼 복잡한 리소스를 반환할 때 사용하며, 데이터를 담아 보낼 testRequestBodyDTO가 필요하다.
            RequestBody로 보내오는 JSON{”id”:123, “message”:”Hello World”}을 DTO오브젝트로 변환해 가져온다. 따라서 클라이언트가 요청 바디로 JSON형태의 문자열을 넘겨줄 때 DTO의 id, message 형태로 구성되어 있어야 한다. 
            
        - @ResponseBody
            
            : 문자열보다 복잡한 오브젝트를 리턴할 때 사용하며, 요청을 통해 오브젝트를 가져올 수 있는데 응답으로 오브젝트를 리턴할 수 있다.
            
            - 직렬화(Serialization)
            : @RestController는 크게 @Controller @ResponseBody라는 두 어노테이션의 조합으로 이뤄져 있다. @Controller는 @Component로 스프링이 이 클래스의 오브젝트를 알아서 생성하고 다른 오브젝트들과의 의존성을 연결한다는 의미이고, @ResponseBody는 이 클래스의 메서드가 리턴하는 것이 웹 서비스의 ResponseBody라는 의미이다.
             즉, 메서드가 리턴할 때 스프링은 리턴된 오브젝트를 JSON의 형태로 바꾸고 HttpResponse에 담아 반환한다. 이처럼 오브젝트를 저장하거나 네트워크를 통해 전달할 수 있도록 변환하는 것을 직렬화라고 한다.
        - @ResponseEntity
            
            : Http응답의 바디뿐만 아니라 다른 매개변수들(status 또는 header)을 조작할 때 사용
            ResponseBody와 비교하면 리턴된 바디에는 아무 차이가 없으나, 단지 헤더와 HTTP status를 조작할 수 있다는 점이 다르다.
            
- 2.2.4 서비스 레이어: 비즈니스 로직
    
    : Controller와 Persistence 사이에서 비즈니스 로직을 수행하며, 양쪽 모두와 분리되어 있어 로직에 집중할 수 있다.
    
    - TodoService
        
        : 스테레오타입 어노테이션인 @Service를 사용하여 해당 클래스가 스프링 컴포넌트이며 기능적으로는 비즈니스 로직을 수행하는 서비스 레이어임을 알려둔다.
        
    - TodoController
        - @RestController도 내부에 @Component를 가지고 있으므로, @Service, @RestController 모두 스프링이 관리하는 자바빈에 해당한다.
        - 스프링이 TodoController를 생성할 때 내부에 @Autowired가 있음을 확인하면, 이 어노테이션이 알아서 빈을 찾은 뒤 그 빈을 인스턴스 멤버 변수에 연결한다.
        - 따라서 TodoController를 초기화할 때 스프링은 알아서 TodoService를 초기화하거나 검색해서 TodoController에 주입해준다.
        - TodoController.java
        
        ```java
        @RestController
        @RequestMapping("todo")
        public class TodoController{
        
        		@Autowired  
            private TodoService service;
        
            @GetMapping("/test")
        		public ResponseEntity<?>testTodo(){
        				//리턴값 "Test Service"를 str변수에 담음
        				String str = service.testService(); 
        
        				//새로운 어레이리스트 list를 생성
        		    List<String>list = new ArrayList<>();
        				// list에 str변수에 담긴 값을, err에 문자열 "err" 넣음
                list.add(str);                      
                String err = "err";
        
                //ResponseDTO를 제너릭으로 작성했으므로 <String>형으로 쓰겠다고 알림
                //<String>형으로 builder를 사용하고 -> error값에 문자열 "err"를, data값에 앞서 담아둔 문자열 리스트 list를 넣은 뒤 -> 빌드
                ResponseDTO<String>response = ResponseDTO.<String>builder().error(err).data(list).build();
        
                // { "error" : "err", "data" : ["Test Service"] }가 출력된다.
                return ResponseEntity.ok().body(response);
        		}
        }
        ```
        
- 2.2.5 퍼시스턴스 레이어: 스프링 데이터 JPA
    
    : JDBC 드라이버는 자바에서 데이터베이스에 연결할 수 있도록 도와주는 라이브러리다. 
    
    - ORM과 DAO클래스
    : 데이터베이스 콜 스니펫을 살펴보면 JDBC는 Connection을 이용해 데이터베이스에 연결하고 sqlSelectAllTodos에 작성된 SQL을 실행한 후 ResultSet이라는 클래스에 결과를 담아온 뒤 while문 내부에서 ResultSet을 Todo오브젝트로 바꿔준다. 이 일련의 과정을 ORM이라고 하며, 이 작업은 엔티티마다 해주어야 하므로 데이터베이스의 각 테이블마다 엔티티 클래스가 하나씩 매핑되어 존재한다. DAO는 생성/수정/삭제와 같은 기본적인 기능들을 엔티티마다 작성해주는데, 이런 반복작업을 줄이기 위해 ORM프레임워크(Hibernate)나 JPA, 스프링데이터 JPA를 사용하기도 한다.
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%202.png)
        
    - JPA(Hibernate, JPA Repository)
    : 반복해서 데이터베이스 쿼리를 보내 ResultSet을 파싱해야 하는 작업을 줄여준다. JPA는 스펙으로, 구현을 위해 특정 기능을 작성하라고 알려주는 지침과 같다. 자바에서 데이터베이스 접근, 저장, 관리에 필요한 스펙이 명시한대로 동작한다면 그 내부 구현 사항은 구현자의 마음이다. 스프링 데이터 JPA는 JPA를 추상화하여 사용하기 쉽게 도와주는 스프링 프로젝트다.
        - JPA Repository
            
            : 기본적인 데이터베이스 오퍼레이션 인터페이스를 제공하며 주로 상속받아 사용한다. save, findById, findAll와 같은 메서드가 스프링데이터JPA 실행시에 자동으로 구현되므로 sql 쿼리를 일일이 작성할 필요가 없다. 
            
    - H2 Database
        
        : In-Memory 데이터베이스로 로컬 환경에서 메모리상에 데이터베이스를 구축한다. H2는 따로 데이터베이스 서버를 구축하지 않아도 되기때문에 초기 개발시에 주로 사용한다. 애플리케이션 실행시 테이블이 생성되었다가 종료시에 테이블이 함께 소멸되고, @SpringBootApplication이 알아서 애플리케이션을 H2 데이터베이스에 연결한다.
        
    - TodoEntity.java
        - 데이터베이스 테이블마다 그에 상응하는 엔티티 클래스가 존재하며, 하나의 엔티티 인스턴스는 데이터베이스 테이블의 한 행에 해당한다. 엔티티 클래스는 ORM이 엔티티를 보고 어떤 테이블의 어떤 필드에 매핑해야 하는지 알 수 있도록 작성한다. 자바 클래스를 엔티티로 정의할 때 1. 클래스에 noArgsConstructor가 필요하다는 것과 2. Getter/Setter가 필요하다는 두 가지를 유의해야 한다.
        - @GeneratedValue와 @GenericGenerator
        : @GeneratedValue는 ID를 자동으로 생성하겠다는 뜻이며 매개변수인 generator로 ID 생성방식을 지정한다. @GenericGenerator는 Hibernate가 제공하는 기본 제너레이터 대신 새로 정의하고 싶은 경우에 사용하며 기본 제너레이터 값으로는 INCREMENTAL, SEQUENCE, IDENTITY가 있다. 프로젝트에서는 문자열 형태의 UUID 사용을 위해 system-uuid라는 이름의 커스텀 제너레이터를 만든 것이고, 이렇게 만든 제너레이터를 @GeneratedValue가 참조해 사용한다.
    - TodoRepository.java
        
        : 퍼시스턴스를 관리하는 패키지를 만들고 그 아래에 TodoRepository 인터페이스를 생성한다. JpaRepository를 확장받아 작성하며 JpaRepository<T,ID>에서 T는 엔티티클래스를 ID는 엔티티 기본키의 타입이다. 따라서 이 프로젝트에서는 JpaRepository<TodoEntity, String>이 된다.
        

### 2.3 백엔드 서비스 개발

- 2.3.1 Create Todo 구현
    1. 퍼시스턴스 → TodoRepository.java
    : 퍼시스턴스로는 JpaRepository를 상속하는 TodoRepository를 사용한다. 엔티티 저장에는 save를, 새 Todo리스트 반환에는 앞서 구현해둔 findByUserId() 메서드를 사용한다.
    2. 서비스 → TodoService의 *validate*
        - 검증(Validation): 넘어온 엔티티가 유효한지 검사하는 로직.  TodoValidator로 분리가능.
        - save(): 엔티티를 데이터베이스에 저장하고 로그를 남긴다.
        - findByUserId(): 저장된 엔티티를 포함하는 새 리스트를 리턴한다.
    3. 컨트롤러 → TodoDTO의 *toEntity,* TodoController의  *createTodo*
        
        : HTTP 응답을 반환할 때 비즈니스 로직을 캡슐화하거나 추가적인 정보를 함께 반환하기 위해 DTO를 사용한다. 따라서 컨트롤러는 사용자에게서 TodoDTO를 요청바디로 넘겨받고 이를 TodoEntity로 변환하여 저장해야 하며, TodoService의 create()가 리턴하는 TodoEntity를 TodoDTO로 변환해 리턴해야 한다.
        
- 2.3.2 Retrieve Todo 구현
    1. 퍼시스턴스 → 동일
    2. 서비스 → TodoService의 *retrieve*
    3. 컨트롤러 → TodoController의 *retrieveTodoList*
- 2.3.3 Update Tool 구현
    1. 퍼시스턴스 → 동일
    2. 서비스 → TodoService의 *update*
    3. 컨트롤러 → TodoController의 *updateTodo*
- 2.3.4 Delete Tool 구현
    1. 퍼시스턴스
    2. 서비스 → TodoService의 *delete*
    3. 컨트롤러 → TodoController의 *deleteTodo*
- 2.3.5 Postman을 이용한 테스트
    
    : [localhost:8080/todo](http://localhost:8080/todo)에서 POST - GET - PUT - GET - DELETE 순으로 테스트
    
    1. POST (create메서드) : 간단히 title 값을 JSON형태로 준다.
    2. GET (retrieve메서드) : 포스팅한 title 값과 id, done값이 모두 출력되는지 확인.
    3. PUT (update메서드) : title, id, done 값 모두를 JSON형태로 준다.
    4. DELETE (delete메서드) : 요청바디로 id만 명시해준다. 바디 전체를 보내도 상관은 없음.
    

# 3. 프론트엔드 개발

### 3.1 프론트엔드 개발 환경

- 3.1.1 Node.js와 NPM
    
    
    | Node.js | 자바스크립트를 컴퓨터 내에서 실행할 수 있게 해주는 자바스크립트 런타임 환경이다. 자바스크립트로 된 node서버를 이용해 구축한 프론트엔드 서버는 단순히 요청이 왔을 때 HTML, Javascript, CSS를 리턴한다. |
    | --- | --- |
    | NPM | Node.js의 패키지 관리 시스템. |
- 3.1.2 프론트엔드 애플리케이션 생성
    - Node.js 설치 후 버전확인(npm version)
    - 새로운 React 프로젝트 생성
        
        : cd IdeaProjects → npx create-react-app *todoapp-react → npm start*
        
        명령어를 차례로 입력해 IdeaProjects > todoapp-react 프로젝트를 만들고 리액트를 실행한다.
        
    - material-ui 패키지 설치
        
        : npm install @material-ui/core → npm install @material-ui/icons
        
- 3.1.3 브라우저 동작 원리
    
    : 브라우저는 서버로 Http GET 요청을 보내면 그 결과로 HTML파일을 받고, 파싱과 렌더링 단계를 거쳐 텍스트로 된 HTML을 보여준다.
    
    - 파싱
        1. 브라우저가 HTML을 트리 자료구조 형태인 DOM(Domain Object Model)로 변환한다.
        2. 다운해야 하는 리소스들(image, css, script 등)을 다운한다. CSS의 경우 추가로 CSSOM 트리로 변환한다.
        3. 다운받은 자바스크립트를 interpret, compile, parsing 및 실행한다.
    - 렌더링
        1. DOM트리(내용)와 CSSOM트리(디자인)를 합쳐 렌더 트리를 만든다.
        2. 레이아웃을 정한다. (트리의 각 노드가 어디에 배치될지)
        3. 사용자가 시각화된 HTML 파일을 볼 수 있도록 브라우저 스크린에 렌더 트리의 각 노드를 그린다. 
- 3.1.4 React.js
    - SPA(Single Page Application)
    : 한 번 웹페이지를 로딩하면 사용자가 새로고침하지 않는 한 새로 로딩하지 않는 애플리케이션. 브라우저의 자바스크립트가 fetch, ajax 등의 함수로 서버에 데이터를 요청하고, 받은 데이터를 기반으로  서버에 새 페이지를 요청하는 대신 자바스크립트가 동적으로 HTML페이지를 재구성한다.
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%203.png)
        
        1. 처음 하얀 화면이 뜰 때 브라우저는 index.html을 로딩하고 있다. 
        2. HTML의 <body>태그 부분을 렌더링하다 보면 마지막에 bundle.js라는 스크립트를 로딩하게 된다. bundle.js는 npm start를 실행했을 때 만들어진 스크립트로 index.js를 포함하고 있다. 
        3. 즉, index.html의 렌더링 과정에서 index.js가 실행되고 덩달아 내부의 ReactDom.render()가 실행된다. 이 render 함수가 동적으로 HTML 엘리먼트를 우리 눈에 보이는 리액트 첫 화면으로 바꾸어준다.
        4. 렌더링된 하위 엘리먼트는 render()의 매개변수 <App /> 부분이다. 만약 페이지를 바꿔주고 싶다면 root의 하위 엘리먼트를 다른 HTML로 수정하면 된다.
    - React Component
        
        : 컴포넌트는 자바스크립트 함수 또는 자바스크립트 클래스(render함수 필요) 형태로 생성할 수 있다. 컴포넌트는 렌더링 부분인 HTML과 로직 부분인 자바스크립트를 포함하는 JSX를 리턴하는데 JSX는 Babel이라는 라이브러리가 빌드 시에 자바스크립트로 번역해준다. ReactDOM은 매개변수로 넘겨받은 컴포넌트로 DOM트리를 만들고, 컴포넌트의 render함수가 반환한 JSX를 렌더링한다.
        
        ```jsx
        import React from 'react';              //React import
        import ReactDOM from 'react-dom';       //React DOM import
        import './index.css';                   //css import
        import App from './App';                //작성한 App Component import
        import reportWebVitals from './reportWebVitals';
        
        ReactDOM.render(    //ReactDOM내부의 컴포넌트들을 'root' 엘리먼트에 rendering
          <React.StrictMode>
            <App />
          </React.StrictMode>,    //첫번째 매개변수
          document.getElementById('root')   //두번째 매개변수 root 엘리먼트
        );
        ```
        
        1. import를 이용해 App 컴포넌트를 불러온다.
        2. ReactDOM.render()함수의 매개변수로 <컴포넌트이름 />을 주면 컴포넌트가 렌더링된다.
        
        ReactDOM.render는 첫번째 매개변수로 리액트 컴포넌트를 받고, 두번째로는 root 엘리먼트를 받는다. 이는 index.html에 정의되어 있는 root 엘리먼트 아래에 해당 컴포넌트를 추가하라는 의미이다. React로 만든 모든 컴포넌트는 이 root 엘리먼트 하위에 추가된다.
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%204.png)
        
    

### 3.2 프론트엔드 서비스 개발

- 3.2.1 Todo 리스트
    1. 간단한 체크박스와 라벨을 렌더링하는 Todo 컴포넌트를 만든다.
    2. 현재 index.html에는 App 컴포넌트가 렌더링되고 있으므로, Todo 컴포넌트를 App 컴포넌트의 render 함수 안쪽 <div> 아래에 작성한다.
    3. <Todo />를 여러번 작성해주면 Todo 역시 개수에 맞게 출력된다.
    
    그러나, 앞으로는 API에서 받아온 임의의 Todo 리스트를 출력해야 하므로 Todo 컴포넌트에 title을 비롯한 필요한 매개변수들을 생성자를 통해 넘겨야 한다.
    
    - Props와 State
        
        Todo 컴포넌트의 생성자에서 super를 이용해 props를 초기화시키고, this.state를 item 변수와 props.item으로 초기화한다. state는 리액트가 관리하는 오브젝트로, 추후에 변경할 수 있는 변수를 state에서 관리한다. 자바스크립트 내에서 변경한 변수의 값을 setState를 통해 HTML에 다시 렌더링하기 위해서다.
        
        - 아래 그림은 App 컴포넌트가 Todo 컴포넌트에 item 매개변수를 넘기는 과정이다.
            1. App컴포넌트의 생성자에서 props를 넘겨받고, this.state에서 item을 초기화해준다.
            2. 이후 this.state.item을 이용해 item오브젝트에 접근할 수 있다. 
            3. Todo 태그에서 item={변수}를 이용해 props로 매개변수를 넘길 수 있다.
            
            ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%205.png)
            
- 3.2.2 Add 핸들러 추가
    - onInputChange
        
        사용자가 input 필드에 키를 하나 입력할 때마다 실행되는 자바스크립트 함수이다. input 필드에 입력된 문자열을 자바스크립트 오브젝트에 저장한다. 사용자가 키보드로 입력할때마다 TextField 컴포넌트에서 Event e가 발생하고 그로 인해 onChange 함수가 실행된다. 이런 함수를 이벤트 핸들러라고 하며 개발자가 직접 정해주어야 한다.
        
        ```jsx
        //(1)함수 작성
        onInputChange = (e) => {
            const thisItem = this.state.item;
            thisItem.title = e.target.value;
            this.setState({item: thisItem});
            console.log(thisItem);
        }
        ```
        
        이벤트가 발생해 onChange에 연결된 onInputChange가 실행되면 이벤트 오브젝트인 e가 매개변수로 넘어온다. 이 오브젝트의 target.value에는 현재 화면의 InputField에 입력된 글자들이 담겨있다. 따라서 e.target.value를 item의 title로 지정한 뒤 setState를 통해 item을 업데이트하여 사용자의 Todo 아이템을 임시로 저장한다.
        
        ```jsx
        <TextField
            placeholder="Add Todo here"
            fullWidth
            onChange={this.onInputChange}
            value={this.state.item.title}
        />
        ```
        
        onInputChange함수를 TextField의 onChange props로 넘기는 작업을 해주면, TextField에 사용자 입력이 들어올 때마다 함수가 실행된다. 
        
    - onButtonClick (Add 핸들러)
        
        사용자가 + 버튼을 클릭할 때 실행. onInputChange에서 저장하고 있던 문자열을 리스트에 추가
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%206.png)
        
        AddTodo컴포넌트는 상위 컴포넌트(App)의 items에 접근할 수 없다. 따라서 리스트에 추가하는 add 함수는 App컴포넌트에 추가하고, 해당 함수를 AddTodo의 프로퍼티로 넘겨 AddTodo에서 사용한다. AddTodo컴포넌트에서 add를 props로 넘겨받아 onButtonClick에서 사용한다.
        
    - enterKeyEventHandler ( 엔터키 입력시 아이템 추가 )
        
        사용자가 input 필드상에서 엔터 또는 리턴키를 눌렀을 때 실행되며 기능은 onButtonClick과 같다.
        
        AddTodo컴포넌트 내부에 enterKeyEventHandler는 키보드의 키 이벤트 발생 시 항상 실행된다. 이벤트가 Enter키로 발생된 경우 onButtonClick을 실행하면 된다. 그런 다음 작성한 함수를 TextField컴포넌트에 onKeyPress={this.eventKeyEventHandler}로 연결시켜주면 된다. 
        
- 3.2.3 Todo 삭제
    
    : 각 리스트 아이템의 오른쪽에 삭제아이콘을 추가하고, 버튼이 클릭되면 아이템이 삭제된다. 아이콘은 material ui의 ListItemSecondaryAction과 IconButton컴포넌트를 사용한다.
    
    - delete함수를 App.js에 추가 작성
        
        : 기존 items에서 매개변수로 넘어온 item을 제외한 items(newItems변수)를 state에 저장하는 것이다. 매개변수로 넘어온 item을 제외하려고 filter 함수를 사용했고 id를 비교해 item과 id가 같은 경우 제외하는 로직을 작성했다.
        
    - delete함수를 Todo컴포넌트에 연결
        
        ```
        constructor(props) {
            super(props);       //props 오브젝트 초기화
            this.state={item: props.item};  //this.state를 item변수와 props.item으로 초기화한다.
            this.delete=props.delete;
        }
        
        deleteEventHandler = () => {
            this.delete(this.state.item)
        }
        
        ```
        
        Todo에는 아직 delete가 없으므로 Todo.js의 생성자 부분에 this.delete = props.delete를 추가하여 함수를 연결하고, deleteEventHandler 함수를 추가한다. 또 render 함수 안쪽에 deleteEventHandler를 연결하는 코드를 추가로 작성해준다.
        
- 3.2.4 Todo 수정
    - [ ]  체크박스에 체크하는 경우 → 클릭 시 item.done의 값을 변경
        - checkbox 업데이트 기능
            
            : done을 true→false, false→true로 변경해준다.
            
            1. Todo.js에서 checkboxEventHandler 작성
            2. Todo.js에서 onChange에 checkboxEventHandler 연결
    - 타이틀을 변경하고 싶은 경우
        1. Todo 컴포넌트에 readOnly플래그를 두고 true인 경우 수정이 불가능하고 false인 경우 아이템을 수정할 수 있다.
        2. 사용자가 title을 클릭하면 해당 input field가 수정가능한 상태가 된다. (readOnly false)
        3. 사용자가 Enter키를 누르면 readOnly가 true인 상태로 전환된다.
        4. 체크박스 클릭 시 item.done값을 다른 상태로 전환한다.
        - 구현하기
            1. Todo.js에서 ReadOnly상태변수 추가
            2. Todo.js에서 offReadOnlyMode() 추가
            3. readOnly와 offReadOnlyMode 연결
            4. Enter를 누르면 ReadOnly모드를 종료하는 이벤트핸들러
    - Item 수정 함수
        
        : AddTodo 컴포넌트와 같이 사용자가 키보드를 입력할 때마다 item을 새 값으로 변경해주어야 함.
        
        - Todo에서 editEventHandler 작성
        - Todo에서 onChange에 editEventHandler 연결

### 3.3 서비스 통합

현재까지 각각 개별적으로 동작하는 백엔드/프론트엔드 애플리케이션을 구현했다. 이제 자바스크립트의 fetch API를 사용하여 프론트엔드에서 백엔드에 API를 요청하는 코드를 작성한다.

- 3.3.1 componentDidMount 함수
    
    : Todo아이템을 불러오는 부분이다. 마운팅이란 렌더링 초기 과정에서 리액트가 처음으로 각 컴포넌트의 render()함수를 호출하여 자신의 DOM트리를 구성하는 과정을 의미한다. 마운팅 과정에서는 생성자와 render()함수를 호출하고 마운팅을 마친 뒤에는 componentDidMount를 호출한다. 컴포넌트가 렌더링 되자마자 API 콜을 하고 그 결과를 리스트로 보여주기 위해 componentDidMount 함수에 백엔드 API콜 부분을 구현해야 한다.
    
    생성자에서 API콜을 통해 상태를 변경하게 될 경우 프로그램이 예기치 못한 방향으로 흐를 수 있다. 마운팅 전에는 컴포넌트의 프로퍼티가 아직 준비되지 않은 상태이기 때문이다. 이런 경우 경고(’Can’t call setState on a component that is not yet mounted. ..중략. ’)가 발생한다.
    
- 3.3.2 CORS(Cross-Origin Resource Sharing)
    
    : 처음 리소스를 제공한 도메인이 현재 요청하려는 도메인과 다르더라도 요청을 허락해주는 웹 보안 방침이다. 앞서 작성한 componentDidMount 함수의 fetch 부분에서 브라우저는 http://localhost:8080으로 2개의 새 요청을 보낸다. 첫 번째 요청은 OPTIONS 메서드 사용 요청이다. 이 메서드는 보통 리소스에 대해 어떤 HTTP 메서드를 사용할 수 있는지 확인하고 싶을 때 보낸다. OPTIONS 요청이 반환되고 CORS여부와 GET 요청 사용가능 여부를 확인하면 두 번째 요청으로 원래 보내려고 했던 요청을 보낸다.
    
    CORS가 가능하려면 백엔드에서 설정해줘야 한다. 스프링부트 애플리케이션 프로젝트에서 config 패키지를 만들고 그 아래에 WebMvcConfig라는 클래스를 만들어 관련 설정 코드를 작성한다. addCorsMapping 메서드는 모든 경로(/**)에 대해 Origin이 http://localhost:3000인 경우 GET, POST, PUT, PATCH, DELETE 메서드를 이용한 요청과 더불어 모든 헤더와 인증에 관한 정보를 허용한다. 이후에는 :8080/todo 요청이 status Code 200을 반환한다.
    
- 3.3.3 fetch
    
    이 프로젝트에서는 fetch를 이용해 백엔드에 HTTP요청을 보내며, 그 결과로 Promise를 리턴받는다.
    
    - 자바스크립트 Promise
        
        : 비동기 오퍼레이션에서 사용한다. 자바스크립트의 싱글스레드 환경에서는 브라우저가 백엔드에 요청을 보내면 백엔드가 그걸 처리하는동안 아무것도 못하는 상태가 되므로, 이를 극복하기 위해 자바스크립트 스레드 밖에서 Web API와 같은 오퍼레이션을 실행해 준다. 
        
        즉, HTTP요청을 XMLHttpequest같은 Web API로 보내는 과정에서 발생할 수 있는 콜백 지옥(Callback Hell)을 피하게 해 준다. Promise는 이 함수를 시행한 뒤 오브젝트에 명시된 사항들을 실행시켜주겠다는 일종의 약속이다. Promise에는 Pending(오퍼레이션이 끝나길 기다리는 상태), Resolve(오퍼레이션의 성공적인 종료를 알리고 원하는 값을 전달), Rejecte(오퍼레이션 중 에러가 나는 경우) 상태가 있다.
        
        ```jsx
        function exampleFunction(){
        	return new Promise((resolve, reject) => {
        		var oReq = new XMLHttpRequest();
        		oReq.open("GET", "http://localhost:8080/todo");
        		oReq.onload = function(){
        			resolve(oReq.response);   //Resolve 상태
        		};
        		oReq.onerror = function(){
        			reject(oReq.response);    //Reject 상태
        		};
        		oReq.send();   //Pending 상태
        	});
        
        	exampleFunction()
        		.then((r) => console.log("Resolve" + r))
        		.catch((e) => console.log("Rejected" + e));
        ```
        
        then이나 catch로 넘기는 함수들은 지금 당장 실행되는 것이 아니라 매개변수로 해야할 일을 넘겨줄 뿐이다. 실제 이 함수들이 실행되는 것은 resolve와 reject가 실행되는 시점이다.
        
    - Fetch API
        
        : 자바스크립트가 제공하는 메서드로 API 서버로 http요청을 송신하거나 수신할 수 있도록 돕는다. url을 매개변수로 받거나, url과 options를 매개변수로 받을 수 있다. fetch()함수는 Promise 오브젝트를 리턴하므로, then과 catch에 콜백 함수를 전달해 응답을 처리할 수 있다.
        
        - 프론트엔드 프로젝트에서 src > app-config.js와 src > service > ApiService.js 생성
        - ApiService에서 작성한 call 메서드를 App 컴포넌트에서 사용할 수 있도록 코드 변경
        
        이제 브라우저를 새로 고침하거나 프론트엔드 애플리케이션을 재시작해도 기존의 Todo리스트가 사라지지 않는다. 백엔드의 데이터베이스에서 Todo 리스트를 가져오도록 코드를 수정했기 때문이다. 
        
    - Todo Update 수정
        
        : 지금까지는 mock()함수를 사용하여 사용자 입력이 발생하면 editEventHandler가 title을 수정해주었으나, API를 이용하여 업데이트하려면 Service API를 이용하여 서버데이터를 업데이트한 뒤 변경된 내용을 다시 화면에 출력하는 과정이 필요하다.
        
        - App 컴포넌트에 update()함수를 구현하고 Todo의 props에 연결
        - Todo 컴포넌트에서 update 연결하여 사용
        - 테스트 - 업데이트시 PUT 메서드를 이용하는 HTTP 리퀘스트 사용했는지 확인 → 성공
            
            ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%207.png)
            
            ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%208.png)
            

# 4. 인증 백엔드 통합

### 4.1 REST API 인증기법

- 4.1.1 Basic 인증
    
    : Todo 애플리케이션은 로그인을 제외하면 특별히 어떤 상태를 유지해야 할 필요가 없으므로 REST 아키텍처를 사용한다. 이를 구현하는 가장 간단한 방법(Basic 인증)은 모든 HTTP 요청에 아이디와 비밀번호를 같이 보내는 것이다. 최초 로그인한 후 HTTP요청 헤더의 Authorization 부분에 ‘Basic <ID>:<Pwd>’처럼 아이디와 비밀번호를 콜론으로 이어붙인 뒤 Base64로 인코딩한 문자열을 함께 보낸다. 
    
    그러나 이 방법에는 단점이 있다. 
    
    1. 아이디와 비밀번호를 노출하기 때문에 HTTP대신 HTTPS와 함께 사용해야 한다
    2. 사용자를 로그아웃 시킬 수 없다. 디바이스 별 로그아웃 관리기능도 당연히 제공할 수 없게 된다.
    3. 사용자의 계정 정보가 있는 저장 장소, 인증 서버와 인증 DB에 과부하가 걸릴 수 있다. 참고로 이 경우, 인증 서버가 단일 장애점이 되어 오류가 나면 전체 시스템이 가동 불가능해진다.
- 4.1.2 토큰 기반 인증
    
    : 토큰은 사용자를 구별할 수 있는 문자열로, 최초 로그인 시 서버가 생성하고 나면 클라이언트가 이후 요청에 따라 아이디와 비밀번호 대신 토큰을 넘겨 인증을 진행한다. 이 방법도 HTTP요청 헤더의 Authorization 부분에 Bearer<TOKEN>을 명시하면 된다.
    
    ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%209.png)
    
    Basic 인증과 달리 아이디와 비밀번호를 매번 네트워크를 통해 전송할 필요가 없어 좀 더 안전하고, 서버가 토큰을 통해 사용자의 정보(User, Admin 등)나 유효시간을 정해 관리할 수 있다. 그러나 토큰기반 인증 역시 *Basic 인증의 스케일 문제를 해결할 수는 없다.*
    
- 4.1.3 JSON 웹 토큰: JWT
    
    : 서버에서 ***전자 서명된 토큰***을 이용하면 인증에 따른 스케일 문제를 해결할 수 있다. JWT는 (Base64로 디코딩했을 때)JSON형태로 된 토큰이며 header, payload, signature로 구성되어 있다. JWT도 토큰 기반 인증이므로 서버가 생성한다. 다른 점은 서버가 헤더와 페이로드를 생성한 뒤 전자 서명을 한다는 점이다.
    
    | Header | typ: 토큰의 타입
    alg: 토큰의 서명을 발행하는데 사용된 해시알고리즘 |
    | --- | --- |
    | Payload | sub: 토큰의 주인. ID처럼 유일한 식별자
    iss: 토큰을 발행한 주체(Issuer)
    iat: issued at의 줄임말로 토큰이 발행된 날짜와 시간을 의미
    exp: expiration의 줄임말로 토큰이 만료되는 시간을 의미 |
    | Signature | 토큰을 발행한 주체(Issuer)가 발행한 서명으로 토큰의 유효성 검사에 사용 |
    1. 최초 로그인 시 서버는 사용자의 아이디와 비밀번호를 서버의 것과 비교해 인증
    2. 사용자의 정보를 이용해 헤더, 페이로드를 작성하고 자신의 시크릿키로 전자 서명
    3. 전자 서명의 결과로 나온 값을 헤더, 페이로드, 서명으로 이어붙이고 Base64로 인코딩 후 반환
    4. 누군가 리소스 접근을 요청하면 이 토큰을 Base64로 디코딩하여 JSON을 세 부분으로 나눔
    5. 헤더, 페이로드 부분과 자신의 시크릿키로 전자서명을 만들고 이를 HTTP요청의 서명 부분과 비교

### 4.2 User 레이어 구현

- 4.2.1 UseEntity
    
    : 사용자 엔티티를 생성한다. 사용자는 id, username, email, password로 구성되며 id는 고유식별자이고 email은 사용자 id처럼 기능한다.
    
- 4.2.2 UserRepository
    
    : interface 파일을 새로 생성하고 JpaRepository를 상속받는 repository에 findByEmail(), existsByEmail(), findByEmailAndPassword() 메서드들을 선언한다.
    
- 4.2.3 UserService
    
    : TodoService처럼 사용자의 데이터베이스에 저장된 사용자를 가져올 때 사용한다. UserRepository를 이용해 사용자를 생성하고 로그인 시 인증에 사용할 메서드를 작성한다.
    
- 4.2.4 UserController
    
    : 사용자 서비스를 이용해 현재 사용자를 가져오는 기능과 레지스터 기능을 구현하기 위해 UserDTO 먼저 구현한 뒤 UserController를 구현한다. registerUser()와 authenticate() 메서드를 작성하고 각각 /signup과 /signin으로 매핑한다. 자세한 내용은 코드의 주석 참고.
    
- 4.2.5 테스트 및 중간정리
    - 테스트
        1. /auth/signup에 email, username, password를 적은 JSON을 POST로 보낸다.
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2010.png)
        
        1. /auth/signin에 email, password를 적어 POST로 보낸다.
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2011.png)
        
    - 중간 정리
        1. 로그인은 가능하나 로그인 상태가 유지되지는 않는다.
        2. 이 API는 사용자의 로그인 여부를 확인하지 않으므로 로그인을 하지 않아도 로그인한 사용자와 같은 내용의 Todo리스트를 보고 있는 셈이다.
        3. 패스워드를 암호화하지 않아 보안에 취약하다.
        
         따라서 스프링 시큐리티에서 제공하는 몇 가지 인터페이스를 이용하여 로그인 상태를 유지하고 지속적으로 인증할 수 있도록 추가로 구현해본다.
        

### 4.3 스프링 시큐리티 통합

- 4.3.1 JWT 생성 및 반환
    
    JWT 토큰을 생성하고 인증하는 과정은 아래 그림과 같다. 1, 2번은 토큰을 발행하고 로그인시 그 토큰을 반환하는 과정을 나타낸다. 3,4번은 스프링 시큐리티를 이용하여 해당 토큰으로 API마다 인증한다.
    
    - 그림 - JWT 토큰 생성과 인증
        
        ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2012.png)
        
    
    지금은 JWT 토큰을 생성하고 로그인 시 반환하는 부분을 아래 순서대로 구현한다.
    
    1. jjwt 라이브러리 사용을 위해 gradle 디펜던시에 추가하고, security 패키지 아래 TokenProvider 클래스를 생성한다.
        1. create(): JWT 라이브러리를 이용해 토큰을 생성
        2. validateAndGetUserId(): 토큰을 디코딩하거나 파싱하고 토큰의 위조여부를 확인한 뒤 sub(사용자의 아이디)를 리턴
    2. 로그인 부분에서 TokenProvider를 이용해 토큰을 생성한 뒤 UserDTO에 반환하는 Controller 코드 작성
    3. 테스트
        1. /auth/signup에서 HTTP POST 메서드로 email, username, password를 보낸다.
        2. /auth/signin에서 email, password를 보내 로그인하고 token이 반환되는지 확인한다.
            
            ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2013.png)
            
- 4.3.2 스프링 시큐리티와 서블릿 필터
    
    : 스프링 시큐리티란 서블릿 실행 전에 실행되는 클래스들인 서블릿 필터의 집합이다.
    
    ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2014.png)
    
    - 서블릿 필터
        
        : 스프링이 구현하는 서블릿 이름은 Dispatcher Servlet으로 서블릿 필터는 이 디스패처 서블릿이 실행되기 전에 항상 실행된다. 서블릿 필터는 구현된 로직에 따라 원하지 않는 HTTP 요청을 걸러낼 수 있으며 살아남은 요청들만 디스패처 서블릿으로 넘어와 컨트롤러에서 실행된다. 
        
        스프링시큐리티 프로젝트를 추가하면 스프링 시큐리티가 FilterChainProxy라는 필터를 서블릿 필터에 끼워 넣어준다. 이 FilterChainProxy 클래스 안에서 내부적으로 필터를 실행시키는데 이 필터들이 스프링이 관리하는 스프링 빈(bean)필터다.
        
        - [참고] 서블릿 필터의 구현
            
            서블릿 필터는 HttpFilter 또는 Filter를 상속하여 doFilter메서드를 원하는대로 오버라이딩하여 사용하며, Bearer 토큰을 가져와 TokenProvider를 이용해 사용자 인증을 진행하고 인증에 실패한 경우 HttpServletResponse의 status를 Forbidden으로 변경한다. 필터 구현이 끝나면 서블릿 컨테이너(톰캣 등)가 <filter>, <filter-mapping>을 통해 필터를 사용할 수 있도록 설정한다. 서블릿 필터는 기능에 따라 여러 개로 나눠 사용할 수 있으며, FilterChain을 통해 각 필터가 연쇄적으로 순차적으로 실행된다.
            
- 4.3.3 JWT 인증 구현
    
    : 구현하고자 하는 프로젝트는 한 번만 인증하면 되는 구조이므로, JwtAuthenticationFilter는 OncePerRequestFilter라는 한 요청당 반드시 한번만 실행되는 클래스를 상속한다. 내부에서 토큰을 파싱해 인증하는 과정은 다음과 같다.
    
    1. parseBearerToken 메서드를 이용하여 Http 요청의 헤더에서 Bearer토큰을 가져온다.
    2. TokenProvider를 이용해 토큰을 인증하고 UsernamePasswordAuthenticationToken을 작성한다. 이 오브젝트에 사용자의 인증 정보를 저장하고 SecurityContext에 인증된 사용자를 등록한다.
        1. SecurityContext를 SecurityContextHoler의 createEmptyContext메서드를 이용해 생성한다.
        2. 여기에 인증정보인 authentication을 넣어 다시 SecurityContextHolder에 컨텍스트로 등록한다.
        
        SecurityContextHolder는 ThreadLocal에 저장되므로 스레드마다 하나의 컨텍스트를 관리할 수 있고, 같은 스레드 내라면 어디에서든 접근 가능하다.
        
- 4.3.4 스프링 시큐리티 설정
    
    : 서블릿 필터를 구현했으면 톰캣같은 서블릿 컨테이너에게 해당 서블릿 필터를 사용설정 해줘야 한다. 스프링 시큐리티를 사용하고 있으므로 스프링 시큐리티에 JwtAuthenticationFilter를 사용하라고 알려준다. Config 패키지 아래에 WebSecurityConfig 클래스를 생성하고 WebSecurityConfigurerAdapter를 상속받는다.
    
    - HttpSecurity: 시큐리티 설정을 위한 오브젝트로 빌더를 통해 cors, csrf, httpBasic, session, authorizeRequests 등 다양한 설정이 가능하다. web.xml 대신 HttpSecurity를 이용해 스프링 시큐리티를 관리하는 것이다.
- 4.3.5 TodoController에서 인증된 사용자 사용하기
    
    : 인증된 사용자 아이디가 있으므로 TodoController의 임시사용자 아이디를 인증된 사용자 아이디인 변수 userId로 변경해준다. userId는 스프링이 @AuthenticationPrincipal을 이용해 찾아내어 넘겨준 것이다. 이 어노테이션은 JwtAuthenticationFilter 클래스에서 생성하여 매개변수로 String 형의 userId와 AuthenticationPrincipal 등을 넣어주었던 UsernamePasswordAuthenticationToken에 존재한다. 이 오브젝트를 SecurityContext에 등록해두었기 때문에 스프링이 해당 어노테이션의 존재를 인지하고 SecurityContextHolder에서 UsernamePasswordAuthenticationToken 오브젝트를 가져올 수 있는 것이고, 이 오브젝트에서 AuthenticationPrincipal을 가져와 컨트롤러에 넘겨주는 것이다.
    
    - 테스팅 - 독립적인 두 명의 사용자로 애플리케이션 이용하기
        1. user1(hello@world.com)과 user2(hello2@world.com)의 계정을 각각 생성한다.
        2. user1로 로그인한 뒤 얻은 토큰을 /todo에 POST 메서드로 리스트 추가요청과 함께 보낸다.
        3. user2로 같은 작업을 반복하고 결과를 리턴바디를 확인한다. 첫번째 사용자가 추가한 Todo는 보이지 않고 자신의 Todo만 리턴되면 성공적으로 작동하고 있음을 알 수 있다.
    
- 4.3.6 패스워드 암호화
    
    : 사용자에게 받은 패스워드를 BCryptPasswordEncoder가 제공하는 비교 메서드인 matches를 사용하여 서로 비교한다. 같은 값을 인코딩하더라도 할 때마다 값이 다르고 패스워드에 의미없는 랜덤값(Salt)을 붙여 결과를 생성하기 때문에, salt값을 고려해 두 값을 비교해주는 matches가 필요한 것이다.
    

# 5. 인증 프론트엔드 통합

백엔드 서비스에 인증과 인가를 구현하고 나면 프론트엔드에도 로그인, 회원가입, 리디렉션 등의 인증을 구현해줘야 한다. 프론트엔드 애플리케이션은 백엔드로부터 HTTP 요청의 답으로 403을 받으면 로그인 페이지로 리디렉트 해야한다. 또 로그인한 뒤 백엔드 서비스로부터 받은 토큰을 어딘가에 저장해놓고 요청을 보낼때마다 헤더에 Bearer 토큰으로 지정해줘야 한다. 따라서 다음 세 가지를 구현한다.

1. 로그인 페이지와 리디렉션 로직
2. 토큰을 저장할 로컬 스토리지와 토큰 저장 로직
3. 회원가입 페이지

### 5.1 라우팅

- 5.1.1 react-router-dom 설치
    
    : 리액트 애플리케이션 경로에서 npm install react-router-dom 명령어를 입력한다.
    
- 5.1.2 react-router-dom 라이브러리의 필요성
    
    : 우리에게 익숙한 ***서버-사이드 라우팅***은 브라우저에 주소를 입력하여 서버에 http GET 요청을 보내 그 답으로 사이트에 렌더링할 index.html 등의 파일을 받는다. 그러나 ***클라이언트-사이드 라우팅***은 서버에 어떤 요청도 보내지 않고 모든 라우팅을 클라이언트 코드인 자바스크립트로 해결한다. 이 애플리케이션은 클라이언트-사이드 라우팅 라이브러리로 react-router-dom을 사용한다.
    
    ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2015.png)
    
    1. http://localhost:3000에 접속하면 프론트엔드 서버가 앞으로 브라우저에서 필요한 모든 리소스를 담고 있는 리액트 애플리케이션을 리턴한다.
    2. /login에 접속하면 리액트 라우터가 이를 가로채고 URL을 파싱한 뒤 login 템플릿을 렌더링한다. 이 과정은 브라우저 내에서만 실행되므로 인터넷이 끊기더라도 렌더링이 가능하다.
- 5.1.3 로그인 컴포넌트
    - src/Login.js - 3000/login에서 렌더링할 컴포넌트
    - src/AppRouter.js - 위 컴포넌트로 라우팅할 수 있도록 모든 라우팅 규칙을 작성
    - Index.js - 기존에는 ReactDOM에 App 컴포넌트를 넘겨주었으나 이제는 경로에 따라 실행되는 컴포넌트가 다르기 때문에 그 정보를 가진 AppRouter를 가장 먼저 렌더링 해야한다.
- 5.1.4 접근 거부 시 로그인 페이지로 라우팅
    
    : 접근거부는 [localhost:8080/todo](http://localhost:8080/todo로)에 API 콜을 했을 때 받는다. 
    

### 5.2 로그인 페이지

- 5.2.1 API 서비스 메서드 작성
    
    로그인 API 서비스는 /auth/signin이며, 이 경로를 이용해 로그인하는 메서드는 ApiService.js에 작성해야 한다. 또한 Login 컴포넌트를 수정하여 이메일과 패스워드를 받는 인풋 필드와 로그인 버튼으로 이루어진 페이지를 만든다. 사용자가 로그인 버튼을 누르면 백엔드의 /auth/signin으로 로그인 요청이 전달된다.
    
    - 테스트
        1. 포스트맨에서 POST로 /auth/signup에 email, username, password를 JSON으로 보내 사용자 계정을 생성한다.
        2. 원래처럼 포스트맨에서 POST로 /auth/signin에 email과 password를 보내는 대신, 브라우저에서 로그인페이지로 이동하여 이메일과 패스워드를 입력하는 인풋필드에 사용자 계정을 입력하고 로그인 버튼을 누른다.
        3. 성공적으로 로그인했다면 아래와 같이 뜨는 로그인 토큰을 확인한다.
            
            ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2016.png)
            
- 5.2.2 로그인에 성공
    
    로그인에 성공하여 토큰이 생성된 경우, Todo리스트가 있는 화면으로 돌아갈 수 있도록 하는 메서드를 작성하기 위해 ApiService.js의 signin을 수정한다. 
    
    그러나 메인페이지로 이동할 경우 액세스 토큰을 함께 보내지 않기 때문에, 유효하지 않은 접근으로 보고 403에러를 리턴하여 로그인 페이지로 이동시킨다. 따라서 로컬스토리지에 액세스 토큰을 저장하고 백엔드 서비스에 접근할 때 이 토큰을 요청에 동봉해야 한다.
    

### 5.3 로컬 스토리지

- 5.3.1 로컬 스토리지
    
    웹 스토리지를 이용하면 사용자의 브라우저에 데이터를 key-value 형태로 저장할 수 있다. 웹 스토리지에는 세션 스토리지와 로컬 스토리지 두 종류가 있다. 사용자가 브라우저를 재시작할 때마다 로그인하게 하고 싶으면 세션스토리지를, 로그인상태를 유지하게 하고 싶으면 로컬스토리지를 사용하면 된다.
    
    브라우저 개발자도구의 콘솔에 아래와 같이 입력해보면 로컬 스토리지에 어떻게 아이템이 저장되고, 어떻게 아이템을 가져올 수 있는지 확인할 수 있다. 또한 Storage 탭에서 애플리케이션별로 사용 저장된 로컬스토리지도 확인 가능하다. 로컬스토리지는 도메인마다 따로 저장되므로, 다른 도메인의 자바스크립트는 다른 도메인의 로컬스토리지에 접근할 수 없다.
    
    ![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2017.png)
    
- 5.3.2 액세스 토큰 저장
    
    로그인시 받은 토큰을 로컬 스토리지에 저장하고, 백엔드에 API콜을 할 때마다 로컬스토리지에서 액세스 토큰을 불러와 헤더에 추가해주는 코드를 작성한다.
    
    1.  로그인 토큰을 로컬스토리지에 저장하기 위해 ApiService의 signin 메서드를 수정한다.
    2. 모든 API의 헤더에 액세스 토큰을 추가하는 부분을 구현한다. 로그인에 관련되지 않은 모든 API콜은 call메서드를 통해 이뤄지므로 call에 토큰이 존재하는 경우 헤더에 추가한다. ApiService.js를 수정한다. 

### 5.4 로그아웃과 글리치 해결

- 5.4.1 로그아웃 서비스
    
    로그인을 위해 액세스 토큰을 로컬스토리지에 추가했으니 로그아웃을 위해서는 로컬스토리지에 존재하는 액세스 토큰을 제거하고 로그인 페이지로 리디렉트하면 된다. 따라서 ApiService에 로컬스토리지 코드들을 작성해두었으므로, 이곳에 signout메서드를 작성한다.
    
- 5.4.2 내비게이션 바와 로그아웃
    
    App 컴포넌트에서 Material UI의 AppBar/ToolBar를 사용하여 내비게이션 바를 구현한다. navigationBar라는 새 변수에 AppBar JSX 코드를 작성하고 return 부분에서 리스트 렌더링 위에 변수를 추가해주면 내비게이션 바가 생긴다. 이제 로그아웃 버튼을 누르면 로그인 페이지로 돌아간다.
    
- 5.4.3 UI 글리치 해결
    
    로그인하지 않은 상태에서 바로 http://localhost:3000에 접속하면 잠깐 Todo리스트가 보이다가 바로 로그인페이지로 이동된다. 이는 라우팅(백엔드 서버에 Todo리스트를 요청하고 결과를 받아 확인)하는데 시간이 걸리기 때문이다. UI를 깔끔하게 정리하기 위해 백엔드에서 Todo리스트를 받아오기 전까지는 로딩중이라는 메시지를 띄우려고 한다. App 컴포넌트를 수정한다.
    

### 5.5 계정생성 페이지

- 5.5.1 계정생성 로직
    
    계정생성은 애플리케이션 구현의 마지막 단계로 백엔드 쪽에서는 signupAPI를 이미 작성했으므로 프론트엔드 부분만 추가하면 된다. 로그인 하단의 링크를 클릭하면 계정생성 페이지로 라우팅한 뒤, 계정을 생성하면 다시 로그인 페이지로 돌아가는 간단한 로직을 구현한다.
    
    ApiService에 백엔드에 signup 요청을 보내는 signup 메서드를 추가한다. 계정생성 페이지는 form 부분, 버튼 부분, 로그인하라는 링크 부분 총 세 가지로 구성되어 있다. 사용자가 버튼을 누르면 submitHandle함수가 실행되고 event.target(=form)에서 데이터를 가져와 요청 바디를 작성하여 ApiService의 signup함수를 통해 계정생성 요청을 한다.
    
    - 테스트
        
        로그인 페이지에서 ‘계정이 없습니까? 가입하세요.’ 링크를 클릭해 계정생성 페이지로 라우팅되는지 확인하였다. 계정생성 페이지에서 ‘이미 계정이 있습니까? 로그인하세요.’ 링크를 눌러 역으로 로그인 페이지로 이동되는지 확인했다. 임의의 계정생성이 완료되면 로그인 페이지로 이동하며, 각 계정으로 로그인하면 저장한 리스트들이 그대로 출력된다.
        

# 6. 프로덕션 배포

### 6.1 서비스 아키텍처

![Untitled](Web%20Development%20101%20467dc773b4bd4176892482d823f6cf72/Untitled%2018.png)

1. 서비스 이용자가 프론트엔드 주소를 브라우저에 입력한다. 그러면 프론트엔드 서버의 애플리케이션 로드 밸런서를 거쳐 오토 스케일링 그룹 내의 EC2 인스턴스 중 하나에 트래픽이 전달된다. EC2 인스턴스 내부에서 실행중인 프론트엔드 애플리케이션이 브라우저에서 동작할 React.js 애플리케이션을 반환한다.
2. 서비스 이용자는 프론트엔드 웹의 화면에서 아이디와 비밀번호를 입력하고 로그인을 클릭한다. 그러면 백엔드 서버의 애플리케이션 로드 밸런서를 통해 백엔드 애플리케이션에 전달된다. 백엔드 애플리케이션은 MySQL 서버와 통신해 데이터를 주고받는다. 이 과정에서 라우트 53, 로드 밸런서, 오토 스케일링 그룹, EC2와 같은 리소스가 필요하다.
- 6.1.1 EC2
    
    EC2는 서버 컴퓨터와 같다. 로컬 환경에서 애플리케이션을 실행하듯이 EC2에서도 애플리케이션을 실행할 수 있다. 다만 로컬환경과 달리 EC2의 IP나 EC2가 제공하는 퍼블릭 도메인을 이용하여 애플리케이션에 접근해야 한다. 즉, localhost:8080/todo 대신 128.29.30.2/todo를 이용해 REST API를 사용한다. 그러나 사이트의 서버 IP 대신 도메인 주소로 애플리케이션에 접근하기 위해 DNS를 사용할 것이다.
    
- 6.1.2 Route 53 - DNS
    
    DNS는 도메인 이름과 주소를 매핑해놓은 시스템이다. 브라우저는 DNS에 도메인 이름을 물어보고 IP를 가져와 통신하는 작업을 수행한다. 각 컴퓨터는 IP 외에도 주로 ISP(인터넷 서비스 제공자)가 제공하는 DNS 서버 IP를 가지고 있다. 이 ISP DNS 서버에 찾고자 하는 주소가 없으면 여러 다른 DNS 서버들에 요청하여 최종적으로 원하는 주소를 찾아온다. 이 때 다른 DNS 서버 중 하나가 Route 53이다. Route 53을 통해 애플리케이션 도메인을 우리가 지정한 IP(EC2 인스턴스 IP 등)에 매핑할 수 있다. 여기까지 완료하면 누구든 app.fsoftwaregineer.com로 접속할 때마다 EC2로 라우팅된다.
    
- 6.1.3 애플리케이션 로드 밸런서
    
    사용량이 늘어나 EC2의 인스턴스를 두 개 이상으로 늘려야 할 경우가 되면, 각 인스턴스에 트래픽을 적절히 분배해주기 위해 VIP(Virtual IP)라는 공유 IP를 사용한다. 애플리케이션 로드 밸런서는 이렇게 공유 IP를 사용하는 여러 서버들에게 트래픽을 적절히 분배한다. 또한 HTTP/HTTPS 요청을 연결된 서버로 분배한다.
    
- 6.1.4 오토 스케일링 그룹(ASG)
    
    AWS 애플리케이션 로드 밸런서에 2개의 인스턴스가 연결되어 있을 때, 이 인스턴스를 AWS에서는 타깃그룹이라고 한다. ASG는 타겟 그룹 중 한 인스턴스가 다운되었을 경우, 자동으로 스케일해준다. ASG에 최소, 최대, 적정 인스턴스 수를 설정해두면 일부 인스턴스가 다운되어 최소 개수보다 작아지면 해당 인스턴스를 제거하고 새로운 인스턴스를 실행시킨다. 또 트래픽 변동에 따라 자동으로 스케일 인/아웃도 가능하다.
    
- 6.1.5 VPC와 서브넷
    
    VPC는 사용자의 AWS계정 전용 가상 네트워크이다. 가상 네트워크는 특별한 설정 없이는 내부에서 생성되는 EC2에 외부접근이 불가능해 가상 사설 네트워크라고도 한다. VPC 안에는 서브넷이라는 여러개로 쪼개진 네트워크가 존재하고 한 서브넷 내에 EC2 서버가 생성된다. 서브넷 설정에 따라 EC2의 사설IP 주소가 결정된다.
    
- 6.1.6 일라스틱 빈스톡
    
    앞서 설명한 모든 인프라를 대신 구축해주는 서비스가 AWS 일라스틱 빈스톡이다. 로드밸런서, 최소 인스턴스 개수, 데이터베이스 등 필요한 리소스를 알려주기만 하면 일라스틱 빈스톡이 로드밸런서, ASG, RDS(데이터베이스), EC2환경을 구축하고 EC2에서 애플리케이션을 실행한다.
    
