package app;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingWorkerExampleCopy implements NativeKeyListener {
    ArrayList<String> keyPresse = new ArrayList<>();
    public static ArrayList<SimpleBook> copiedItems = new ArrayList<>();

    int keyPressCount = 0;
    int keyReleaseCount = 0;

    public static DefaultTableModel tableModel = new DefaultTableModel();
    public static JTable table = new JTable(tableModel);


    public static void main(String[] args) {
        // Show GUI
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {
                    GlobalScreen.registerNativeHook();
                } catch (NativeHookException ex) {
                    System.err.println("There was a problem registering the native hook.");
                    System.err.println(ex.getMessage());
                    System.exit(1);
                }
                Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
                logger.setLevel(Level.OFF);
                logger.setUseParentHandlers(false);
                GlobalScreen.addNativeKeyListener(new SwingWorkerExampleCopy());


                GUI gui = new GUI();

                // Use a SwingWorker
                Worker worker = new Worker();
                worker.execute();

                table.setEnabled(true);
                table.addNotify();
                table.setGridColor(Color.LIGHT_GRAY);
              //  table.setRowHeight(40);

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                gui.getContentPane()
                        .add(scrollPane, BorderLayout.CENTER);
            }
        });
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }

        if (keyPresse.size() == 0) {
            keyPresse.add(0, NativeKeyEvent.getKeyText(e.getKeyCode()));
            keyPressCount = keyPressCount + 1;
        } else {
            keyPresse.add(keyPresse.size(), NativeKeyEvent.getKeyText(e.getKeyCode()));
            keyPressCount = keyPressCount + 1;
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        keyReleaseCount = keyReleaseCount + 1;

        for (int i = keyPresse.size() - keyPressCount; i < keyPresse.size(); i++) {
            //System.out.println("i :" + i + ": " + "value : " + keyPresse.get(i));
        }

        if (keyReleaseCount == keyPressCount) {
            keyPressCount = 0;
            keyReleaseCount = 0;

            if (keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_META) && keyPresse.get(1) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_C)) {
                System.out.println("INSIDE COPY Condition......");

                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    Transferable t = cb.getContents(null);
                    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        System.out.println(">>>> Copied String " + t.getTransferData(DataFlavor
                                .stringFlavor));
                        // add time stamp

                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        System.out.println(dateFormat.format(cal.getTime()));

                        copiedItems.add(new SimpleBook(dateFormat.format(cal.getTime()), t.getTransferData(DataFlavor
                                .stringFlavor).toString()));
                        // Use a SwingWorker
                        Worker worker = new Worker();
                        worker.execute();
                    }

                } catch (UnsupportedFlavorException | IOException ex) {
                    System.out.println(ex);
                }
            } else if (keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_CONTROL)) {
                System.out.println("items size " + copiedItems.size());
                System.out.println("INSIDE Ctrl+Command/windows key  Condition......" + copiedItems.get(copiedItems.size() - 2));
                setSysClipboardText(copiedItems.get(copiedItems.size() - 2).getContent());
            }


            for (int i = 0; i < copiedItems.size(); i++) {
                System.out.println(" copiedItems i :" + i + ": " + "value : " + copiedItems.get(i));
            }

            keyPresse.clear();
        }
    }

    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }


    static class GUI extends JFrame {
        private static final long serialVersionUID = 1L;

        public GUI() {
            setTitle("GUI");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width, screenSize.height);
            setLocationRelativeTo(null);
            setVisible(true);
            JPanel contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            contentPane.setLayout(new BorderLayout(0, 0));
            setContentPane(contentPane);
        }
    }

    static class Worker extends SwingWorker<DefaultTableModel, Object[]> {

        private DefaultTableModel model = new DefaultTableModel();
        private final static int numCols = 2;

        Worker() {
            model.setColumnCount(numCols);
            model.setColumnIdentifiers(new Object[]{"S.No","Time ", "Copied Text "});
        }

        @Override
        protected DefaultTableModel doInBackground() throws Exception {
            System.out.println("SwingWorkerExampleCopy.copiedItems.size() " + SwingWorkerExampleCopy.copiedItems.size());
            // Add row
            for (int row = 0; row <= SwingWorkerExampleCopy.copiedItems.size(); row++) {
                model.addRow(new Object[]{row + 1,SwingWorkerExampleCopy.copiedItems.get(row).getTime(), SwingWorkerExampleCopy.copiedItems.get(row).getContent()});
            }
            return model;
        }

        @Override
        protected void done() {
            table.setModel(model);
            table.addNotify();
            table.setAutoCreateRowSorter(true);
            TableColumn columnA = table.getColumn("S.No");
            columnA.setMinWidth(10);
            columnA.setMaxWidth(50);
            TableColumn columnB = table.getColumn("Time ");
            columnB.setMinWidth(170);
            columnB.setMaxWidth(180);
        }

    }
}
class SimpleBook {
    private String time;
    private String content;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public SimpleBook( String time, String content) {
        this.time = time;
        this.content = content;
    }


}


