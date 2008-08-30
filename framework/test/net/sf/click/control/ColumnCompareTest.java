package net.sf.click.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ColumnCompareTest extends TestCase {

    public void test_1() {
        Column column = new Column("name");

        Table table = new Table("table");
        table.addColumn(column);

        Column.ColumnComparator comparator = new Column.ColumnComparator(column);
        List rowList = createRowList1();
        Collections.sort(rowList, comparator);
        System.out.println(rowList);

        table.setSortedAscending(false);
        Collections.sort(rowList, comparator);
        System.out.println(rowList);
    }

    public void test_2() {
        Column column = new Column("name");

        Table table = new Table("table");
        table.addColumn(column);

        Column.ColumnComparator comparator = new Column.ColumnComparator(column);
        List rowList = createRowList2();
        Collections.sort(rowList, comparator);
        System.out.println(rowList);

        table.setSortedAscending(false);
        Collections.sort(rowList, comparator);
        System.out.println(rowList);
    }

    private List createRowList1() {
        List rowList = new ArrayList();

        rowList.add(createRow("Dht"));
        rowList.add(createRow("DHT"));
        rowList.add(createRow(Boolean.FALSE));
        rowList.add(createRow(Boolean.TRUE));
        rowList.add(createRow("Data Services"));
        rowList.add(createRow("Data 213 Services"));
        rowList.add(createRow("Data 123 Services"));
        rowList.add(createRow("Data 2.1.3 Services"));
        rowList.add(createRow("Data 1.2.3 Services"));
        rowList.add(createRow("0123"));
        rowList.add(createRow("1234"));
        rowList.add(createRow("-234"));
        rowList.add(createRow("32-34"));
        rowList.add(createRow("23-34"));
        rowList.add(createRow("item4"));
        rowList.add(createRow("item3"));
        rowList.add(createRow("item2"));
        rowList.add(createRow("item1"));
        rowList.add(createRow("item10"));

        return rowList;
    }

    private List createRowList2() {
        List rowList = new ArrayList();

        rowList.add(createRow(null));
        rowList.add(createRow(Boolean.TRUE));
        rowList.add(createRow(Boolean.FALSE));

        return rowList;
    }

    private Map createRow(Object value) {
        return Collections.singletonMap("name", value);
    }
}
