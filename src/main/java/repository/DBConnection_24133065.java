package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Ket noi co so du lieu SQL Server (Data Access Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class DBConnection_24133065 {

    private static final String SERVER = "localhost:1433";
    private static final String DB_NAME = "BookStore";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    private static final String URL =
            "jdbc:sqlserver://" + SERVER
            + ";databaseName=" + DB_NAME
            + ";encrypt=false;trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Khong tim thay JDBC driver!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
