package com.ohgiraffers.section01.problem;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.ohgiraffers.common.JDBCTemplate.close;
import static com.ohgiraffers.common.JDBCTemplate.getConnection;

public class pracatice1 {

    public static void main(String[] args) {

        // 1. menuCode 마지막 번호 조회
        // 2. categoryCode에 해당하는 categoryName 조회
        // 3. menuCode 마지막 번호 + 1, categoryName으로 신규 메뉴 추가

        Connection con = getConnection();

        // connection 성공 여부
        System.out.println(con);

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;

        // 1번, 2번
        ResultSet rset1 = null;
        ResultSet rset2 = null;

        // 3번
        int result = 0;

        // --------------------------

        // 1번 :
        int maxMenuCode = 0;

        // 2번 : 카테고리 리스트 조회
        /* categoryList
              ├─ Map<Integer, String> → { 1="식사", 2="음료" }
              ├─ Map<Integer, String> → { 3="디저트", 4="한식" }
              └─ Map<Integer, String> → { 5="중식" }
              위와 같은 형태로 담겨있음
        * */
        // map<키,값>
        List<Map<Integer, String>> categoryList = null;

        // --------------------------

        Properties prop = new Properties();

        try {
            prop.loadFromXML(new FileInputStream("src/main/java/com/ohgiraffers/mapper/menu-query.xml"));

            // sql을 문자열로 읽어서 query1에 넣기
            String query1 = prop.getProperty("selectLastMenuCode");
            String query2 = prop.getProperty("selectAllCategoryList");
            String query3 = prop.getProperty("insertMenu");

            // DB 커넥션 (= con)을 통해, SQL을 실행할 객체 (= pstmt1)을 만들고,
            // 그 객체에 미리 SQL을 넣어서 준비시켜둠
            pstmt1 = con.prepareStatement(query1);
            pstmt2 = con.prepareStatement(query2);
            pstmt3 = con.prepareStatement(query3);

            // rset1에 조회 결과 담기
            // executeQuery() : 조회 / executeUpdate() : 추가, 수정, 삭제 ...
            rset1 = pstmt1.executeQuery();
            rset2 = pstmt2.executeQuery();

            // rset1.next() : ResultSet에서 다음 행으로 이동 -> 데이터가 있으면 true로 반환
            if (rset1.next()) {
                // MENU_CODE 중 가장 큰 값을 int형으로 가져와 maxMenuCode에 담음
                maxMenuCode = rset1.getInt("MAX(A.MENU_CODE)");
            }
                System.out.println("maxMenuCode = " + maxMenuCode);

            // 2번 : 카테고리 조회
            // 카테고리리스트 (List) 생성
            categoryList = new ArrayList<>();

            while (rset2.next()) {

                // 카테고리 (Map) 생성
                Map<Integer, String> category = new HashMap<>();
                // 생성한 Map에 int,String으로 키값 담기
                category.put(rset2.getInt("CATEGORY_CODE"), rset2.getString("CATEGORY_NAME"));
                // List에 위의 map을 추가
                categoryList.add(category);
            }
            System.out.println("categoryList = " + categoryList);

            // 3번 : 메뉴 추가
            Scanner sc = new Scanner(System.in);
            System.out.print("등록할 메뉴의 이름을 입력하세요. : ");
            String menuName = sc.nextLine();
            System.out.print("등록할 메뉴의 가격을 입력하세요. : ");
            int menuPrice = sc.nextInt();
            System.out.print("카테고리를 선택해주세요. (식사, 음료, 디저트, 한식, 퓨전) : ");
            // 버퍼
            sc.nextLine();
            String categoryName = sc.nextLine();
            System.out.print("바로 판매 메뉴에 적용하시겠습니가? (예, 아니오) : ");
            String answer = sc.nextLine();

            int categoryCode = 0;
            switch (categoryName) {
                case "식사" : categoryCode = 1; break;
                case "음료" : categoryCode = 2; break;
                case "디저트" : categoryCode = 3; break;
                case "한식" : categoryCode = 4; break;
                case "퓨전" : categoryCode = 7; break;
            }

            String orderableStatus = "";
            switch (answer) {
                case "예" : orderableStatus = "y"; break;
                case "아니오" : orderableStatus = "n"; break;
            }

            // 쿼리에 담기
            pstmt3.setInt(1, maxMenuCode+1);
            pstmt3.setString(2, menuName);
            pstmt3.setInt(3, menuPrice);
            pstmt3.setInt(4, categoryCode);
            pstmt3.setString(5, orderableStatus);

            // result에 결과 업데이트
            result = pstmt3.executeUpdate();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rset1);
            close(rset2);
            close(pstmt1);
            close(pstmt2);
            close(pstmt3);
            close(con);
        }

        if (result > 0) {
            System.out.println("메뉴 등록 성공!");
        } else {
            System.out.println("메뉴 등록 실패!");
        }


    }
}
