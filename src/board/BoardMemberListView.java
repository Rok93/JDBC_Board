package board;

import java.util.ArrayList;
import java.util.Scanner;

import dao.BoardDAO;
import vo.BoardVO;

public class BoardMemberListView {

	public void input() throws Exception {

		Scanner sc = new Scanner(System.in);
		System.out.print("조회할 회원의 이름을 입력하세요: ");
		String name = sc.next();

		BoardDAO bd = new BoardDAO();	
		ArrayList<BoardVO> list = bd.getMemberWritingList(name);
		
		System.out.println("**************************조회 종료 **************************");

		for(BoardVO vo: list) {
			//제목, 작성자,작성시간 
			System.out.println(" 제목: " + vo.getTitle()
			+ " 작성자: " + vo.getWriter() + " 작성시간: " + vo.getTime()
			+ " 조회수: " + vo.getViewcount());
		}
		
		System.out.println("**************************조회 종료 **************************");
		
		//상세조회
		BoardDetailView bdv = new BoardDetailView();
		bdv.input();
	}
}

