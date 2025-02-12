# 이커머스 서비스
대용량 트래픽 처리가 가능한 이커머스 서비스

## 프로젝트 기능 및 설계 (기본 기능)
- 기본 설정
  - USERID 1번은 ADMIN이다.

- 회원가입 기능
  - 사용자는 회원가입해서 서비스를 이용한다.
  - 회원가입하면 기본적으로 USER권한(기본 권한)이 부여된다.
  - 회원가입시 아이디는 Email이다.
  - 회원가입시 이메일 인증을 거치며 아이디는 유니크 해야한다.
 
- 회원정보 수정 기능
  - 사용자는 로그인 후 회원 정보를 수정할 수 있다.
  - 수정할 수 있는 정보는 이름, 비밀번호, 주소, 상세주소, 핸드폰 번호이다.
 
- 로그인
  - 사용자는 인증된 아이디, 패스워드로 로그인 할 수 있다.

- 로그아웃 기능
  - 사용자는 보안을 위해 원할 때 로그아웃 할 수 있다.
 
- 상품 관리 기능
  - ADMIN은 상품을 추가, 삭제, 수정할 수 있다.
  - 상품 이름, 상품 설명, 상품 가격, 상품 재고 갯수로 상품을 추가 한다.
  - 같은 이름의 상품은 등록할 수 없다.

- 장바구니 확인 기능
  - 회원은 장바구니에 담긴 모든 상품을 확인 할 수 있다.
  - 로그인 하지 않으면 장바구니에 아무것도 표시되지 않는다.
 
- 장바구니 상품 추가 기능
  - 회원은 장바구니에 상품을 추가할 수 있다.
  - 상품은 100개까지 추가 가능하다.
  - 똑같은 상품을 장바구니에 추가하면 상품 갯수만 늘어난다.
 
- 장바구니 상품 삭제 기능
  - 회원은 장바구니에 추가했던 상품을 삭제할 수 있다.
  - 장바구니에 있는 상품을 삭제하면 갯수와 상관없이 모두 삭제된다
 
- 장바구니 상품 갯수 수정 기능
  - 회원은 장바구니에 담긴 상품의 갯수를 늘리거나 줄일 수 있다.
  - 장바구니에 담긴 상품의 갯수를 0으로 수정하면 장바구니에서 삭제된다.
 
- 상품 검색 기능
  - 로그인하지 않은 사용자를 포함한 모든 사용자는 상품명이나 키워드로 상품을 검색을 할 수 있다.
  - 가나다순, 별점순, 가격순으로 정렬이 가능하다.
 
## ERD
![Image](https://github.com/user-attachments/assets/c8d60a5c-3b97-4bc5-8fdb-83ffe9104517)


## 프로젝트 목표
1. 경험해보지 못한, 선호도가 높은 기술들을 사용하고 공부
2. 많은 데이터들을 다뤄볼 수 있게 프로젝트를 확장(테이블 추가)
3. 프로젝트를 완료 후 배포


## 사용할 라이브러리, 프레임워크
1. Spring Web
2. Spring Jpa
3. Spring Security
4. JJWT
5. Mockito
6. PostgreSQL
7. Redis
8. Lombok
9. Java Mail Sender


## 기술 스택
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
