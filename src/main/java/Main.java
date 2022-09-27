import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static final String URL = "jdbc:mysql://localhost:3306/magnit_test";
    public static final String USER = "root";
    public static final String PASSWORD = "root";
    public static final int N = 1000;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Application application = new Application();
            application.setConnection(connection);
            application.setN(N);
            application.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
