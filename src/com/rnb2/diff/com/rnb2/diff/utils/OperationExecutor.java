package com.rnb2.diff.com.rnb2.diff.utils;

import java.awt.Container;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

//import org.apache.log4j.Logger;

//import com.azovstal.ugdt.resources.windows.MainWin;

public class OperationExecutor {
	
	//protected static final Logger log = Logger.getLogger(OperationExecutor.class);
	
	private static OperationExecutor instance = new OperationExecutor();
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private volatile boolean running;
	
	private OperationExecutor(){
	}
	
	public static OperationExecutor getInstance(){
		return instance;
	}
	
	public void execute(final Runnable task){
		this.execute(task, null, null);
	}
	
	public void execute(final Runnable task, final Runnable oncomplete){
		this.execute(task, null, oncomplete);
	}
	
	public void execute(final Runnable task, Container container){
		this.execute(task, container, null);	
	}
			
	
	public void execute(final Runnable task, Container container, final Runnable oncomplete){

		if (running){
			//TODO
			throw new RuntimeException("Ещё не выполнилась предыдущая операция");
		}
		
		running = true;
		
		final InfiniteProgressPanel progressPanel = new InfiniteProgressPanel("Подождите...", 14, 0.5f, 7, 10);

		OperationExecutor.setGlassPane(container, progressPanel);
		Runnable runnable = new Runnable(){
			public void run() {
				try{
					task.run();
				}catch(RuntimeException e){e.printStackTrace();}	
				finally{
					progressPanel.stop();
					running = false;
					if (oncomplete != null){
						SwingUtilities.invokeLater(oncomplete);
					}
				}
			}};
			
		progressPanel.start();
		executor.execute(runnable);
		
	}
	
	private static void setGlassPane(Container c, InfiniteProgressPanel panel){
		if (c instanceof JInternalFrame){
			((JInternalFrame)c).setGlassPane(panel);
		}else if (c instanceof JDialog){
			((JDialog)c).setGlassPane(panel);	
		}else if (c instanceof JFrame){
			((JFrame)c).setGlassPane(panel);			
		}
		c.validate();
	}
	
}
