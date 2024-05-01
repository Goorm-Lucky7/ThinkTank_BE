# WebIDE 구현 feat.THINKTANK 7️⃣
<img src="readme-image/thinktank.png" width="1000" height="600" alt="thinktank.png"/>

## 메인 페이지
<img src="readme-image/main-page.png" width="1000" height="600" alt="main-page.png"/>


## 🧑‍🧑‍🧒‍🧒 TEAMMATE 소개
| ![박창민](https://github.com/ChangMinPark2.png) | ![강솔문](https://github.com/solmoonkang.png) | ![이지수](https://github.com/leedidoo.png) | ![심예은](https://github.com/hesener.png) |
|----------------------------------------------|----------------------------------------------|-------------------------------------------|------------------------------------------|
| [박창민](https://github.com/ChangMinPark2)      | [강솔문](https://github.com/solmoonkang)   | [이지수](https://github.com/leedidoo)   | [심예은](https://github.com/hesener)    |
| **BackEnd**                                  | **BackEnd**                                   | **BackEnd**                                | **BackEnd**                               |
| 체점(메인), 배포, BE 팀장                            | 회원 로그인, 회원가입, 마이페이지                    | 게시글, 좋아요                             | 댓글, 소셜 로그인                        |


## 아키텍처
<img src="readme-image/architecture.png" width="1000" height="600" alt="architecture.png"/>

## 배포환경
<img src="readme-image/server.png" width="1000" height="600" alt="server.png"/>


## 기술 스택
| **Language & Library** | ![Java](https://img.shields.io/badge/Java-F89820?style=for-the-badge&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) |
|------------------------|------------------------|
| **Database**           | ![H2](https://img.shields.io/badge/H2-0078D7?style=for-the-badge&logo=h2&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white) |
| **CI/CD**              | ![AWS](https://img.shields.io/badge/AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) |
| **HTTP**               | ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white) |
| **Test**               | ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-007ACC?style=for-the-badge&logo=mockito&logoColor=white) |


## 패키지 구조
```text
com.thinktank
├── api
│   ├── controller
│   ├── dto
│   │   ├── post
│   │   │   ├── request 
│   │   │   └── response
│   │   ├── user
│   │   │   ├── request
│   │   │   └── response
│   │   ├── comment
│   │   │   ├── request
│   │   │   └── response
│   │   ├── judge
│   │   │   ├── request
│   │   │   └── response
│   │   └── like
│   │       ├── request
│   │       └── response
│   ├── entity
│   ├── repository
│   └── service
└── global
    ├── common
    │   └── util
    ├── error
    │   ├── exception
    │   ├── handler
    │   └── model
    ├── auth
    │   ├── filter
    └── config

```

## ERD
<img src="readme-image/erd.png" width="1000" height="600" alt="erd.png"/>



## 코드 컨벤션

팀 내에서 지켜야 할 코드 컨벤션을 명시합니다. 이 컨벤션들은 코드의 가독성을 높이고, 효율적인 협업 및 유지 보수를 도모하기 위해 정립되었습니다.

### 일반 규칙
- **인텔리제이 네이버 코드 컨벤션 사용** : 가독성 향상과 오류 발생 위험을 줄이기 위해 사용합니다.
- **코드 길이** : 한 줄의 코드 길이는 최대 120자를 넘지 않도록 합니다.
- **클래스 구조** : 클래스는 상수, 멤버 변수, 생성자, 메서드 순으로 작성합니다.

### 네이밍 규칙
- **메서드 이름** : 메서드는 동사+명사의 형태로 명확하게 작성합니다. 예) `saveOrder`, `deleteUser`
- **불린 반환 메서드** : 반환 값이 불린 타입인 경우 메서드 이름은 'is'로 시작합니다. 예) `isAdmin`, `isAvailable`
- **검증 메서드** : 검증에 관한 메서드는 `validate`로 시작합니다. 예) `validateInput`, `validateUser`

### 아키텍처 및 설정
- **계층형 아키텍처** : 프로젝트는 계층형 아키텍처 구조를 따릅니다.
- **BaseTimeEntity** : 날짜 정보가 자동으로 등록되도록 `BaseTimeEntity`를 적용합니다.
- **YML 파일 분리** : 개발 환경에 맞게 `local`, `develop`, `main` 등으로 yml 설정 파일을 분리합니다.

### 특별한 규칙
- **정적 팩토리 메서드 사용** : 객체 생성 시 정적 팩토리 메서드를 사용하여 가독성과 유저 친화성을 높입니다.
- **빌더 패턴 사용** : 생성자의 매개변수가 4개 이상일 경우 빌더 패턴을 사용해 가독성을 높입니다.
- **레코드 활용** : DTO 등 간단한 목적의 클래스에는 Java의 record를 활용하여 코드를 간소화합니다.

## 이슈 템플릿
<img src="readme-image/issue-template.png" width="1000" height="600" alt="issue-template.png"/>


## PR 템플릿
<img src="readme-image/pullrequest-template.png" width="1000" height="600" alt="pullrequest-template.png"/>


## 커밋 메시지 규칙
- **`refactor`**: 코드 리팩터링 시 사용합니다.
- **`feat`**: 새로운 기능 추가 시 사용합니다.
- **`fix`**: 버그 수정 시 사용합니다.
- **`chore`**: 빌드 업무 수정, 패키지 매니저 수정 시 사용합니다.
- **`style`**: 코드 포맷 변경, 세미콜론 누락, 코드 수정이 없는 경우 사용합니다.
- **`docs`**: 문서 수정 시 사용합니다.
- **`test`**: 테스트 관련 코드 시 사용합니다.
- **`Move`**: 코드 또는 파일의 이동이 있을 경우 사용합니다.
- **`Rename`**: 파일명(or 폴더명)을 수정한 경우 사용합니다.
- **`Remove`**: 코드(파일)의 삭제가 있을 경우 사용합니다.
- **`Comment`**: 주석 추가 및 변경이 있을 경우 사용합니다.
- **`Add`**: 코드나 테스트, 예제, 문서 등의 추가 생성이 있을 경우 사용합니다.

