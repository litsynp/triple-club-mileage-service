# 🧳 트리플여행자 클럽 마일리지 서비스

트리플 사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에 대한 포인트 부여 히스토리와, 개인별 누적 포인트를 관리하고자 합니다.

Spring Boot 애플리케이션을 이용하여 주어진 문제를 해결하고 그 방안에 대해서 이야기합니다.

## 🔍 구현 기능

- 장소에 리뷰를 작성하면 포인트를 부여
- 전체/개인에 대한 포인트 부여 히스토리 관리
- 개인별 누적 포인트 관리

## ✅ 요구사항 및 체크리스트

- [x]  MySQL ≥ 5.7 사용
- [x]  테이블과 인덱스에 대한 DDL 작성
- [x]  애플리케이션으로 다음 API 제공
    - [x]  `POST /events` 로 호출하는 포인트 적립 API
    - [x]  포인트 조회 API
- 상세 요구사항
    - [x]  REST API를 제공하는 서버 애플리케이션 구현
    - [x]  Java, Kotlin, Python, JavaScript(or TypeScript) 중 언어 선택
    - [x]  Framework, Library 자유 사용, 추가 Data Storage 필요시 여러 종류 사용 가능
    - [x]  README 작성
    - [x]  테스트 케이스 작성 (Optional)

## ❓ 사용 방법

먼저 `docker compose`로 MySQL 컨테이너를 실행합니다. ([`docker-compose.yml 내용`](https://github.com/litsynp/triple-club-mileage-service/blob/main/docker-compose.yml))

```bash
$ docker compose up
```

컨테이너가 준비됐다면 스프링 애플리케이션을 빌드 후 실행합니다.

```bash
$ ./gradlew build && java -jar build/libs/mileage-service-0.0.1-SNAPSHOT.jar
```

서버가 띄워졌다면

- **리뷰 작성 이벤트 API**인 `POST http://localhost:8080/events`
    ```json
    {
        "type": "REVIEW",
        "action": "ADD",
        "reviewId": "ff35e929-fcf6-11ec-b3c2-0242ac170002",
        "userId": "31313130-3031-3131-3130-000000000000",
        "placeId": "8040a09f-fcf6-11ec-b3c2-0242ac170002",
        "content": "좋아요!",
        "attachedPhotoIds": ["48925641-70f3-4674-86e6-420bbab59bf8", "cf00ec57-563b-4f0e-b5bf-78ce28738efb"]
    }
    ```

- **사용자 포인트 총점 조회 API**인 `GET http://localhost:8080/points?user-id=31313130-3031-3131-3130-000000000000`

- 페이지네이션을 제공하는 **사용자 포인트 기록 조회 API**인 `GET http://localhost:8080/point-histories`

와 같이, API 명세에 맞게 실행해보실 수 있습니다.

- 미리 **사용자 2명, 장소 1개, 사진 2개**를 [`src/main/resources/data.sql`](https://github.com/litsynp/triple-club-mileage-service/blob/main/src/main/resources/data.sql)에 넣어 애플리케이션이 시작되면 `INSERT` 되도록 했습니다.
    - 만약 초기 데이터가 필요 없으시다면 `data.sql`을 삭제하고 진행하시면 됩니다.

## ⚙️ 주요 사용 프레임워크 / 라이브러리 및 버전

- Spring Boot 2.7.1
    - Querydsl JPA 5.0.0
    - Spring REST Docs (API 문서화)
    - p6spy (SQL logging)
- Java 11
- Gradle 7.4.1
- MySQL (8.0.29) (InnoDB)
- Docker & docker compose (MySQL 컨테이너 실행)

## 🚗 프로젝트 구조 및 아키텍처 요약

![3-tier-layered-architecture](https://user-images.githubusercontent.com/42485462/178142905-86592505-b3c5-455f-91de-7f2d38010e29.png)

출처: https://www.petrikainulainen.net/software-development/design/understanding-spring-web-application-architecture-the-classic-way/

- 프로젝트 구조는 위와 같이 3 tier layered architecture로 구현하였습니다. Web, Service, Repository로 구분하였습니다.

- **클라이언트 ↔ Controller**에 사용되는 DTO와, **Controller ↔ Service**에 사용되는 DTO를 구분하여 구현하였습니다.

- API controller 클래스는 [`api`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/api), service 클래스는 [`service`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/service), repository는 [`dao`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/dao), entity는 [`domain`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/domain) 패키지에 관심사에 따라 모아두었습니다.
- 각 계층에서 사용되는 DTO는 [`dto`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/dto) 패키지에 용도에 따라 모아두었습니다.
    - 각 DTO에는 `@NotNull`과 같은 어노테이션을 이용한 validation이 적용되어 있습니다.
- 통일된 양식의 exception handling을 위해 [`global.error`](https://github.com/litsynp/triple-club-mileage-service/tree/main/src/main/java/com/litsynp/mileageservice/global/error) 패키지에 exception handler 및 exception, error code 등을 모아두었습니다.

## 👥 SQL Schema 및 ERD

다음은 요구사항을 바탕으로 작성한 ERD입니다.

![triple-erd](https://user-images.githubusercontent.com/42485462/178138740-3f335bc5-13f7-4b0f-a634-436d72894e78.png)

스키마는 [`src/main/resources/schema.sql`](https://github.com/litsynp/triple-club-mileage-service/blob/main/src/main/resources/schema.sql) 에 작성하였습니다.

DDL Schema 작성 및 unique & foreign constraint, index 설정을 하였습니다.

DB Engine은 **InnoDB**로 사용합니다.

## 🧾 API 명세

Endpoint는 리뷰 작성, 수정, 삭제 이벤트를 전달하는 `/events`, 사용자 점수 합계를 확인하는 `/points`, 사용자 점수 기록 목록을 조회하는 `/point-histories` 로 총 3개입니다. 이벤트의 종류까지 계산하면 5개의 API가 됩니다.

API 명세는 Spring REST Docs을 이용해 테스트를 통해 문서화했습니다.

[http://localhost:8080/docs/index.html](http://localhost:8080/docs/index.html) 로 접속하면 확인하실 수 있습니다.

추가로 PDF로 제작하여 첨부합니다. [트리플 클럽 마일리지 서비스 API PDF](https://github.com/litsynp/triple-club-mileage-service/blob/main/triple-club-mileage-service-api-spec.pdf)

## 👍 Test 결과

JUnit 5, Assertj, BDDMockito, Spring REST Docs 및 MockMvc 등을 통해 유닛 테스트 및 통합 테스트를 진행하였습니다.

![image](https://user-images.githubusercontent.com/42485462/178146949-0305612d-a6d0-4969-87fe-08dc696e868b.png)

## 🏁 REMARKS 해결 방안

요구사항 문서의 Remarks에 적힌 각 문제에 대한 해결 방안입니다.

### ✅️ 한 사용자는 장소마다 리뷰를 1개만 작성할 수 있고, 리뷰는 수정 또는 삭제할 수 있다.

한 사용자가 장소마다 리뷰를 1개만 작성할 수 있는 조건은 미리 DB에 unique constraint를 걸었습니다.

```sql
alter table review
    add unique review_ak01 (user_id, place_id);
```

이후 리뷰 작성 이벤트 비즈니스 로직을 처리하는 서비스 메서드인 `ReviewService.writeReview()` 에서도 해당 장소에 대해 사용자가 작성한 리뷰가 이미 존재하는지를 확인하고, 존재한다면 `409 CONFLICT` 를 반환하도록 했습니다.

리뷰 수정 또는 삭제는 이벤트 API에서 `ADD` 외에도 `MOD` 및 `DELETE` `action`을 지원하여 해결하였습니다.

### ✅️ 리뷰 보상 점수가 존재한다.

리뷰 보상 점수는 다음과 같습니다.

```
내용 점수
- 1자 이상 텍스트 작성: 1점
- 1장 이상 사진 첨부: 1점

보너스 점수
- 특정 장소에 첫 리뷰 작성: 1점
```

서비스 메서드인 `ReviewService.writeReview()` 에서 점수를 계산하는 것으로 해결했습니다. 뒤에서 설명하고 있습니다.

### ✅ 포인트 증감이 있을 때마다 이력이 남아야 한다.

작성, 수정, 삭제 Review 이벤트를 `POST /events` API를 통해 전달할 때마다 사용자의 포인트가 변동되며, 그 기록은 시간과 함께 얼마나 변동됐는지 이력이 남습니다.

요청으로 전달된 DTO를 읽어 어떤 유형의 이벤트인지 판단하여, `ReviewService` 클래스에 정의된 `writeReview`, `updateReview`, `deleteReviewById` 메서드가 호출됩니다.

### ✅ 사용자마다 현재 시점의 포인트 총점을 조회하거나 계산할 수 있어야 한다.

각 사용자의 포인트의 총점은 `GET /points` API로 조회할 수 있습니다. Request parameter로 UUID인 사용자 ID를 전달해야 하며 (예: `GET /points?user-id=…`), 포인트의 총점을 다음과 같은 형식으로 반환합니다.

```json
{
  "userId": "<사용자 ID>",
  "points": 4
}
```

### ✅ 포인트 부여 API 구현에 필요한 SQL 수행 시, 전체 테이블 스캔이 일어나지 않는 인덱스가 필요하다.

MySQL에서 InnoDB를 사용하였으며, 외래키 설정을 하면 인덱스 설정이 됩니다. 그렇지만 추가로 외래키에도 인덱스 설정을 하였습니다.

테스트를 위한 설정은 다음과 같습니다.

- 사용자 점수 테이블인 `user_point` 에 사용자 점수 변동 기록 `100`개를 넣었습니다.

- 100개 중에서 1개의 기록만이 사용자가 보유한 점수 기록입니다.

- `schema.sql` 파일에 정의된 테이블 DDL에서 테이블을 생성할 때 `engine = InnoDB` 로 **InnoDB**로 설정해두었습니다.

- 위에서 말씀드렸다시피, MySQL InnoDB는 외래키에 대해서는 index가 자동으로 적용됩니다. 그렇지만 foreign key constraint 말고도 index도 추가로 걸었습니다.

`user_id`에 대해서 다음과 같이 index를 적용해보았습니다.

```sql
alter table user_point
    add index user_point_ak01 (user_id);
```

그리고 다음 쿼리 플랜을 확인하기 위해 query를 실행하였습니다.

```sql
explain
select coalesce(sum(amount), 0)
from user_point
where user_point.user_id = @user_id;
```

결과는 다음과 같습니다.

![select-sum-user-point-query-plan](https://user-images.githubusercontent.com/42485462/178138819-9bfad8b9-c59d-4aa6-a5e3-3a6a87deb263.png)

`type`은 `ref`, `Using index condition`으로, 인덱스를 사용해 조회하고 있으며, 전체 테이블 스캔이 일어나지 않은 것을 알 수 있습니다.

### ✅ 리뷰를 작성했다가 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수는 회수한다.

리뷰를 작성하면 `user_point` 테이블에 다음과 같은 pseudo code처럼 점수가 추가됩니다.

```python
amount = 0

# 1자 이상 텍스트 작성: 1점
if len(dto.content) > 0:
    amount = amount + 1

# 1장 이상 사진 첨부: 1점
if len(dto.attachedPhotoIds):
    amount = amount + 1

# 첫 리뷰 작성: 1점
if not exists(review where review.placeId = dto.placeId):
    amount = amount + 1

if amount > 0:
    newUserPoint = UserPoint(user, review, amount)
    save newUserPoint to user_point table
```

즉, 작성에서 발생한 점수가 1 이상일 때만 저장합니다.

`user_point` 테이블은 `review` 테이블과 FK로 연결된 테이블입니다. `ON DELETE SET NULL` 옵션으로 삭제될 때 리뷰가 삭제되더라도 사용자 점수 기록은 삭제되지 않고 FK가 `null`이 되도록 해서 기록을 유지합니다.

```sql
alter table user_point
    add constraint user_point_fk02 foreign key (review_id) references review (id) on delete set null on update cascade;
```

대신 기록이 그대로 남아있으므로 현 점수의 합산만큼 뺀 값을 차감합니다.

삭제할 때는 점수 계산 및 회수가 다음과 같이 진행됩니다.

```python
# 리뷰를 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수 회수
# 하지만 기록 유지를 위해 삭제해서는 안된다.
pointsFromReview = getUserPoints(user=review.user, review=review) # e.g.) 3

# 해당 리뷰로부터 얻은 점수를 계산하여 회수한다.
if pointsFromReview > 0L:
    # 리뷰로부터 받은 값만큼 차감하면 된다
    newUserPoint = UserPoint(user, review, amount=-pointsFromReview) # e.g) -3
    save newUserPoint to user_point table
```

총점을 계산해서 삭제하므로, 리뷰 수정 등으로 회수된 점수까지 고려해서 최종적으로 회수할 점수가 계산됩니다.

리뷰는 이후 삭제하게 됩니다.

실제로 리뷰를 다음에 작성하게 된다면, 사용자 기록은 남아 있지만 `review` 테이블에 리뷰는 없기 때문에 점수 계산이 처음 작성하는 것과 동일하게 진행됩니다.

### ✅ 리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수한다.

요구사항에 따르면 리뷰를 수정하면 다음과 같이 진행됩니다.

```
1. 글만 작성한 리뷰에 사진을 추가하면 1점을 부여
2. 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수
```

엄밀히 말하면 1, 2번 조건은 모두 글이 있어야 한다는 것을 전제로 하지만, 그렇게 한다면 2번 조건을 악용할 수 있습니다.

1. **글을 먼저 삭제**하고, **이후 사진을 삭제**하면 2번 조건의 `글과 사진이 있는 리뷰에서` 라는 전제조건을 회피해 회수를 회피할 수 있습니다.
2. 이후 **다시 글을 작성한** 뒤, **사진을 추가**하면 1번 조건의 `글만 작성한 리뷰에 사진을 추가` 조건이 반영되어 또 1점을 얻게 됩니다.

따라서 글이 있든 없든 사진이 변동된다면 점수에 변동을 주도록 했습니다.

이 점수 계산은 다음과 같이 진행했습니다.

```
1. 리뷰에 사진이 이전에 없었는지 확인한다.
2. 사진이 없었는데 사진을 1장 이상 추가했다면 1점을 부여한다.
3. 사진이 1장 이상 있었는데 사진을 모두 삭제했다면, 해당 리뷰를 통해 1점을 부여한 적이 있다면 1점을 차감한다.
```

리뷰 수정의 점수 계산은 다음과 같은 pseudo code로 구현하였습니다.

```python
# 이전에 사진이 있었는지 확인
emptyPhotosBefore = len(review.attachedPhotos) == 0

# 기존 리뷰에 저장된 사진 중, 새로 추가된 사진이 아닌 사진은 전부 삭제
review.photos.filter(photo.id not in dto.attachedPhotoIds).delete()

# 새로 추가된 사진 저장
review.photos.addAll(dto.attachedPhotoIds)

## 리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수 ##

# 글만 작성한 리뷰에 사진을 추가하면 1점을 추가
if emptyPhotosBefore and len(review.photos) != 0:
    newUserPoint = UserPoint(user, review, amount=1)
    save newUserPoint to userPoint table

# 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수
if (not emptyPhotosBefore) and len(review.photos) == 0:
    userPoints = getAllUserPoints(user.id)
    if (userPoints > 0):
        newUserPoint = UserPoint(user, review, amount=-1)
    save newUserPoint to userPoint table
```

위와 같이 글을 올리기 전의 사진의 갯수, 글을 올린 후의 사진의 갯수를 이용하여 점수를 계산했습니다.

\* **요구사항에 따르면 글을 작성한다면 1점이 추가되지만, 글이 없는 상태에서 글을 추가하거나, 글이 있는 상태에서 없도록 수정하더라도 포인트의 변화는 일어나지 않습니다.**

### ✅ 사용자 입장에서 본 ‘첫 리뷰'일 때 보너스 점수를 부여한다.

```
1. 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하고, 삭제된 이후 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여한다.
2. 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하는데, 삭제되기 이전 사용자 B가 리뷰를 남기면 사용자 B에게 점수를 부여하지 않는다.
```

1, 2를 동시에 구현하기 위해서는 **단순히 리뷰를 삭제하면 리뷰 테이블에서 삭제**하면 됩니다. 그리고 **리뷰를 작성하는 시점에** 해당 장소에 리뷰를 남긴 사람이 없는지 확인하고 점수를 계산하면 됩니다.

따라서 다음과 같이 진행됩니다.

```
1. 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하고, 삭제된 이후 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여한다.
    1. 사용자 A가 장소 P에 리뷰를 남긴다.
    2. 사용자 A가 리뷰를 삭제한다. 사용자 A의 회수 포인트가 계산되어 기록된다.
    3. 사용자 B가 장소 P에 리뷰를 남긴다. 해당 장소에 리뷰가 없으므로 보너스 1점 추가한다.

2. 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하는데, 삭제되기 이전 사용자 B가 리뷰를 남기면 사용자 B에게 점수를 부여하지 않는다.
    1. 사용자 A가 장소 P에 리뷰를 남긴다.
    2. 사용자 B가 장소 P에 리뷰를 남긴다. 이미 해당 장소에 리뷰가 있으므로 보너스 점수는 없다.
    3. 사용자 A가 리뷰를 삭제한다. 사용자 A의 회수 포인트가 계산되어 기록된다.
```
