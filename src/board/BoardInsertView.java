package board;

import java.util.Scanner;

import dao.BoardDAO;
import dao.MemberDAO;
import vo.BoardVO;
import vo.MemberVO;

//최종적으로 View 붙는 클래스의 상위 추상 클래스 View 를 만들어서 상속 받게하여 input 메소드 오버라이드 하는 방법 써보기
public class BoardInsertView {

	Scanner sc = new Scanner(System.in);

	public void input() throws Exception{
		
		System.out.println("로그인 아이디와 암호를 입력하세요");
		System.out.print("아이디 : ");
		String id = sc.next();
		System.out.print("비밀번호 : ");
		String pw = sc.next();
		
		MemberVO vo = new MemberVO();
		vo.setId(id);
		vo.setPw(pw);
		MemberDAO dao = new MemberDAO();
		int result = dao.getMember(vo);
		
		if(result == 1) { //id존재 , pw 일치
			//맞으면 글쓰기
			System.out.println("로그인되었습니다.");
			System.out.print("글제목을 입력하세요 : "); //마지막 엔터 포함
			sc.nextLine(); //암호엔터 
			String title = sc.nextLine(); //제목에 공백이 포함될 수도있으니까 ~
			
			System.out.print("내용을 입력하세요 :");
			String contents = sc.nextLine();
			
			System.out.print("암호를 입력하세요 :");
			String writingpw = sc.nextLine();
			
			BoardVO boardvo = new BoardVO(title); // 4개 변수 저장 BoardVO 객체 생성
			
			boardvo.setTitle(title);
			boardvo.setContents(contents);
			boardvo.setWriter(id);
			boardvo.setPw(writingpw);
			
			BoardDAO boarddao = new BoardDAO();
			boarddao.insertBoard(boardvo);
			
			//내용 : xxxx

		}
		else if(result == 2){ //id 존재, pw 불일치
			System.out.println("비밀번호를 확인하세요.");
		}else { // 3 id 존재 x
			System.out.println("회원 가입부터 하세요.");
		}
		
		//member id , pw 존재 여부 확인 
		//맞으면 글쓰기
		//아니면 "회원가입부터 하세요" 출력 <-- 회원가입 메뉴도 만들어야할듯.....

	}
	
	public void view() {
		
	}
}
