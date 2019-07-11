package board;

import java.util.Scanner;

public class BoardMain {

	public static void main(String[] args) {
		try {
			while(true) {

				System.out.println("---게시판 프로젝트---");
				System.out.println("1. 게시물 글쓰기");
				System.out.println("2. 페이지별 글조회");
				System.out.println("3. 조건별 글조회");
				System.out.println("4. 회원별 글조회");
				System.out.println("5. 게시판 프로젝트 종료");
				System.out.print("번호 입력 : ");

				Scanner sc = new Scanner(System.in);
				int menu = sc.nextInt();
				if(menu == 5 ) {
					System.out.println("게시판 프로젝트 종료합니다.");
					System.exit(0);
				}
				else if(menu == 1) { // 1. 글쓰기 (로그인)
					BoardInsertView view = new BoardInsertView();
					view.input();
				}
				else if(menu == 2) { // 2. 페이지 조회
					BoardPagingListView pagingview = new BoardPagingListView();
					pagingview.input();
					
				}
				else if(menu == 3) { // 3. 조건별 조회
					BoardConditionListView conditionview = new BoardConditionListView();
					conditionview.input();
				}
				else if(menu == 4) { // 4. 회원별 조회
					BoardMemberListView memberview = new BoardMemberListView();
					memberview.input();
				}
				//메뉴 추가 고려해서 일단 4번까지 전부 else if 로 처리
			}
		}catch(Exception e){
			e.printStackTrace();
			//예외처리 일단 대충해놨음 나중에 더 디테일하게 예외처리하기
		}finally {
			
		}
	}

}
