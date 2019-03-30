package nokori.clear_dialogue.ui;

import java.io.IOException;

import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.widget.assembly.WidgetContainer;
import nokori.clear.windows.Window;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.widget.node.ConnectionRendererWidget;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 */
public class SharedResources {

	private Window window;
	private NanoVGContext context;
	
	/*
	 * Dialogue Data
	 */
	private Project project = new Project();
	
	/*
	 * GUI Data
	 */
	
	private Font notoSans, notoSerif;
	private String contextHint;
	private WidgetContainer toolbar;
	private ClearDialogueCanvas canvas;
	private ConnectionRendererWidget connectionRenderer;
	
	public void init(Window window, NanoVGContext context) {
		this.window = window;
		this.context = context;
		
		resetContextHint();
		
		try {
			notoSans = new Font("res/fonts/NotoSans/", "NotoSans-Regular", "NotoSans-Bold", "NotoSans-Italic", "NotoSans-Light").load(context);
			notoSerif = new Font("res/fonts/NotoSerif/", "NotoSerif-Regular", "NotoSerif-Bold", "NotoSerif-Italic", "NotoSerif-Light").load(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Window getWindow() {
		return window;
	}

	public NanoVGContext getNanoVGContext() {
		return context;
	}

	public WidgetContainer getToolbar() {
		return toolbar;
	}

	public void setToolbar(WidgetContainer toolbar) {
		this.toolbar = toolbar;
	}

	public ClearDialogueCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(ClearDialogueCanvas canvas) {
		this.canvas = canvas;
	}

	public ConnectionRendererWidget getConnectionRenderer() {
		return connectionRenderer;
	}

	public void setConnectionRenderer(ConnectionRendererWidget connectionRenderer) {
		this.connectionRenderer = connectionRenderer;
	}

	/**
	 * Gets the current context hint visible at the bottom of the screen. Context hints give contextual information on how to use the IDE.
	 * @return
	 */
	public String getContextHint() {
		return contextHint;
	}

	/**
	 * Sets the current context hint.
	 * @param contextHint
	 */
	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
	
	/**
	 * Resets the context hint back to the general controls for navigating the canvas.
	 */
	public void resetContextHint() {
		contextHint = "Drag LMB = Pan Canvas & Drag Nodes";
	}

	/**
	 * Gets the currently active JDialogue Project.
	 * @return
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Sets a new JDialogue Project and refreshes the Canvas with its data.
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	public Font getNotoSans() {
		return notoSans;
	}

	public Font getNotoSerif() {
		return notoSerif;
	}
}
