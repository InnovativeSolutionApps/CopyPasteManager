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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingWorkerExampleCopy implements NativeKeyListener {
    ArrayList<String> keyPresse = new ArrayList<>();
    public static ArrayList<SimpleBook> copiedItems = new ArrayList<>();
    int keyPressCount = 0;
    int keyReleaseCount = 0;
    public static DefaultTableModel tableModel = new DefaultTableModel();
    public static JTable table = new JTable(tableModel);
    public static JButton delete = new JButton("DELETE");

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
        keyReleaseCount = keyReleaseCount + 1;
        if (keyReleaseCount == keyPressCount) {
            keyPressCount = 0;
            keyReleaseCount = 0;
            boolean keyCopy = false;

            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                keyCopy = keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_CONTROL) && keyPresse.get(1) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_C);
            } else {
                keyCopy = keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_META) && keyPresse.get(1) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_C);
            }

            if (keyCopy) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    Transferable t = cb.getContents(null);
                    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        boolean isPresent = checkDuplicateElementInList(copiedItems,t.getTransferData(DataFlavor
                                .stringFlavor).toString());
                        if(!isPresent){
                            copiedItems.add(new SimpleBook("",dateFormat.format(cal.getTime()), t.getTransferData(DataFlavor
                                    .stringFlavor).toString()));
                        }
                        // Use a SwingWorker
                        Worker worker = new Worker();
                        worker.execute();
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    System.out.println(ex);
                }
            } else if (keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_CONTROL)) {
                setSysClipboardText(copiedItems.get(copiedItems.size() - 2).getContent());
            }
            keyPresse.clear();
        }
    }

    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }


    static class GUI extends JFrame implements ActionListener  {
        private static final long serialVersionUID = 1L;

        public GUI() {
            setTitle("ClipBoard Manager V1.0 by Sakthivel Iyappan ");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width, screenSize.height);
            setLocationRelativeTo(null);
            setVisible(true);
            setLayout(new BorderLayout());
            JPanel contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            contentPane.setLayout(new BorderLayout(0, 0));
            add(contentPane,BorderLayout.CENTER);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            delete.setSize(150,40);
            delete.addActionListener(this);
            panel.add(delete,BorderLayout.CENTER);
            add(panel,BorderLayout.PAGE_END);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    String s = (String)JOptionPane.showInputDialog(
                            null,
                            "File will be saved inside your Documents folder \n"+
                            "Enter text file name",
                            "Do you want to save ? ",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            null);
                    //If a string was returned, say so.
                    if ((s != null) && (s.length() > 0)) {
                        MakeTable.writeReport(SwingWorkerExampleCopy.copiedItems,s);
                        System.exit(0);
                    }
                }
            });

            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == delete) {
                DefaultTableModel tModel =  (DefaultTableModel)  table.getModel();
                SwingWorkerExampleCopy.copiedItems.remove(SwingWorkerExampleCopy.copiedItems.get(table.getSelectedRow()));
                tModel.removeRow(table.getSelectedRow());
                table.addNotify();
            }
        }
    }

    public boolean checkDuplicateElementInList(List<SimpleBook> list, String obj) {
        boolean isAvail = false;
        for (int i = 0; i < list.size(); i++) {
            String data = list.get(i).getContent();
            if (data.equals(obj)) {
                isAvail = true;
                break;
            }
            if (isAvail)
                break;
        }
        return isAvail;
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
            // Add row
            for (int row = 0; row <= SwingWorkerExampleCopy.copiedItems.size(); row++) {
                model.addRow(new Object[]{row + 1,SwingWorkerExampleCopy.copiedItems.get(row).getTime(), SwingWorkerExampleCopy.copiedItems.get(row).getContent()});
                 SwingWorkerExampleCopy.copiedItems.get(row).setsNo(String.valueOf(row+1));
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



