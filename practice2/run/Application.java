package com.ohgiraffers.practice2.run;

import com.ohgiraffers.practice2.model.dao.MenuDAO;
import com.ohgiraffers.practice2.model.dto.MenuDTO;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.ohgiraffers.common.JDBCTemplate.getConnection;

public class Application {

    public static void main(String[] args) {

        Connection con = getConnection();

        MenuDAO registDAO = new MenuDAO();

        // 1번 : 마지막 메뉴코드 조회
        int maxMenuCode = registDAO.selectLastMenuCode(con);

        System.out.println("maxMenuCode : " + maxMenuCode);

        // 2번 : 카테고리 리스트
        List<Map<Integer, String>> categoryList = registDAO.selectAllCategory(con);

        System.out.println(categoryList);

        // 3번 : 메뉴 추가
        // 3-1 스캐너
        Scanner sc = new Scanner(System.in);
        System.out.println("등록할 메뉴의 이름을 입력하세요. : ");
        String menuName = sc.nextLine();

        System.out.println("등록할 메뉴의 가격을 입력하세요. : ");
        int menuPrice = sc.nextInt();

        System.out.println("카테고리를 선택해주세요. (식사, 음료, 디저트, 한식) : ");
        sc.nextLine();  // 버퍼 아웃
        String categoryName = sc.nextLine();

        System.out.println("판매 여부를 선택해주세요. (예, 아니오) : ");
        String answer = sc.nextLine();

        int menuCode = maxMenuCode + 1;

        // 3-2) 스위치
        int categoryCode = 0;
        switch (categoryName) {
            case "식사":
                categoryCode = 1;
                break;
            case "음료":
                categoryCode = 2;
                break;
            case "디저트":
                categoryCode = 3;
                break;
            case "한식":
                categoryCode = 4;
                break;
        }

        String orderableStatus = "";
        switch (answer) {
            case "예":
                orderableStatus = "y";
                break;
            case "아니오":
                orderableStatus = "n";
                break;
        }

        MenuDTO newMenu = new MenuDTO(menuCode, menuName, menuPrice, categoryCode, orderableStatus);

        // 3-3) 신규 메뉴 등록을 위한 메소드 호출 -> 등록
        int result = registDAO.insetNewMenu(newMenu, con);

        if (result > 0) {
            System.out.println("메뉴 등록 성공!");
        } else {
            System.out.println("메뉴 등록 실패!");
        }


    }
}