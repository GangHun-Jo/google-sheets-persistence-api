# GoogleSheetsRepository 가이드

### GoogleSheetsRepository는 구글시트를 자바 객체지향 코드에 적용하기 위해 Data를 Mapping 하는 기능을 제공합니다.

### GoogleSheetsRepository가 필요한 경우
- 어드민 페이지를 만들 시간이 없을 때
- 운영팀이 DB에 대한 지식이 부족한데, 데이터에 직접 접근할 필요가 많은 서비스를 개발할 때
- DB에 저장하기에는 데이터 양이 적은 경우(DB를 운영하는 비용이 부담스러울 때)

### 로컬 세팅
https://github.com/GangHun-Jo/google-sheets-persistence-api/blob/master/LOCAL_SETTING.md  
  
### 사용방법 및 샘플
- 엔티티 매핑  
엔티티 클래스는 GPAEntity 클래스를 상속해야 합니다.(rowNum)  
구글 시트의 시트이름과 매핑하고자 하는 엔티티 클래스의 이름은 동일해야 합니다.(대소문자 구별)  
또한 해당 클래스 필드의 이름과 시트의 각 컬럼 이름도 동일해야 합니다.(대소문자 구별)    
필드의 타입과 셀 데이터 타입이 일치하지 않으면 예외를 던집니다.  
엔티티 클래스의 생성자는 NoArgsConstructor가 존재해야 합니다.  
getter와 setter는 보편적인 방식대로 네이밍(get/set + 필드이름:첫글자대문자)  
![image](https://user-images.githubusercontent.com/47145210/116774955-0f671a00-aa9b-11eb-8b75-c851ef181a7f.png). 
![image](https://user-images.githubusercontent.com/47145210/116774956-12620a80-aa9b-11eb-856e-349ccb5b4752.png). 

- repository생성 및 사용  
Repository 이름은 엔티티클래스이름 + "Repository"
추가하고자 하는 메소드가 있으면 repository에 추가
![image](https://user-images.githubusercontent.com/47145210/116774958-14c46480-aa9b-11eb-83c2-114a625ce498.png). 

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
![image](https://user-images.githubusercontent.com/47145210/116774962-1beb7280-aa9b-11eb-891a-4d561da5b3f1.png). 

- 쿼리용 클래스를 통한 조건절 조회  
![image](https://user-images.githubusercontent.com/47145210/116774964-1db53600-aa9b-11eb-8a35-13004e1fa74f.png). 
쿼리용 클래스를 생성하려면 빌드 툴에 annotation processor를 등록해야 한다.  

- pageRequest를 통한 페이징 처리  
![image](https://user-images.githubusercontent.com/47145210/116774969-20179000-aa9b-11eb-9ef9-e94455bf1994.png). 
결과 값  
![image](https://user-images.githubusercontent.com/47145210/116775018-5e14b400-aa9b-11eb-87d9-39e50d748d78.png). 


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
  
### 참고
- 구글 시트 데이터 저장  
빈이 생성되는 시점에 구글 시트에 해당 엔티티의 모든 데이터를 불러와서 파싱 후 저장한다
따라서 getAll() 메소드를 실행하면 시트에 request를 보내지 않고 저장된 데이터를 반환한다.

- 연관관계 조회  
LeftJoin이 걸려있는 필드가 존재한다면 데이터를 불러올 때 LeftJoin으로 연관된 모든 엔티티 데이터도 한꺼번에 조회한다.(즉, 지연로딩 없이 즉시로딩만 지원)

- 연관관계 수정  
현재 join-column은 각 엔티티의 rowNum을 id로 하여 조인한다.
따라서 연관관계를 수정하려면 joinColumn의 값을 수정하고 update 메소드를 수행하면 된다.
또한 객체 지향의 관점에서 봤을 때 LeftJoin으로 저장된 collection에서도 해당 객체를 추가하거나 수정/삭제 해주는게 맞다.  
![image](https://user-images.githubusercontent.com/47145210/116774971-23128080-aa9b-11eb-862a-29b61bf6503c.png)
