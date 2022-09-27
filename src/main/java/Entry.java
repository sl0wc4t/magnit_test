import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Entry {
    private int field;

    public Entry(int field) {
        setField(field);
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public void entryToXml(Document document, Element entries) {
        Element entry = document.createElement("entry");
        entries.appendChild(entry);
        Element field = document.createElement("field");
        entry.appendChild(field);
        Text value = document.createTextNode("" + getField());
        field.appendChild(value);
    }
}
