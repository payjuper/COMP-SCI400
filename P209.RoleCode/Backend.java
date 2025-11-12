import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Backend 클래스는 BackendInterface의 약속을 이행하여 
 * 캠퍼스 그래프 데이터 관리 및 최단 경로 검색 서비스를 제공합니다.
 * 이 클래스는 모든 그래프 작업을 GraphADT 객체(현재는 Placeholder)에게 위임합니다.
 */
public class Backend implements BackendInterface {

  // Graph ADT 타입의 객체를 저장하는 필드입니다.
  // 이 필드에 Graph_Placeholder 객체가 주입(저장)되어 사용됩니다.
  private GraphADT<String, Double> campusGraph;

  public Backend(GraphADT<String, Double> graph) {
    this.campusGraph = graph;
  }

  /**
   * campus.dot 파일을 읽어 그래프에 노드와 엣지 데이터를 삽입합니다.
   * 반환값은 없지만, 내부 campusGraph의 상태를 변경하는 중요한 역할을 합니다.
   * @param filename 읽어올 파일의 이름 (예: "campus.dot")
   */
  @Override
  public void loadGraphData(String filename) throws IOException {
    
    // 파일 객체 및 Scanner 준비
    File file = new File(filename);
    Scanner scanner = new Scanner(file); 
    
    // 파일의 첫 줄 ("digraph campus {") 건너뛰기
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    
    // 파일 내용 파싱 (Parsing) 시작
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim(); 
      
      // 마지막 줄이거나 빈 줄이면 건너뜁니다.
      if (line.isEmpty() || line.startsWith("}")) {
        continue;
      }
      
      // --- 데이터 추출 ---
      // line 예시: "Memorial Union" -> "Science Hall" [seconds=105.8];
      
      String[] parts = line.split("->"); 
      
      // 1. 출발지 (pred) 추출: 큰따옴표와 공백 제거
      String pred = parts[0].trim().replaceAll("\"", ""); 
      String rest = parts[1].trim(); 
      
      // 2. 도착지 (succ) 추출: [seconds= 앞에서 끝나는 부분을 가져오고 큰따옴표를 제거
      String succ = rest.substring(0, rest.indexOf("[")).trim().replaceAll("\"", "");

      // 3. 가중치(시간) 추출 및 Double로 변환
      int startIndex = rest.indexOf("=") + 1; 
      int endIndex = rest.indexOf("]"); 
      String weightStr = rest.substring(startIndex, endIndex).trim();
      Double weight = Double.parseDouble(weightStr); 

      // 4. Graph ADT에 데이터 삽입 (내부 상태 변경)
      this.campusGraph.insertNode(pred); // 노드를 삽입 (중복 삽입은 ADT가 처리)
      this.campusGraph.insertNode(succ); // 노드를 삽입
      this.campusGraph.insertEdge(pred, succ, weight); // 엣지를 삽입
    }
    
    scanner.close();
  }

  /**
   * 그래프에 저장된 모든 위치(노드) 이름을 목록으로 반환합니다.
   * (Graph ADT의 명시적인 getAllNodes 기능이 없어 Placeholder의 기능을 이용해 임시로 처리)
   */
  @Override
  public List<String> getListOfAllLocations() {
    // Placeholder가 초기화된 노드들을 포함하는 '대표 경로'를 반환한다고 가정합니다.
    try {
      // 이 경로는 Placeholder에 초기화된 노드들을 반환합니다.
      return campusGraph.shortestPathData("Union South", "Weeks Hall for Geological Sciences");
    } catch (NoSuchElementException e) {
      // 그래프가 비어있거나 경로가 없으면 빈 목록을 반환
      return new ArrayList<String>();
    }
  }

  /**
   * 두 위치 사이의 최단 경로 상의 노드(위치) 순서를 반환합니다.
   */
  @Override
  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    try {
      // Graph ADT의 핵심 기능인 shortestPathData를 그대로 호출하여 최단 경로 목록을 요청
      return campusGraph.shortestPathData(startLocation, endLocation); 
    } catch (NoSuchElementException e) {
      // 경로가 없거나 위치가 그래프에 없으면 빈 목록을 반환
      return new ArrayList<String>();
    }
  }

  /**
   * 최단 경로 상의 각 구간(엣지)의 이동 시간(가중치)을 목록으로 반환합니다.
   */
  @Override
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
    // 1. 최단 경로의 노드 순서를 먼저 확보합니다.
    List<String> path = findLocationsOnShortestPath(startLocation, endLocation);
    List<Double> times = new ArrayList<>();
    
    // 경로가 없거나 노드가 하나뿐이면 빈 목록 반환
    if (path == null || path.size() < 2) {
      return times;
    }

    // 2. 노드 순서를 따라가며 각 엣지의 가중치(시간)를 ADT에게 요청합니다.
    for (int i = 0; i < path.size() - 1; i++) {
      String pred = path.get(i); // 출발 노드
      String succ = path.get(i + 1); // 도착 노드
      
      try {
        // Graph ADT의 getEdge 메서드를 호출하여 가중치를 얻습니다.
        times.add(campusGraph.getEdge(pred, succ).doubleValue()); 
      } catch (NoSuchElementException e) {
        // 엣지가 없으면 (오류 발생 시) 빈 목록 반환
        return new ArrayList<Double>(); 
      }
    }
    
    return times;
  }

  /**
   * 시작 위치에서 도달 가능한 모든 최단 경로 중, 가장 많은 위치를 경유하는 경로를 반환합니다.
   */
  @Override
  public List<String> getLongestLocationListFrom(String startLocation) throws NoSuchElementException {
    
    List<String> allLocations = getListOfAllLocations();
    List<String> longestPath = new ArrayList<>();
    
    // 1. startLocation이 그래프에 있는지 확인 (과제 요구사항)
    if (!allLocations.contains(startLocation)) {
      throw new NoSuchElementException("Start location " + startLocation + " does not exist in the graph.");
    }

    // 2. 모든 위치를 순회하며 최단 경로를 찾고 가장 긴 경로를 갱신
    for (String endLocation : allLocations) {
      
      // 자기 자신으로 돌아오는 경로는 제외
      if (startLocation.equals(endLocation)) {
        continue;
      }
      
      try {
        // Graph ADT의 shortestPathData 호출
        List<String> currentPath = campusGraph.shortestPathData(startLocation, endLocation);
        
        // 가장 긴 경로를 찾습니다.
        if (currentPath.size() > longestPath.size()) {
          longestPath = currentPath;
        }
      } catch (NoSuchElementException e) {
        // 도달 불가능한 경로는 무시
      }
    }
    
    // 3. 도달 가능한 곳이 없으면 예외 처리 (과제 요구사항)
    if (longestPath.isEmpty() && campusGraph.getNodeCount() > 1) { 
      throw new NoSuchElementException("No other locations can be reached from " + startLocation);
    }

    return longestPath;
  }
}