# GoogleSheetsRepository 가이드

### GoogleSheetsRepository는 구글시트를 자바 객체지향 코드에 적용하기 위해 Data를 Mapping 하는 기능을 제공합니다.

- googleSheetsConnection  
GoogleSheetsConnection 을 주입받아야 합니다.  

- 엔티티 매핑  
구글 시트의 시트이름과 매핑하고자 하는 엔티티 클래스의 이름은 동일해야 합니다.(대소문자 구별)  
또한 해당 클래스 필드의 이름과 시트의 각 컬럼 이름도 동일해야 합니다.(대소문자 구별)    
필드의 타입과 셀 데이터 타입이 일치하지 않으면 예외를 던집니다.  
id 필드는 int(최대 200만셀이므로)로 선언해야하고 필수로 존재해야 합니다.(시트의 컬럼도 마찬가지)  
엔티티 클래스의 생성자는 NoArgsConstructor가 존재해야 합니다.  
getter와 setter는 보편적인 방식대로 네이밍(get/set + 필드이름:첫글자대문자)  

- repository생성 및 사용  
`@Repository
public class SessionRepository extends GoogleSheetsRepository<Session> {
}`   
...  
`List<Session> sessionList = sessionRepository.getAll();`  
추가하고자 하는 메소드가 있으면 repository에 추가

- 지원하는 필드의 타입  
int  
LocalDateTime(포맷: yyyy-MM-dd HH:mm)  
String

- 조인(LeftJoin만 가능)  
조인 하고자 하는 필드에  
`@LeftJoin(joincolumn, targetClass)` 어노테이션을 추가한다.  
해당 필드는 List<?> 타입이어야한다.  
조인하려고 하는 targetClass의 repository component도 존재해야한다.  
지연 로딩 기능 없이 leftjoin이 있으면 조회시에 한꺼번에 조회  
객체 그래프 탐색 가능  

### 고민거리

- Generics를 사용해서 런타임시에는 typeEraser로 인해 Generics를 통한 리플렉션을 사용할 수 없다.  
방법을 찾아보니 구글에서 제공하는 typeToken을 통해 런타임시에도 Generics 정보를 사용할 수 있었다.  
하지만 안정성을 보장할 수는 없는 것 같다.  
가능한 해결방안으로는 빈의 생성자에 class 변수를 넘겨주는 방안을 생각해보았다.(하지만 이건 사용자입장에서 복잡해짐)

- 현재 repository가 생성되면 @postconstruct를 통해 generics에 해당하는 엔티티의 시트 데이터를 모두 불러와서  
엔티티 리스트로 변환한 것을 repository 빈에서 공유되도록 구현해두었다.  
이렇게 구현한 이유는 조회 메소드가 새로 생겨날 때마다 connection을 통해 sheet 데이터를 매번 가져오는게 비용이 크다고 생각  
하지만 이렇게 구현했을 때 데이터 양이 많아지면 메모리 비용이 커질거라고 생각됨

- 조인의 시간복잡도  
현재 leftJoin의 시간복잡도는 n * m이다.(getAll로 전체 데이터를 조회할 때)  
join column과 같은지 모두 비교해야하기 때문임  
인덱스와 비슷한 기능을 구현하면 빨라질 수 있을까  

### 추가 구현 가능한 사항  
- duplicated Id 방지
- 인덱스기능
