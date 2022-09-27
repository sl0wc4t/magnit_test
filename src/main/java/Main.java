import java.sql.SQLException;

public class Main {
    public static final String URL = "jdbc:mysql://localhost:3306/magnit_test";
    public static final String USER = "root";
    public static final String PASSWORD = "root";
    public static final int N = 100;

    public static void main(String[] args) {
        Application application = new Application();
        application.setUrl(URL);
        application.setUser(USER);
        application.setPassword(PASSWORD);
        application.setN(N);
        application.run();
    }
}
