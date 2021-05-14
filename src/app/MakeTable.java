package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MakeTable {

    public static File file;

    public static void createFolder() {
        file = new File(System.getProperty("user.dir") + "/ClipboardManager");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void writeReport(List<SimpleBook> val1) {

        createFolder();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        Date date = new Date();
        BufferedWriter writeTable = null;
        try {

            writeTable = new BufferedWriter(new FileWriter("ClipboardManager/" + "Report_" + dateFormat.format(date) + ".txt"));
            writeTable.write("\n");
            writeTable.write("\n");
            writeTable.newLine();

            for (int i = 0; i < val1.size(); i++) {
                if (i == 0) {
                    writeDashedLine(writeTable, val1.get(i).getsNo().length(), val1.get(i).getTime().length(), getLongestValue(val1));
                    writeTable.newLine();
                    writeValueLine(writeTable, val1.get(i).getsNo(), val1.get(i).getsNo().length(), val1.get(i).getTime(), val1.get(i).getTime().length(), val1.get(i).getContent().trim(), getLongestValue(val1));
                    writeTable.newLine();
                    writeDashedLine(writeTable, val1.get(i).getsNo().length(), val1.get(i).getTime().length(), getLongestValue(val1));
                    writeTable.newLine();
                } else {
                    writeValueLine(writeTable, val1.get(i).getsNo(), val1.get(i).getsNo().length(), val1.get(i).getTime(), val1.get(i).getTime().length(), val1.get(i).getContent().trim(), getLongestValue(val1));
                    writeTable.newLine();
                    writeDashedLine(writeTable, val1.get(i).getsNo().length(), val1.get(i).getTime().length(), getLongestValue(val1));
                    writeTable.newLine();
                }
            }
            writeTable.close();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (writeTable != null) {
            }
        }
    }

    public static void writeDashedLine(BufferedWriter writeTable, int v1l, int v2l, int v3l)
            throws IOException {
        writeTable.write("|");
        writeDashes(writeTable, v1l + 1);
        writeTable.write(" | ");
        writeDashes(writeTable, v2l);
        writeTable.write(" | ");
        writeDashes(writeTable, v3l);
        writeTable.write(" |");
    }

    public static void writeDashes(BufferedWriter writeTable, int length)
            throws IOException {
        for (int i = 0; i < length; i++) {
            writeTable.write("-");
        }
    }

    public static void writeValueLine(BufferedWriter writeTable, String value1, int v1l, String value2, int v2l, String value3, int v3l) throws IOException {
        writeTable.write("| " + centerText(value1, v1l) + " | "
                + centerText(value2, v2l) + " | "
                + centerText(value3, v3l) + " | "
        );
    }

    public static String centerText(String text, int length) {
        int textLength = text.length();
        if (textLength > length) {
            return text.substring(0, length);
        } else if (textLength == length) {
            return text;
        } else {
            int diff1 = (length - textLength) / 2;
            int diff2 = length - textLength - diff1;
            return getPadding(' ', diff1) + text + getPadding(' ', diff2);
        }
    }

    public static String getPadding(char pad, int length) {
        String padding = "";
        for (int i = 0; i < length; i++) {
            padding += pad;
        }
        return padding;
    }

    public static int getLongestValue(List<SimpleBook> val1) {
        int length = 0;
        for (SimpleBook s : val1) {
            length = Math.max(length, s.getContent().length());
        }
        return length;
    }
}