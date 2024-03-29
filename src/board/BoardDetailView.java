package board;

import java.util.ArrayList;
import java.util.Scanner;

import dao.BoardDAO;
import vo.BoardVO;

public class BoardDetailView {

	public void input() throws Exception{

		Scanner sc = new Scanner(System.in);
		System.out.print("상세조회 글번호 입력: ");
		int seq = sc.nextInt();

		BoardDAO bd = new BoardDAO();
		ArrayList<BoardVO> list = bd.getBoard(seq);

		System.out.println("**************************조회 종료 **************************");

		for(BoardVO vo: list) {
			//제목,내용,작성자,작성시간,조회수 출력되야함.   상세조회라 1개만 조회되야한다.
			System.out.println(" 글번호: " + vo.getSeq());
			System.out.println(" 제목: " + vo.getTitle());
			System.out.println(" 내용: " + vo.getContents());
			System.out.println(" 작성자: " + vo.getWriter());
			System.out.println(" 작성시간: " + vo.getTime());
			System.out.println(" 조회수: " + vo.getViewcount());
			System.out.println();
		}
		System.out.println("21.글수정 ");
		System.out.println("22.글삭제 ");
		System.out.println("23.첫화면 ");

		//여기까지는 정상적으로 작동함.
		while(true) {
			boolean check;
			System.out.print("번호를 입력하세요 : ");
			int input = sc.nextInt();
			if(input == 21) {
				//글수정 (제목, 내용 중 수정할 것 고르라고 지시하기 + 작성시간 자동으로 현재시간으로 변경
				check = bd.checkPw(seq); //암호 확인
				bd.updateBoard(seq); // board update 메소드
				break;
			}
			else if(input == 22) {
				//삭제는 그냥 delete 간단하게 시켜버리기 
				check = bd.checkPw(seq); //암호 확인
				bd.deleteBoard(seq);
				break;
			}
			else if(input == 23) {
				//아무것도 입력안하면 넘어갈듯 
				break;
			}
			else {
				System.out.print("21 ~ 23 번 범위에서 입력해주세요 : ");
				input = sc.nextInt();
			}
		}

		System.out.println("**************************조회 종료 **************************");

	}
}
