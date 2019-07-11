package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import vo.BoardVO;

public class BoardDAO {


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
		int bfViewcount = rs.getInt("viewcount");   // 오류코드 

		sql = "update board set viewcount = " + (bfViewcount + 1) 
				+ " where seq like " + seq;
		ps = con.prepareStatement(sql);
		boolean result = ps.execute(sql);

		if(result) {
			System.out.println("조회수가 증가하였습니다.");
		}
		else {
			System.out.println("조회수가 정상적으로 증가하지 않았습니다.  rollback 합니다");
			con.rollback();
		}
		//조회수 증가 안했을 때  rollback
		
		sql = "select seq, title, contents, writer, title, time, viewcount"
				+ " from board where seq like " + seq;
		PreparedStatement ps2 = con.prepareStatement(sql);
		ResultSet rs2 = ps2.executeQuery();

		ArrayList<BoardVO> list = new ArrayList<BoardVO>();

		int curViewcount = -1;
		
		while(rs2.next()) {
		BoardVO bv = new BoardVO(rs2.getString("title"));
		bv.setSeq(rs2.getInt("seq"));
		bv.setWriter(rs2.getString("writer"));
		bv.setTime(rs2.getString("time"));
		curViewcount = rs2.getInt("viewcount");
		bv.setViewcount(curViewcount);
		bv.setContents(rs2.getString("contents"));
		list.add(bv);
		}

		if(curViewcount == bfViewcount) {
			System.out.println("조회수 오류 Rollback 합니다.");
			con.rollback();
			System.exit(0); //강제 종료
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

	if(colnames.equals("viewcount")){
		int std = Integer.parseInt(word);
		sql = "select seq, title, writer, time, viewcount, contents"
				+ " from (select * from board order by seq)"
				+ " where viewcount >= " + std;
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
