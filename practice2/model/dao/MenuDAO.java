package com.ohgiraffers.practice2.model.dao;

import com.ohgiraffers.practice2.model.dto.MenuDTO;
import jdk.jfr.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.ohgiraffers.common.JDBCTemplate.close;
import static com.ohgiraffers.common.JDBCTemplate.getConnection;

public class MenuDAO {

    // 필드에 한번만 선언
    private Properties prop = new Properties();

    public MenuDAO() {
        try {
            prop.loadFromXML(new FileInputStream("src/main/java/com/ohgiraffers/mapper/menu-query.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public int selectLastMenuCode(Connection con) {

        // sql을 실행할 객체 생성
        Statement stmt = null;
        // 결과를 담을 rset
        ResultSet rset = null;

        int maxMenuCode = 0;

        String query = prop.getProperty("selectLastMenuCode");

        try {
            // stmt 실행 준비
            stmt = con.createStatement();
            // 결과 받기
            rset = stmt.executeQuery(query);

            // 결과에서 메뉴코드 마지막을 가져옴
            if (rset.next()) {
                maxMenuCode = rset.getInt("MAX(A.MENU_CODE)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(stmt);
            close(rset);
        }
        return maxMenuCode;


    }

    public List<Map<Integer, String>> selectAllCategory(Connection con) {
        Statement stmt = null;
        ResultSet rset = null;

        List<Map<Integer, String>> categoryList = null;

        String query = prop.getProperty("selectAllCategoryList");

        try {
             // SQL 실행 준비
            stmt = con.createStatement();
            // 결과 받기
            rset = stmt.executeQuery(query);

            categoryList = new ArrayList<>();

            while (rset.next()) {
                Map<Integer, String> category = new HashMap<>();
                category.put(rset.getInt("CATEGORY_CODE"), rset.getString("CATEGORY_NAME"));

                categoryList.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rset);
            close(stmt);
        }
        return categoryList;


    }


    public int insetNewMenu(MenuDTO newMenu, Connection con) {

        PreparedStatement pstmt = null;

        int result = 0;

        String query = prop.getProperty("insertMenu");

        try {
            pstmt = con.prepareStatement(query);

            pstmt.setInt(1, newMenu.getCode());
            pstmt.setString(2, newMenu.getName());
            pstmt.setInt(3, newMenu.getPrice());
            pstmt.setInt(4, newMenu.getCategoryCode());
            pstmt.setString(5, newMenu.getOrderableStatus());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(pstmt);
        }
        return result;
    }
}
