package com.rnb2.diff.core;

import com.rnb2.diff.com.rnb2.diff.utils.OperationExecutor;
import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * @author budukh.rn
 */
public class LongRunProcess extends SwingWorker {

    private  String nameTool;
    private  String nameOut;
    private  String fullReport;
    private  String commandPathDirTool;
    private  String commandPathDirOut;
    private  String commandPath1;
    private  String commandPath2;

    private  JFrame container;
    private boolean openAfter;

    private int wf = -1;
    private Process process;

    @Override
    protected Object doInBackground() throws Exception {
        int result = -2;
        OperationExecutor.getInstance().execute(taskGo, container);
        return result;
    }

    private void doGo(){
        try {
            ProcessBuilder  processBuilder = new ProcessBuilder();

            if(!fullReport.isEmpty()){
                processBuilder.command("java","-jar",
                        commandPathDirTool+"\\"+nameTool,
                        "-i", commandPath1,
                        "-j", commandPath2,
                        "-o", commandPathDirOut + "\\" + nameOut,
                        fullReport, ""
                );
            }else {
                processBuilder.command("java","-jar",
                    commandPathDirTool+"\\"+nameTool,
                    "-i", commandPath1,
                    "-j", commandPath2,
                    "-o", commandPathDirOut + "\\" + nameOut
                );
            }
            /*pb.command("java","-jar", "c:\\Tools\\Diff_tool\\differ-1.0-SNAPSHOT.jar","-i",
                        "C:\\Work\\Tasks\\NDSAKELA-10226\\pp-11\\fb\\UR11402\\ROOT.NDS","-j",
                        "C:\\Work\\Tasks\\NDSAKELA-10226\\pp-13\\fb\\UR11402\\ROOT.NDS", "-o",
                        "C:\\Tmp\\summary_log23.txt");
            pb.directory(new File("c:\\Tools\\Diff_tool\\"));*/
            processBuilder.directory(new File(commandPathDirTool));

            //System.out.println("command " + processBuilder.command().toArray());
            processBuilder.command().forEach(System.out::println);
            process = processBuilder.inheritIO().start();
           wf = process.waitFor();
           //System.out.println("waitFor = " + wf);

            /*BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String rs = builder.toString();*/
            //System.out.println("result = " + rs);

        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }


    final Runnable taskGo = new Runnable(){
        public void run() {
            try {
                doGo();

                if(process.waitFor() == 0) {
                    JOptionPane.showMessageDialog(container, "Done!!!");

                    if (openAfter){
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        openLogFile(String.format("%s\\%s", commandPathDirOut, nameOut));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public LongRunProcess(){
    }

    public Process getProcess() {
        return process;
    }

    public int getWfValue(){
        return wf;
    }

    public LongRunProcess addPathDirTool(String path){
        this.commandPathDirTool = path;
        return this;
    }

    public LongRunProcess addPathDirOut(String path){
        this.commandPathDirOut = path;
        return this;
    }

    public LongRunProcess addCommandPath1(String path){
        this.commandPath1 = path;
        return this;
    }

    public LongRunProcess addCommandPath2(String path){
        this.commandPath2 = path;
        return this;
    }

    public LongRunProcess addNameOut(String name){
        this.nameOut = name;
        return this;
    }

    public LongRunProcess addFullReport(String name){
        this.fullReport = name;
        return this;
    }

    public LongRunProcess addNameToll(String name){
        this.nameTool = name;
        return this;
    }

    public LongRunProcess addContainer(JFrame mainFrame) {
        this.container = mainFrame;
        return this;
    }

    public LongRunProcess addOpenAfter(boolean openAfter) {
        this.openAfter = openAfter;
        return this;
    }

    private void openLogFile(@NotNull String fullName){
        try {
            String string  = fullName.replace("\\","\\\\");
            Process p = Runtime.getRuntime().exec(String.format("notepad %s", string));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}