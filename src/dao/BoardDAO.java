package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Delayed;

import oracle.net.aso.r;
import vo.BoardVO;

public class BoardDAO {

	/**
	 * 상세조회 -> 22. 글삭제 선택시
	 * @param seq
	 * @throws Exception
	 */
	public void deleteBoard(int seq) throws Exception{
		
		System.out.print("삭제하려는 글제목을 입력하세요 : ");
		Scanner sc = new Scanner(System.in);
		String title = sc.next();
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");
		
		String sql = "select title from board";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		//board의 모든 title을 저장하는 list
		List<String> list = new ArrayList<String>();
		while(rs.next()) {
			list.add(rs.getString("title"));
		}
		
		boolean ck = false;
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(title)) {
				ck = true;
				break;
			}
		}
		if(ck) {
			System.out.println(title + "글을 삭제합니다.");
			sql = "delete from board where title = '" + title +"'";
			ps = con.prepareStatement(sql);
			int result = ps.executeUpdate();
			System.out.println(result + "행을 삭제하였습니다.");
			
		}
		else {
			System.out.println("일치하는 제목의 글이 존재하지 않습니다.");
			System.out.println("메인으로 돌아갑니다.");
		}
	}
	
	
	/**
	 * 상세조회 -> 21. 글수정 선택시 
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	public void updateBoard(int seq) throws Exception{
		
		Scanner sc = new Scanner(System.in);
		System.out.print("변경 제목 입력: ");
		String newTitle = sc.next();
		System.out.print("변경 내용 입력: ");
		String newContents = sc.next();
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");
		
		String sql = "select id from member";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		
		List<String> list = new ArrayList<String>();
		//member id 전부 저장
		while(rs.next()) {
			list.add(rs.getString("id"));
		}
		
		System.out.print("변경 작성자 입력: ");  //member에 있는 id만 가능 미리 예외처리 해주자
		String newWriter = sc.next();
		
		boolean ck = false;
		for(int i = 0; i <list.size(); i++) {
			System.out.println("member id = " + list.get(i));
			if(list.get(i).equals(newWriter)) {
				ck = true;
				break; //굳이 다 돌필요 없잖아~ 하나라도 일치하면 바로 반복문 탈출
			}
		}
		
		if(ck) { //member 라면 글 수정 진행
			sql = "update board set "
					+ " title = '" + newTitle + "',"
					+ " contents = '" + newContents + "',"
					+ " writer = '" + newWriter + "'"
					+ " where seq like " + seq;
			
			ps = con.prepareStatement(sql);
			int result = ps.executeUpdate(sql);
			System.out.println("글을 수정하였습니다.");
			
		}
		else { //member 가 아니라면
			System.out.println("회원가입을 먼저 진행해주세요.");
		}
		
		
		
	}
	
	
	/**
	 * pw 확인 절차  21, 22 가 동일하게 암호 확인 절차를 거치기 때문에... 
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	public boolean checkPw(int seq) throws Exception{

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");

		System.out.print("암호입력 : ");
		Scanner sc = new Scanner(System.in);
		String pw = sc.next();

		String sql = "select seq, title, contents, writer, pw, title, time, viewcount"
				+ " from board where seq like " + seq;

		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();
		//테스트 결과 여기에 도달하기는 한다.

		while(rs.next()) {
			BoardVO bv = new BoardVO(rs.getString("title"));
			bv.setSeq(rs.getInt("seq"));
			bv.setWriter(rs.getString("writer")); //작성자에 title 출력중인 에러
			bv.setTime(rs.getString("time"));	//작성자에 title 출력중인 에러
			bv.setViewcount(rs.getInt("viewcount")); //작성자에 title 출력중인 에러
			bv.setContents(rs.getString("contents"));
			bv.setPw(rs.getString("pw"));
			list.add(bv);
		}
		
		//pw 일치 여부
		while(true) {
			
			if(pw.equals(list.get(0).getPw())) { 
				
				return true;
			}
			else if(pw.equals("exit")){
				
				return false;
			}
			else {
				System.out.println("비밀번호 불일치");
				System.out.println("종료를 원한다면 'exit'를 입력하세요.");
			}
			System.out.print("비밀번호를 다시입력하세요 : ");
			pw = sc.next();
		}
	}

	/**
	 * 상세 글 조회 메소드
	 * 조회 (조회수 +1)
	 * 수정, 삭제
	 */

	public ArrayList<BoardVO> getBoard(int seq) throws Exception{
		/**
		 * ----트랜잭션-----
		 * 1> seq 해당 글의 조회수 + 1  sql 실행
		 * 2> seq 해당 글의 조회
		 * 하나의 트랜젝션이 되어야 한다.
		 * 둘중에 하나라도 안되는 순간 rollback
		 */

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");

		String sql = "select viewcount from board where seq like " + seq;
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int Viewcount = rs.getInt("viewcount");   //정상적으로 읽어왔음 
		Viewcount++; 
		sql = "update board set viewcount = " + Viewcount 
				+ " where seq like " + seq;

		ps = con.prepareStatement(sql);
		int result = ps.executeUpdate(sql); 
		//정상적으로 수행됐다면, result 값은 1이 될 것이다.  그 외의 경우에는 정상적으로 수행되지 않았다고 판단.

		if(result == 1) {
			System.out.println("조회수가 증가하였습니다.");
			System.out.println(seq + "번글 조회수 : " + Viewcount );
		}
		else {
			System.out.println("조회수가 정상적으로 증가하지 않았습니다.  rollback 합니다");
			con.rollback();
		}
		//조회수 증가 안했을 때  rollback

		sql = "select seq, title, contents, writer, title, time, viewcount"
				+ " from board where seq like " + seq;

		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();

		//테스트 결과 여기에 도달하기는 한다.

		while(rs.next()) {
			BoardVO bv = new BoardVO(rs.getString("title"));
			bv.setSeq(rs.getInt("seq"));
			bv.setWriter(rs.getString("writer")); //작성자에 title 출력중인 에러
			bv.setTime(rs.getString("time"));	//작성자에 title 출력중인 에러
			bv.setViewcount(rs.getInt("viewcount")); //작성자에 title 출력중인 에러
			bv.setContents(rs.getString("contents"));
			list.add(bv);
		}

		con.commit();
		con.close();
		return list;
	}

	/**
	 * 4. 회원별 글조회
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ArrayList<BoardVO> getMemberWritingList(String name) throws Exception{

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();

		String sql = "select seq, title, writer, time, viewcount, contents"
				+ " from (select * from board order by seq)"
				+ " where writer like '" + name + "'";

		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			BoardVO bv = new BoardVO(rs.getString("title"));
			bv.setSeq(rs.getInt("seq"));
			bv.setWriter(rs.getString("writer"));
			bv.setTime(rs.getString("time")); 
			bv.setViewcount(rs.getInt("viewcount")); 
			bv.setContents(rs.getString("contents"));
			list.add(bv);
		}
		con.close();
		return list;
	}

	/**
	 * 3번 조건별 글 조회 메소드
	 * 선택 가능한 컬럼명은 다음과 같습니다.
	 * 1. 제목 2. 내용 3. 작성자 4. 조회수 5. 모두 
	 * 컬럼명 : 1 , 2 ,  3
	 * 조회조건 : 게시판 
	 * ==> 제목/내용/작성자 에 게시판 포함 글 리스트         %게시판%
	 * @param colname
	 * @param word
	 * @return
	 */
	public ArrayList<BoardVO> getConditionList(String colname , String word) throws Exception{

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");

		String[] colnames = colname.split("-"); //- 기준으로 분리

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();

		String sql;

		if(colname.equals("viewcount")){
			int std = Integer.parseInt(word);
			System.out.println("받아오는 숫자는 = " + std);
			sql = "select seq, title, writer, time, viewcount, contents"
					+ " from (select * from board order by seq)"
					+ " where " + colname + " >= " + std;
		}
		else {
			if(colnames.length == 3) { //title-contents-writer

				sql = "select seq, title, writer, time, viewcount, contents"
						+ " from (select * from board order by seq)"
						+ " where " + colnames[0] + " like '%" + word + "%' "
						+ " or + " + colnames[1] +  " like '%" + word + "%' "
						+ " or + " + colnames[2] +  " like '%" + word + "%' ";
			}

			else {

				sql = "select seq, title, writer, time, viewcount, contents"
						+ " from (select * from board order by seq)"
						+ " where " + colname + " like '%" + word + "%' ";
			}
		}

		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			BoardVO bv = new BoardVO(rs.getString("title"));
			bv.setSeq(rs.getInt("seq"));
			bv.setWriter(rs.getString("writer"));
			bv.setTime(rs.getString("time")); 
			bv.setViewcount(rs.getInt("viewcount")); 
			bv.setContents(rs.getString("contents"));
			list.add(bv);
		}
		con.close();
		return list;
	}

	/**
	 * 2번 페이지별 글 조회 메소드
	 * @param pagenum
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	public ArrayList<BoardVO> getPageList(int pagenum , int listPerPage) throws Exception{

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");


		int start = (pagenum-1)*listPerPage + 1;
		int end = pagenum*listPerPage;

		String sql = 
				"select r, title, writer, time" 
						+ " from (select rownum r, title, writer, time" 
						+ "	from (select title, writer, time"
						+ " from board order by time)"
						+ " 	)"
						+ " where r >= ? and r <= ?"; 

		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, start);
		st.setInt(2, end);

		ResultSet rs = st.executeQuery();   

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();

		while(rs.next()) {
			BoardVO bv = new BoardVO(rs.getString("title"));
			bv.setWriter(rs.getString("writer"));
			bv.setTime(rs.getString("time")); 
			list.add(bv);
		}
		con.close();
		return list;
	}


	//BoardInsertView 호출 vo(제목 내용 작성자 암호)
	public void insertBoard(BoardVO vo) throws Exception{ //매개변수 수가 너무 많아서 BoardVO 객체 하나로 만들어서 매개변수 줄임
		// vo 저장 변수들 board 테이블 insert
		// 번호(board_seq.nextval) , 조회수(0) , 작성시간(sysdate)

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
				"jdbc", "jdbc");

		String sql = "insert into board values(board_seq.nextval,"
				+ " ?,?,?,?,sysdate,0)";
		PreparedStatement pt = con.prepareStatement(sql);

		pt.setString(1, vo.getTitle());
		pt.setString(2, vo.getContents());
		pt.setString(3, vo.getPw());
		pt.setString(4, vo.getWriter());
		int row = pt.executeUpdate(); //여러개 값이라서 executeUpdate
		System.out.println(row);
		con.close();

		System.out.println("게시물 작성을 완료하였습니다.");
	}

}
