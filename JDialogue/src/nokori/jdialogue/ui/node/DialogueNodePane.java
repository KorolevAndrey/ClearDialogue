package nokori.jdialogue.ui.node;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc.ConnectorType;
import nokori.jdialogue.ui.util.UIUtil;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public abstract class DialogueNodePane extends StackPane {
	
	//Start/Dispose animation
	private static final int FADE_TIME = 200;
	private static final double START_ROT = 25.0;
	
	//Basic background dimension data that all nodes should share in-common
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;
	public static final int TITLE_HEIGHT = 30;
	
	private JDialogueCore core;
	
	//Instances
	protected DialogueNode node;
	private Tooltip tooltip = null;
	private InlineCssTextArea title;
	private Rectangle outline, background;
	private boolean multiSelected = false;
	
	public DialogueNodePane(JDialogueCore core, DialogueNode node, DropShadow shadow, Font titleFont) {
		this.core = core;
		this.node = node;
		
		//Outline for highlighting node
		outline = new Rectangle(WIDTH, HEIGHT);
		outline.setFill(Color.TRANSPARENT);
		outline.setStroke(Color.CORAL);
		outline.setOpacity(0.0);
		outline.setMouseTransparent(true);
		
		//Background
		background = new Rectangle(WIDTH, HEIGHT);
		background.setFill(Color.WHITE);
		background.setStroke(Color.LIGHTGRAY);
		background.setEffect(shadow);
		background.setMouseTransparent(true);
		
		//In-Connector
		DialogueNodeConnectorArc connectorArc = new DialogueNodeConnectorArc(core, this, ConnectorType.IN, node.getInConnector());
		
		//Title text
		title = new InlineCssTextArea();
		title.replaceText(node.getName());
		title.setMaxWidth(WIDTH - 20f);
		title.setMaxHeight(TITLE_HEIGHT); 
		title.setWrapText(false);
		title.setEditable(false);
		title.setMouseTransparent(true);
		
		title.setStyle("-fx-font-family: '" + titleFont.getFamily() + "'; -fx-font-size: " + titleFont.getSize() + ";"
				  	 + "-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
		
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		StackPane.setMargin(title, new Insets(10, 0, 0, 0));
		
		//Configure pane
		getChildren().addAll(background, connectorArc, title, outline);
		
		setOnMouseEntered(event -> {
			if (!multiSelected) {
				fadeOutlineIn();
			}
		});
		
		setOnMouseExited(event -> {
			if (!multiSelected) {
				fadeOutlineOut();
			}
		});

		setOnMouseClicked(event -> {
			checkDispose(event, core);
		});
		
		//Tooltip
		tooltip = new Tooltip("Drag with LMB\nDouble-click LMB to edit node.\nDouble-click RMB to delete node.\nConnect green to red connectors to link nodes.");
		tooltip.setShowDelay(Duration.seconds(JDialogueCore.TOOLTIP_SHOW_DELAY));
		Tooltip.install(this, tooltip);
		
		//Initial animation
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(FADE_TIME), this);
		rotateTransition.setFromAngle(START_ROT);
		rotateTransition.setToAngle(0.0);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), this);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
	
		//Updates connectors if the pane changes
		Platform.runLater(() -> {
			core.updateConnectors(null);
		});
	}
	
	/**
	 * Refresh nodes to have the latest Node data
	 * 
	 * Called by DialogueNodeEditor when it closes
	 */
	public void refresh(JDialogueCore core) {
		title.replaceText(node.getName());
		UIUtil.computeHighlighting(title, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
	}
	
	/**
	 * Set the text of this DialogueNodePane's tooltip
	 * @param text
	 */
	public void setTooltipText(String text) {
		tooltip.setText(text);
	}
	
	/**
	 * Checks if the right mouse button is clicking on this node, indicating a deletion request
	 */
	protected boolean checkDispose(MouseEvent event, JDialogueCore core) {
		if (event.getClickCount() > 1 && event.getButton() == MouseButton.SECONDARY) {
			dispose(core);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Begin the process of deleting this node
	 */
	protected void dispose(JDialogueCore core) {
		setMouseTransparent(true);
		
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(FADE_TIME/2), this);
		rotateTransition.setFromAngle(0.0);
		rotateTransition.setToAngle(START_ROT);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME/2), this);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
		
		fadeTransition.setOnFinished(event -> {
			if (tooltip != null) {
				tooltip.hide();
			}
			
			core.removeDialogueNode(this);
		});
	}
	
	/**
	 * Modify background height of the pane (used primarily by DialogueResponseNodePane)
	 */
	public void setBackgroundHeight(int height) {
		outline.setHeight(height);
		background.setHeight(height);
	}
	
	/**
	 * Highlights this DialogueNodePane for multi-select controls (editing multiple DialogueNodes at once)
	 */
	
	public void setMultiSelected(boolean multiSelected) {
		this.multiSelected = multiSelected;
		
		if (multiSelected) {
			fadeOutlineIn();
		} else {
			fadeOutlineOut();
		}
	}
	
	/**
	 * Sets and starts a fader the highlight outline that goes up to 1.0 from its current opacity.
	 */
	private void fadeOutlineIn() {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
		fadeTransition.setFromValue(outline.getOpacity());
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}
	
	/**
	 * Sets and starts a fader the highlight outline that goes down to 0.0 from its current opacity.
	 */
	private void fadeOutlineOut() {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
		fadeTransition.setFromValue(outline.getOpacity());
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
	}
	
	/**
	 * Tries to fetch the DialogueNodeConnectorArc for the inputted connector
	 */
	public DialogueNodeConnectorArc getDialogueNodeConnectorArcOf(DialogueNodeConnector connector) {
		return getDialogueNodeConnectorArcOf(connector, getChildren());
	}

	private DialogueNodeConnectorArc getDialogueNodeConnectorArcOf(DialogueNodeConnector connector, ObservableList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			Node node = children.get(i);
			
			DialogueNodeConnectorArc arc = null;
			
			if (node instanceof DialogueNodeConnectorArc) {
				arc = (DialogueNodeConnectorArc) node;
			}
			
			if (node instanceof Region) {
				arc = getDialogueNodeConnectorArcOf(connector, ((Region) node).getChildrenUnmodifiable());
			}
			
			if (node instanceof Group) {
				arc = getDialogueNodeConnectorArcOf(connector, ((Group) node).getChildren());
			}
		
			if (arc != null && arc.getDialogueNodeConnector() == connector) {
				return arc;
			}
		}
		
		return null;
	}
	
	public DialogueNode getDialogueNode() {
		return node;
	}
}
