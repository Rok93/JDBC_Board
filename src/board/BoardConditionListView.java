package board;

import java.util.ArrayList;
import java.util.Scanner;

import dao.BoardDAO;
import vo.BoardVO;

public class BoardConditionListView {

	public void input() throws Exception {

		Scanner sc = new Scanner(System.in);
		
		System.out.println("선택 가능한 컬럼명은 다음과 같습니다. ");
		System.out.println("1.제목  2.내용  3.작성자  4.조회수  5.모두");
		
		BoardDAO bd = new BoardDAO();
				
		System.out.print("번호 입력 : ");
		int inputnum = sc.nextInt(); //번호 입력 받음
		System.out.print("조회조건 : ");
		
		ArrayList<BoardVO> list = new ArrayList<BoardVO>();
		
		if(inputnum == 1) { // 1.제목
			String strInput = sc.next(); //공백도 같이 받아주지 뭐.... 충돌주의
			list = bd.getConditionList("title", strInput);
		}
		else if(inputnum == 2) { // 2.내용
			String strInput = sc.next();
			list = bd.getConditionList("contents", strInput);
		}
		else if(inputnum == 3) { // 3.작성자
			String strInput = sc.next();
			list = bd.getConditionList("writer", strInput);
		}
		else if(inputnum == 4) { // 4.조회수
			int intInput = sc.nextInt();
			list = bd.getConditionList("seq", String.valueOf(intInput));
			//주의 int타입 ->  String 타입 형변환 하여 전달하였음. BoardDAO 에서도 생각해서 처리해야함.
		}
		else if(inputnum == 5){
			String strInput = sc.next();
			list = bd.getConditionList("title-contents-writer", strInput);
			// split 메소드 이용하여 - 단위로 자르자.
		}

		System.out.println("**************************조회 시작 **************************");

		for(BoardVO vo: list) {
			//제목, 작성자,작성시간 
			System.out.println("게시물 번호: " + vo.getSeq() + " 제목: " + vo.getTitle()
			+ " 작성자: " + vo.getWriter() + " 작성시간: " + vo.getTime()
			+ " 조회수: " + vo.getViewcount());
		}
		
		System.out.println("**************************조회 종료 **************************");
	}
}
