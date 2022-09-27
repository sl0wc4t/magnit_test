import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;

public class Application {
    private String url;
    private String user;
    private String password;
    private int n;
    private static final String SELECT_QUERY = "select field from test";
    private static final String INSERT_QUERY = "insert into test value (?)";
    private static final String TRUNCATE_QUERY = "truncate test";

    public void run() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            //Очищаю таблицу
            executeTruncate(connection);

            //Вставляю данные в таблицу
            executeInsert(connection);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element entries = document.createElement("entries");
            Element entry = document.createElement("entry");
            Element field = document.createElement("field");
            Text value = document.createTextNode("12134");
            document.appendChild(entries);
            entries.appendChild(entry);
            entry.appendChild(field);
            field.appendChild(value);

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(document), new StreamResult(new FileOutputStream("1.xml")));



            //Вычитываю данные из таблицы
            ResultSet rs = executeSelect(connection);
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException | ParserConfigurationException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }

    }

    private ResultSet executeSelect(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_QUERY);
        return ps.executeQuery();
    }

    private void executeInsert(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
        for (int i = 1; i <= getN(); i++) {
            ps.setInt(1, i);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private void executeTruncate(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(TRUNCATE_QUERY);
        ps.execute();
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

    public int getN() {
        return n;
    }
}
