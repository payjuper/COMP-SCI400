import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// 테스트 대상 클래스와 Placeholder를 사용하기 위해 import 합니다.

public class BackendTests {

    // Helper 메서드: 테스트마다 Backend 객체를 생성하고 파일을 로드하여 준비합니다.
    private Backend setupTestBackend() {
        // 1. Graph ADT를 구현한 Placeholder 객체 생성
        GraphADT<String, Double> placeholder = new Graph_Placeholder();
        
        // 2. Backend 객체에 Placeholder를 주입하여 생성 (의존성 주입)
        Backend backend = new Backend(placeholder);

        // 3. 파일 데이터를 로드 (loadGraphData 호출 - 5가지 Backend 메서드 중 1개 호출)
        try {
            backend.loadGraphData("campus.dot");
        } catch (IOException e) {
            // 파일 로드 실패는 테스트 실패로 간주
            fail("Failed to load graph data from campus.dot: " + e.getMessage());
        }
        return backend;
    }
    
    /**
     * roleTest1: Backend의 loadGraphData가 파일을 성공적으로 로드하는지 확인하고,
     * getListOfAllLocations 메서드를 호출하여 노드 목록을 정상적으로 반환하는지 검증합니다.
     * (Backend 메서드 호출: loadGraphData, getListOfAllLocations)
     */
    @Test
    public void roleTest1() {
        Backend backend = setupTestBackend();

        // 1. getListOfAllLocations 호출 (5개 중 1개)
        List<String> locations = backend.getListOfAllLocations();
        
        // 2. Placeholder에 의해 반환된 위치 목록의 크기가 3개 이상인지 확인
        assertTrue(locations.size() >= 3, "Backend should return at least 3 locations from the Placeholder's path.");
        
        // 3. 목록에 Placeholder의 기본 위치가 포함되어 있는지 확인
        assertTrue(locations.contains("Union South"), "Locations list must contain 'Union South'.");
    }

    /**
     * roleTest2: 최단 경로를 찾는 findLocationsOnShortestPath와
     * 해당 경로의 엣지 가중치를 찾는 findTimesOnShortestPath의 정확성을 검증합니다.
     * (Backend 메서드 호출: findLocationsOnShortestPath, findTimesOnShortestPath)
     */
    @Test
    public void roleTest2() {
        Backend backend = setupTestBackend();

        // Placeholder 경로에 의존한 테스트 시작점/도착점 설정
        String start = "Union South";
        String end = "Weeks Hall for Geological Sciences";
        
        // 1. findLocationsOnShortestPath 호출 (5개 중 2개)
        List<String> path = backend.findLocationsOnShortestPath(start, end);
        
        // 경로가 3개 노드를 포함하는지 확인
        assertEquals(3, path.size(), "Shortest path should contain 3 locations based on Placeholder.");

        // 2. findTimesOnShortestPath 호출 (5개 중 3개)
        List<Double> times = backend.findTimesOnShortestPath(start, end);

        // 시간 목록의 크기 확인 (3개 노드 사이에 2개 엣지)
        assertEquals(2, times.size(), "Times list should contain 2 edge weights.");
        
        // 3. Placeholder의 getEdge 구현에 따른 가중치 (1.0, 2.0으로 예상) 확인
        assertEquals(1.0, times.get(0), 0.001, "First edge weight should be 1.0.");
    }

    /**
     * roleTest3: getLongestLocationListFrom 메서드가 가장 긴 최단 경로를 올바르게 찾는지 확인하고,
     * 존재하지 않는 위치에 대해 NoSuchElementException을 올바르게 던지는지 검증합니다.
     * (Backend 메서드 호출: getLongestLocationListFrom)
     */
    @Test
    public void roleTest3() {
        Backend backend = setupTestBackend();
        String start = "Union South";

        // 1. getLongestLocationListFrom 호출 (5개 중 4개)
        List<String> longestPath = backend.getLongestLocationListFrom(start);

        // 경로 길이가 3인지 확인 (Placeholder에 의존)
        assertEquals(3, longestPath.size(), "Longest path from Union South should be the full 3-node path.");

        // 2. 존재하지 않는 위치에 대한 예외 처리 확인 (5개 중 5개)
        String nonExistentLocation = "NonExistentPlace";
        
        // Assertions.assertThrows를 사용하여 NoSuchElementException을 발생시키는지 확인
        assertThrows(NoSuchElementException.class, () -> {
            backend.getLongestLocationListFrom(nonExistentLocation);
        }, "Calling getLongestLocationListFrom with a non-existent start location must throw NoSuchElementException.");
    }
}