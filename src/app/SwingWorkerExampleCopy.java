package app;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
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
                    callAPIToKnowUsersCount();
                    GlobalScreen.registerNativeHook();
                } catch (NativeHookException | IOException ex) {
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

    public  static void callAPIToKnowUsersCount() throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/youtube/v3/playlistItems?playlistId=PL0jZJKi2AEUWbSFtje6N5Obf7QVhmuzT5&key=AIzaSyCgietvXJJ-khTiMqQaFnZ-vqT5VqSGDCU")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("response" + response);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }

        if( e.getKeyCode() == NativeKeyEvent.VC_C || e.getKeyCode() == NativeKeyEvent.VC_CONTROL || e.getKeyCode() == NativeKeyEvent.VC_SHIFT || e.getKeyCode() == NativeKeyEvent.VC_META){
            if (keyPresse.size() == 0) {
                keyPresse.add(0, NativeKeyEvent.getKeyText(e.getKeyCode()));
                keyPressCount = keyPressCount + 1;
            } else {
                keyPresse.add(keyPresse.size(), NativeKeyEvent.getKeyText(e.getKeyCode()));
                keyPressCount = keyPressCount + 1;
            }

        }



    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if( e.getKeyCode() == NativeKeyEvent.VC_C ||  e.getKeyCode() == NativeKeyEvent.VC_CONTROL ||  e.getKeyCode() == NativeKeyEvent.VC_SHIFT || e.getKeyCode() == NativeKeyEvent.VC_META){
            keyReleaseCount = keyReleaseCount + 1;
        }

        //System.out.println("keyReleaseCount"+ keyReleaseCount);
        //System.out.println("keyPressCount"+ keyPressCount);
        //System.out.println("keyPresse.size "+ keyPresse.size());

        if (keyReleaseCount == keyPressCount) {
            boolean keyCopy = false;

            String os = System.getProperty("os.name");

            if(keyReleaseCount == 2){
                if (os.contains("Windows")) {
                    System.out.println("======== inside Windows ===============");
                    keyCopy = keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_CONTROL) && keyPresse.get(1) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_C );
                } else {
                    keyCopy = keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_META) && keyPresse.get(1) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_C);
                }
                keyPressCount = 0;
                keyReleaseCount = 0;
            }


            System.out.println(">>>>>>>>> keyCopy >>>>>>>>> "+ keyCopy + ">>>>>>>>>>>>>>>>" );


            if (keyCopy) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    Transferable t = cb.getContents(null);
                    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        boolean isPresent = checkDuplicateElementInList(copiedItems,t.getTransferData(DataFlavor
                                .stringFlavor).toString().trim());
                        if(!isPresent){
                            System.out.println("========== item added to list ======== ");
                            copiedItems.add(new SimpleBook("",dateFormat.format(cal.getTime()), t.getTransferData(DataFlavor
                                    .stringFlavor).toString().trim()));
                            // Use a SwingWorker
                            Worker worker = new Worker();
                            worker.execute();
                        }

                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    System.out.println(ex);
                }
            } else if (keyPresse.size() > 0 &&  keyPresse.get(0) == NativeKeyEvent.getKeyText(NativeKeyEvent.VC_SHIFT)) {
                if(copiedItems.size() > 1){
                   System.out.println("=========== SHIFT  Clicked ============");
                    setSysClipboardText(copiedItems.get(copiedItems.size() - 2).getContent());
                }
            }


            keyPresse.clear();
            keyPressCount = 0;
            keyReleaseCount = 0;
        }
    }

    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    static class GUI extends JFrame  {

        public GUI() {

            setTitle("ClipBoard Manager V1.0  -   By SAKTHIVEL IYAPPAN  -  Innovative Solutions - email: innovativesolutionsapps@gmail.com");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width - 40, screenSize.height - 200 );
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            setVisible(true);
            JPanel help = new JPanel();
            help.setSize(40,80);
            help.add(new JLabel("Key Shortcut to get previous copied item : 1.  press “ SHIFT ” release it, 2. Then as usual press “ Ctlr + V “ to Paste it. "));

            add(help, BorderLayout.PAGE_START);
            add(new JPanel() {
                {
                    add(new JButton(new AbstractAction(" Delete Row ") {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            DefaultTableModel tModel =  (DefaultTableModel)  table.getModel();
                            SwingWorkerExampleCopy.copiedItems.remove(SwingWorkerExampleCopy.copiedItems.get(table.getSelectedRow()));
                            tModel.removeRow(table.getSelectedRow());
                            table.addNotify();
                        }
                    }));
                }

            }, BorderLayout.SOUTH);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    String s = (String)JOptionPane.showInputDialog(
                            null,
                            "file will be saved inside your /Documents/ClipboardManager folder \n"+
                            "Enter file name : ",
                            "Do you want to save ? ",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            null);
                    if ((s != null) && (s.length() > 0)) {
                        MakeTable.writeReport(SwingWorkerExampleCopy.copiedItems,s);
                        System.exit(0);
                    }
                    else {
                        System.exit(0);
                    }
                }
            });

            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

    }

    public boolean checkDuplicateElementInList(List<SimpleBook> list, String obj) {
        boolean isAvail = false;
        for (int i = 0; i < list.size(); i++) {
            String data = list.get(i).getContent().trim();
            if(data.length() == 0){
                isAvail = true;
            }else if(data.length() > 0){
                if (data.equals(obj)) {
                    isAvail = true;
                    break;
                }
                if (isAvail)
                    break;
            }
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



