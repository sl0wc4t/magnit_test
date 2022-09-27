import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private Connection connection;
    private int n;
    private List<Entry> entries;

    private static final String FILE_NAME_1 = "1.xml";
    private static final String FILE_NAME_2 = "2.xml";
    private static final String FILE_NAME_3 = "converter.xsl";
    private static final String SELECT_QUERY = "SELECT FIELD FROM TEST";
    private static final String INSERT_QUERY = "INSERT INTO TEST VALUE (?)";
    private static final String TRUNCATE_QUERY = "TRUNCATE TEST";

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void run() {
        //Очищаю таблицу
        executeTruncate(connection);

        //Вставляю данные в таблицу
        executeInsert(connection);

        //Вычитываю данные из таблицы
        executeSelect(connection);

        if (!entries.isEmpty()) {
            //Формирую 1.xml
            getXml();

            //Преобразую 1.xml в 2.xml с помощью шаблона converter.xsl
            getTransformedXml();

            //Считаю сумму значений field в 2.xml
            System.out.println("Сумма = " + getSum());
        }
    }

    private void executeSelect(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_QUERY); ResultSet rs = ps.executeQuery()) {
            entries = new ArrayList<>();
            while (rs.next()) {
                Entry entry = new Entry(rs.getInt(1));
                entries.add(entry);
                //System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeInsert(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_QUERY)) {
            for (int i = 1; i <= getN(); i++) {
                ps.setInt(1, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeTruncate(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(TRUNCATE_QUERY)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().newDocument();
            Element root = document.createElement("entries");
            document.appendChild(root);

            //Добавляю ноды в документ
            entries.forEach(entry -> entry.entryToXml(document, root));

            StreamResult xmlOutput = new StreamResult(new File(FILE_NAME_1));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(document), xmlOutput);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void getTransformedXml() {
        try {
            StreamSource xmlSource = new StreamSource(FILE_NAME_1);
            StreamSource xslSource = new StreamSource(FILE_NAME_3);
            StreamResult xmlOutput = new StreamResult(new File(FILE_NAME_2));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xslSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlSource, xmlOutput);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private int getSum() {
        int sum = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(new File(FILE_NAME_2));
            Node root = document.getDocumentElement();
            NodeList entryNodes = root.getChildNodes();
            for (int i = 0; i < entryNodes.getLength(); i++) {
                if (entryNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Node entry = entryNodes.item(i);
                    Node attrib = entry.getAttributes().item(0);
                    sum += Integer.parseInt(attrib.getNodeValue());
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return sum;
    }
}
