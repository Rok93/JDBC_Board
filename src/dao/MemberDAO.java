package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import vo.MemberVO;

public class MemberDAO { //MEMEBER 테이블 
	//INSERT / UPDATE / SELECT
	//입력받은 ID, PW 와 일치하는지 확인해야함   SELECT
	
	public int getMember(MemberVO vo) throws Exception{
			Class.forName("oracle.jdbc.driver.OracleDriver");//외부에 있는 다른라이브러리 등록
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
					"jdbc", "jdbc");
			String sql = "select pw from member where id=?";
			con.prepareStatement(sql);
			PreparedStatement pt = con.prepareStatement(sql);
			pt.setString(1, vo.getId());
			ResultSet rs = pt.executeQuery();
			if(rs.next()) {
				String dbpw = rs.getString("pw");
				String inputpw = vo.getPw();
				if(dbpw.equals(inputpw)) {
					return 1;
				}
				else {
					return 2;
				}
			}
			else {
				return 3;
			}
	}
}
