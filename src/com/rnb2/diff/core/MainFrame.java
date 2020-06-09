package com.rnb2.diff.core;


import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author budukh.rn
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_DIR = "C:\\";

    private static final String C_TOOLS_DIFF_TOOL_DIFFER_1_0_SNAPSHOT_JAR;
    private static final String DIFFER_NAME = "differ-1.0-SNAPSHOT.jar";
    public static final String TXT_2 = "txt2";
    public static final String TXT_1 = "txt1";
    public static final String TXT_3 = "txt3";
    private Logger logger = Logger.getLogger(MainFrame.class.getName());

    static {
        C_TOOLS_DIFF_TOOL_DIFFER_1_0_SNAPSHOT_JAR = "C:\\tools\\Diff_tool\\" + DIFFER_NAME;
    }

    private static final Color titleColor = new Color(139, 26, 26);
    private static final String PATH_SRC = "/com/rnb2/diff/";

    private static final String PROP_PATH_DB1  = "prop_path_db1";
    private static final String PROP_PATH_TOOL = "prop_path_tool";
    private static final String PROP_NAME_TOOL = "prop_name_tool";
    private static final String PROP_PATH_DB2  = "prop_path_db2";
    private static final String PROP_PATH_LOG  = "prop_path_log";
    private static final String PROP_FILENAME  = "GuiDiffTool.properties";

    private final ImageIcon ICON_EXIT = new ImageIcon(getClass().getResource(PATH_SRC + "img/exit.png"));
    private final ImageIcon ICON_GO = new ImageIcon(getClass().getResource(PATH_SRC + "img/go.png"));
    private final ImageIcon ICON_SETTING = new ImageIcon(getClass().getResource(PATH_SRC + "img/setting.png"));

    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    private Dimension dimension = toolkit.getScreenSize();
    private int srcWidth = dimension.width - 800;
    private int srcHeight = dimension.height - 800;
    private Container container = getContentPane();
    private Map<String, JTextField> map = new HashMap<>();
    private JFileChooser chooserNds = new JFileChooser();
    private JFileChooser chooserDir = new JFileChooser();
    private JTextField fieldLog;
    private JToolBar toolbar = new JToolBar();

    private JButton button1 = new JButton("...");
    private JButton button2 = new JButton("...");
    private JButton button3 = new JButton("...");

    private JCheckBox checkBox = new JCheckBox();
    private JCheckBox checkBox2 = new JCheckBox();

    private String customDiffToolPath = C_TOOLS_DIFF_TOOL_DIFFER_1_0_SNAPSHOT_JAR;
    private String customNameTool = "";
    private String fullReport = "";
    private Properties properties = new Properties();

    public MainFrame() {
        setTitle("Gui app for diff tool \"" + DIFFER_NAME + "\"");
        setSize(srcWidth, srcHeight);
        setLocationRelativeTo(null);
        Locale.setDefault(new Locale("ru", "RU"));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFileChooser();
        initDirChooser();

        JPanel panelMain = new JPanel(new GridLayout(4, 1));

        button1.addActionListener(listenerPath1());
        button2.addActionListener(listenerPath2());
        button3.addActionListener(listenerPath3());

        fieldLog = new JTextField("summary_log");

        panelMain.add(getBoxPanel("Тестируемая база 1:", button1, TXT_1));
        panelMain.add(getBoxPanel("Тестируемая база 2:", button2, TXT_2));
        panelMain.add(getBoxPanel("Лог:", button3, TXT_3, fieldLog));

        checkBox.setText("Полный лох");
        checkBox2.setText("Открыть по завершению");

        panelMain.add(getBoxPanel(checkBox, checkBox2));

        final String fileName = getFileNameProperties();
        loadParameters(fileName);

        actionExit.putValue(Action.SHORT_DESCRIPTION, "Exit");
        actionGo.putValue(Action.SHORT_DESCRIPTION, "Huyak... ");
        actionSetting.putValue(Action.SHORT_DESCRIPTION, "S");

        toolbar.setFloatable(false);
        toolbar.add(actionExit);
        toolbar.add(actionGo);
        toolbar.add(actionSetting);

        customNameTool = getNameTool(customDiffToolPath);

        container.add(toolbar, BorderLayout.NORTH);
        container.add(panelMain, BorderLayout.CENTER);

        addWindowListener(windowCloseAdapter(fileName));
    }

    private Component getBoxPanel(JComponent... components) {
        Box box = Box.createHorizontalBox();
        for (JComponent c:components){
            box.add(c);
        }
        return box;
    }


    private String getFileNameProperties() {
        return new StringBuilder()
                .append(System.getProperties().get("user.home"))
                .append(System.getProperties().get("file.separator"))
                .append(PROP_FILENAME)
                .toString();
    }

    private void initFileChooser() {
        final NdsFilter filter = new NdsFilter();
        filter.addExtentions("nds");
        filter.setDescription("NDS Files");
        chooserNds.setFileFilter(filter);
    }

    private void initDirChooser() {
        //chooserDir.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooserDir.setCurrentDirectory(new File(DEFAULT_DIR));
        chooserDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooserDir.setAcceptAllFileFilterUsed(false);
    }

    private String getDirectory(String property) {
        if (property != null && !property.isEmpty()) {
            return property.substring(0, property.contains(".") ? property.lastIndexOf('\\') : property.length());
            //return property.substring(0, property.lastIndexOf('\\'));
        }
        return DEFAULT_DIR;
    }

    Action actionSetting = new AbstractAction("", ICON_SETTING) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String dialog = JOptionPane.showInputDialog("Path:", customDiffToolPath);
            if (dialog != null && !dialog.isEmpty()) {
                customDiffToolPath = dialog;
                customNameTool = getNameTool(customDiffToolPath);
            }
        }
    };

    final Action actionGo = new AbstractAction("", ICON_GO) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            boolean error = false;
            for (Entry<String, JTextField> jTextField : map.entrySet()) {
                if (jTextField.getValue().getText().isEmpty()) {
                    JOptionPane.showMessageDialog(rootPane, "Field with key = " + jTextField.getKey() + " is empty", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    error = true;
                    break;
                }
            }
            if (error) {
                return;
            }
            if (fieldLog.getText().isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, " Log file name is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //OperationExecutor.getInstance().execute(doGo(), MainFrame.this);
            doGo();
        }
    };

    private void doGo() {

        saveParameters(getFileNameProperties());

        String path1 = map.get(TXT_1).getText();
        String path2 = map.get(TXT_2).getText();
        String path3 = map.get(TXT_3).getText();

        fullReport = "";
        if (checkBox.isSelected())
            fullReport = "--full";

        LongRunProcess process = new LongRunProcess()
                .addPathDirTool(customDiffToolPath.substring(0, customDiffToolPath.lastIndexOf("\\")))
                .addNameToll(customNameTool)
                .addCommandPath1(path1)
                .addCommandPath2(path2)
                .addPathDirOut(path3)
                .addNameOut(fieldLog.getText() + ".txt")
                .addFullReport(fullReport)
                .addContainer(MainFrame.this)
                .addOpenAfter(checkBox2.isSelected());

        final String fileNameLog = fieldLog.getText() + ".txt";

      /*  process.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pcEvt) {
                LongRunProcess pr = (LongRunProcess)pcEvt.getSource();
// && pr.getWfValue() == 0
                if (SwingWorker.StateValue.DONE == pcEvt.getNewValue() && pr.getWfValue() == 0) {
                    System.out.println("pcEvt.getNewValue()=" + pcEvt.getNewValue() + " old="  + pcEvt.getOldValue());
                    System.out.println("class = " + pcEvt.getSource().getClass());
                    System.out.println("pr.getWfValue() = " + pr.getWfValue());

                }
            }
        });*/

        try {
            process.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getLocalizedMessage());
        }

    }

    Action actionExit = new AbstractAction("", ICON_EXIT) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            saveParameters(getFileNameProperties());
            dispose();
            System.exit(0);
        }
    };

    private ActionListener listenerPath1() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String property = properties.getProperty(PROP_PATH_DB1);

                final File currentDirectory = new File(getDirectory(property));
                chooserNds.setCurrentDirectory(currentDirectory);

                int r = chooserNds.showOpenDialog(MainFrame.this);

                if (r == JFileChooser.APPROVE_OPTION) {
                    String string = chooserNds.getSelectedFile().getPath();
                    JTextField jTextField1 = map.get(TXT_1);
                    jTextField1.setText(string);

                    chooserDir.setCurrentDirectory(currentDirectory);
                    String text = chooserNds.getCurrentDirectory() + "";
                    JTextField jTextField = map.get(TXT_3);
                    jTextField.setText(text);
                }
            }
        };
    }

    private ActionListener listenerPath2() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String property = properties.getProperty(PROP_PATH_DB2);
                if (!map.get(TXT_2).getText().isEmpty()){
                    property = getDirectory(map.get(TXT_2).getText());
                } else
                if (property.isEmpty()){
                    property = getDirectory(map.get(TXT_1).getText());
                }
                chooserNds.setCurrentDirectory(new File(getDirectory(property)));

                int r = chooserNds.showOpenDialog(MainFrame.this);

                if (r == JFileChooser.APPROVE_OPTION) {
                    String string = chooserNds.getSelectedFile().getPath();
                    JTextField jTextField1 = map.get(TXT_2);
                    jTextField1.setText(string);
                }
            }
        };
    }

    private ActionListener listenerPath3() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String property = properties.getProperty(PROP_PATH_LOG);
                if (!map.get(TXT_3).getText().isEmpty()){
                    property = getDirectory(map.get(TXT_3).getText());
                } else
                if (property.isEmpty()){
                    property = getDirectory(map.get(TXT_1).getText());
                }

                chooserDir.setCurrentDirectory(new File(getDirectory(property)));

                int r = chooserDir.showOpenDialog(MainFrame.this);

                if (r == JFileChooser.APPROVE_OPTION) {
                    String string = chooserDir.getSelectedFile() + "";
                    JTextField jTextField1 = map.get(TXT_3);
                    jTextField1.setText(string);
                }
            }
        };
    }

    private Box getBoxPanel(String caption, JButton button, String key) {
        Box mainLeftBox = Box.createVerticalBox();
        Box searchBox = Box.createVerticalBox();
        TitledBorder titledBorder = new TitledBorder(new EtchedBorder(), caption);
        titledBorder.setTitleColor(new Color(139, 26, 26));
        searchBox.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(1, 5, 3, 5)));

        Box periodPanel = Box.createHorizontalBox();
        periodPanel.add(button);
        periodPanel.add(Box.createHorizontalStrut(5));
        JTextField textField = new JTextField();
        map.putIfAbsent(key, textField);
        setTextFieldValue(key);

        periodPanel.add(textField);
        searchBox.add(periodPanel);
        mainLeftBox.add(searchBox);

        return mainLeftBox;
    }

    private void setTextFieldValue(String key) {
        JTextField jTextField = map.get(key);
        String value = "";
        switch (key) {
            case TXT_1:
                value = properties.getProperty(PROP_PATH_DB1);
                break;
            case TXT_2:
                value = properties.getProperty(PROP_PATH_DB2);
                break;
            case TXT_3:
                value = properties.getProperty(PROP_PATH_LOG);
                break;

            default:
                break;
        }
        jTextField.setText(value);
    }


    private Box getBoxPanel(String caption, JButton button, String key, JTextField field) {
        Box mainLeftBox = Box.createVerticalBox();
        Box searchBox = Box.createVerticalBox();
        TitledBorder titledBorder = new TitledBorder(new EtchedBorder(), caption);
        titledBorder.setTitleColor(new Color(139, 26, 26));
        searchBox.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(1, 5, 3, 5)));

        Box periodPanel = Box.createHorizontalBox();
        periodPanel.add(button);
        periodPanel.add(Box.createHorizontalStrut(5));
        JTextField textField = new JTextField();

        periodPanel.add(textField);
        periodPanel.add(Box.createHorizontalStrut(3));
        periodPanel.add(field);
        searchBox.add(periodPanel);
        mainLeftBox.add(searchBox);

        map.putIfAbsent(key, textField);
        return mainLeftBox;
    }


    protected Box getTitleBox(String title, List<Component> components, boolean isCompoundBorder, int widthPanel,
                              int heightPanel) {
        JLabel label = new JLabel(title);
        Box box = Box.createVerticalBox();

        if (isCompoundBorder) {
            TitledBorder titledBorder = new TitledBorder(new EtchedBorder(), title);
            titledBorder.setTitleColor(titleColor);
            box.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(0, 0, 0, 0)));
        } else {
            box.add(label);
        }
        for (Component component : components) {
            box.add(component);
        }
        box.setMinimumSize(new Dimension(widthPanel, heightPanel));
        box.setPreferredSize(box.getMinimumSize());
        box.revalidate();
        return box;
    }

    private WindowAdapter windowCloseAdapter(final String fileName) {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveParameters(fileName);
            }
        };
    }

    private void loadProperties(File fileName) throws FileNotFoundException {
        try (FileInputStream inStream = new FileInputStream(fileName)){
            properties.load(inStream);

            customDiffToolPath = (String) properties.getProperty(PROP_PATH_TOOL, "");
            customNameTool = (String) properties.getProperty(PROP_NAME_TOOL, "");
            map.get(TXT_1).setText((String) properties.getProperty(PROP_PATH_DB1, ""));
            map.get(TXT_2).setText((String) properties.getProperty(PROP_PATH_DB2, ""));
            map.get(TXT_3).setText((String) properties.getProperty(PROP_PATH_LOG, ""));
        }catch (IOException e){
            logger.log(Level.INFO, e.getLocalizedMessage());
            throw new FileNotFoundException(fileName.getName());
        }
    }

    private void loadParameters(final String fileName) {
        File file = new File(fileName);
        File path = file.getParentFile();

        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            loadProperties(file);
           /* FileInputStream propf = new FileInputStream(file);
            properties.load(propf);

            customDiffToolPath = (String) properties.getProperty(PROP_PATH_TOOL, "");
            customNameTool = (String) properties.getProperty(PROP_NAME_TOOL, "");
            map.get(TXT_1).setText((String) properties.getProperty(PROP_PATH_DB1, ""));
            map.get(TXT_2).setText((String) properties.getProperty(PROP_PATH_DB2, ""));
            map.get(TXT_3).setText((String) properties.getProperty(PROP_PATH_LOG, ""));
            propf.close();*/
        } catch (FileNotFoundException exception) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fileName);

                properties.put(PROP_PATH_TOOL, customDiffToolPath);
                properties.put(PROP_PATH_DB1, "");
                properties.put(PROP_PATH_DB2, "");
                properties.put(PROP_PATH_LOG, "");
                properties.put(PROP_NAME_TOOL, "");
                properties.store(out, "MainWindow");

            } catch (IOException e) {
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (Exception exc) {
                    }
                }
            }
        } catch (IOException exception) {
        }
    }

    private String getNameTool(String customDiffToolPath) {
        int beginIndex = customDiffToolPath.lastIndexOf("\\");
        return customDiffToolPath.substring(beginIndex + 1);
    }

    private void saveParameters(final String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName);) {
            properties.put(PROP_PATH_TOOL, customDiffToolPath);
            properties.put(PROP_PATH_DB1, map.get(TXT_1).getText());
            properties.put(PROP_PATH_DB2, map.get(TXT_2).getText());
            properties.put(PROP_PATH_LOG, map.get(TXT_3).getText());
            properties.put(PROP_NAME_TOOL, customNameTool);
            properties.store(out, "MainWindow");
        } catch (IOException e) {
            logger.log(Level.WARNING, "saveParameters(): " + e.getLocalizedMessage());
        }
    }

    static class NdsFilter extends FileFilter {
        private ArrayList<String> list = new ArrayList<>();
        private String descrip = "";

        public void addExtentions(String newVal) {
            if (!newVal.startsWith(".")) {
                newVal = "." + newVal;
                list.add(newVal.toLowerCase());
            }
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            String string = f.getName().toLowerCase();

            for (int i = 0; i < list.size(); i++) {
                if (string.endsWith((String) list.get(i)))
                    return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return descrip;
        }

        public String setDescription(String newval) {
            return descrip = newval;
        }
    }
}
