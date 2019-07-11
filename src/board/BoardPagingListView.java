package board;

import java.util.ArrayList;
import java.util.Scanner;

import dao.BoardDAO;
import vo.BoardVO;

//최종적으로 View 붙는 클래스의 상위 추상 클래스 View 를 만들어서 상속 받게하여 input 메소드 오버라이드 하는 방법 써보기
public class BoardPagingListView {


	public void input() throws Exception{
		
		Scanner sc = new Scanner(System.in);
		System.out.print("페이지 번호 : ");
		int pagenum = sc.nextInt();
		System.out.print("1페이지당 출력할 게시물갯수 : ");
		int listPerPage = sc.nextInt();
		
		//BoardDAO.getPageList(int, int)
		//ArrayList 리턴
		BoardDAO bd = new BoardDAO();
		ArrayList<BoardVO> list = bd.getPageList(pagenum, listPerPage);
		
		System.out.println("*************조회 시작 *************");
		
		for(BoardVO vo: list) {
			//제목, 작성자,작성시간 
			System.out.println("제목 : " + vo.getTitle()
			+ " 작성자 : " + vo.getWriter() + " 작성시간 : " + vo.getTime());
		}
		System.out.println("*************조회 종료 *************");
		//상세조회
				BoardDetailView bdv = new BoardDetailView();
				bdv.input();
	}
	
	public void view() {
		
	}
}
