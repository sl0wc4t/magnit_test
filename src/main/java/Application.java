import java.sql.*;

public class Application {
    private String url;
    private String user;
    private String password;
    private int n;
    private static final String SELECT_QUERY = "select field from test";
    private static final String UPDATE_QUERY = "insert into test value (?)";
    private static final String TRUNCATE_QUERY = "truncate test";

    public void run() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement ps1 = connection.prepareStatement(TRUNCATE_QUERY);
            ps1.execute();

            PreparedStatement ps2 = connection.prepareStatement(UPDATE_QUERY);
            for (int i = 1; i <= n; i++) {
                ps2.setInt(1, i);
                ps2.addBatch();
            }
            ps2.executeBatch();

            PreparedStatement ps = connection.prepareStatement(SELECT_QUERY);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setN(int n) {
        this.n = n;
    }
}
