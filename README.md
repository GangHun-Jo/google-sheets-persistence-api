# GoogleSheetsRepository 가이드

### GoogleSheetsRepository는 구글시트를 자바 객체지향 코드에 적용하기 위해 Data를 Mapping 하는 기능을 제공합니다.

### GoogleSheetsRepository가 필요한 경우
- 어드민 페이지를 만들 시간이 없을 때
- 운영팀이 DB에 대한 지식이 부족한데, 데이터에 직접 접근할 필요가 많은 서비스를 개발할 때
- DB에 저장하기에는 데이터 양이 적은 경우(DB를 운영하는 비용이 부담스러울 때)

### 사용방법 및 샘플
- 엔티티 매핑  
엔티티 클래스는 GPAEntity 클래스를 상속해야 합니다.(rowNum)  
구글 시트의 시트이름과 매핑하고자 하는 엔티티 클래스의 이름은 동일해야 합니다.(대소문자 구별)  
또한 해당 클래스 필드의 이름과 시트의 각 컬럼 이름도 동일해야 합니다.(대소문자 구별)    
필드의 타입과 셀 데이터 타입이 일치하지 않으면 예외를 던집니다.  
엔티티 클래스의 생성자는 NoArgsConstructor가 존재해야 합니다.  
getter와 setter는 보편적인 방식대로 네이밍(get/set + 필드이름:첫글자대문자)  
![image](https://media.oss.navercorp.com/user/16792/files/25941d00-c6b7-11ea-864e-28710db38380)  
![image](https://media.oss.navercorp.com/user/16792/files/61c77d80-c6b7-11ea-9ade-037769db1eb7)

- repository생성 및 사용  
Repository 이름은 엔티티클래스이름 + "Repository"
추가하고자 하는 메소드가 있으면 repository에 추가
![image](https://media.oss.navercorp.com/user/16792/files/99cec080-c6b7-11ea-86c7-b4992f53bb6c)

- 지원하는 필드의 타입  
int  
LocalDateTime(Format: yyyy-MM-dd HH:mm)  
String

- 조인(LeftJoin만 가능)  
조인 하고자 하는 필드에  
`@LeftJoin(joincolumn, targetClass)` 어노테이션을 추가한다.  
해당 필드는 List<?> 타입이어야한다.  
조인하려고 하는 targetClass의 repository component도 존재해야한다.  
지연 로딩 기능 없이 leftjoin이 있으면 조회시에 한꺼번에 조회  
객체 그래프 탐색 가능  
![image](https://media.oss.navercorp.com/user/16792/files/af43ea80-c6b7-11ea-9a55-9687b399e5e2)

- 쿼리용 클래스를 통한 조건절 조회  
![image](https://media.oss.navercorp.com/user/16792/files/e4635b80-dd8f-11ea-82d3-3607fdfa1a84)  
쿼리용 클래스를 생성하려면 빌드 툴에 annotation processor를 등록해야 한다.  

- pageRequest를 통한 페이징 처리  
![image](https://media.oss.navercorp.com/user/16792/files/a6b58400-e2f5-11ea-9f6a-ce731122aa05)  
결과 값  
![image](https://media.oss.navercorp.com/user/16792/files/e8dec580-e2f5-11ea-9584-fd38d54692e0)  

- 구현 메소드  
1) add(T instance)  
2) delete(T instance)  
3) update(T instance)  
4) getByRowNum(int rowNum)  
5) getAll()
6) getAll(GPAPageRequest pageRequest)
7) selectWhere(Predicate<T> condition)
8) selectWhere(Predicate<T> condition, GPAPageRequest pageRequest)
7) selectOneWhere(Predicate<T> condition)
  
### Reference
- 구글 시트 데이터 저장  
빈이 생성되는 시점에 구글 시트에 해당 엔티티의 모든 데이터를 불러와서 파싱 후 저장한다
따라서 getAll() 메소드를 실행하면 시트에 request를 보내지 않고 저장된 데이터를 반환한다.

- 연관관계 조회  
LeftJoin이 걸려있는 필드가 존재한다면 데이터를 불러올 때 LeftJoin으로 연관된 모든 엔티티 데이터도 한꺼번에 조회한다.(즉, 지연로딩 없이 즉시로딩만 지원)

- 연관관계 수정  
현재 join-column은 각 엔티티의 rowNum을 id로 하여 조인한다.
따라서 연관관계를 수정하려면 joinColumn의 값을 수정하고 update 메소드를 수행하면 된다.
또한 객체 지향의 관점에서 봤을 때 LeftJoin으로 저장된 collection에서도 해당 객체를 추가하거나 수정/삭제 해주는게 맞다.  
![image](https://media.oss.navercorp.com/user/16792/files/d995a800-c6b7-11ea-8df1-b374653f2adf)
