# 로컬 세팅하기
1. 우선 `generate-query-class` 모듈에서 gradle의 publish to maven local을 실행한다.  
로컬 메이븐 저장소에 해당 프로젝트가 배포될 것이다.  
아래에서 이 프로젝트를 사용한다.  
(프로젝트를 분리한 이유는 annotation processor 때문)  

2. google-sheets-persistence-api의 코드 실행
build 결과물 중에 generated > sources아래 엔티티가 저장된 패키지를 보면  
Query... Class가 생성된 것을 확인할 수 있다.  
![image](https://user-images.githubusercontent.com/47145210/116775785-e301cc80-aa9f-11eb-906a-aab57c510dc0.png)
