package nokori.jdialogue.ui.node;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.ui.editor.DialogueResponseNodeEditor;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc.ConnectorType;
import nokori.jdialogue.ui.pannable_pane.PannablePane;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueResponseNodePane extends DialogueNodePane{
	
	private Group labelGroup = null;
	private Group connectorGroup = null;
	
	private Font textFont;
	private int incrementH;
	
	public DialogueResponseNodePane(JDialogueCore core, DialogueResponseNode node, DropShadow shadow, Font titleFont, Font textFont, int incrementH) {
		super(core, node, shadow, titleFont);
		
		this.textFont = textFont;
		this.incrementH = incrementH;
		
		//Open Editor
		setOnMouseClicked(event -> {
			if (checkDispose(event, core)) {
				return;
			}
			
			if (event.getClickCount() > 1) {
				core.getUIPane().getChildren().add(new DialogueResponseNodeEditor(core, node, this, titleFont, textFont));
			}
		});
		
		refresh(core);
	}
	
	@Override
	public void refresh(JDialogueCore core) {
		super.refresh(core);
		
		//Remove old connectors and labels to replace with new ones
		if (labelGroup != null) {
			getChildren().remove(labelGroup);
		}
		
		if (connectorGroup != null) {
			getChildren().remove(connectorGroup);
		}
		
		labelGroup = new Group();
		connectorGroup = new Group();
		
		ArrayList<Response> responses = ((DialogueResponseNode) node).getResponses();
		
		//The ten is to give the bottom extra bounding space
		int extendedH = Math.max(TITLE_HEIGHT + 10 + (responses.size() * incrementH), HEIGHT);
		int connectorRadius = (int) (incrementH * (1.0/3.0));
		
		for (int i = 0; i < responses.size(); i++) {
			Response response = responses.get(i);
			
			int y = (i * incrementH);
			
			/*
			 * Node Connector
			 */
			
			DialogueNodeConnectorArc outConnector = new DialogueNodeConnectorArc(core, this, ConnectorType.OUT, response.getOutConnector(), connectorRadius);
			outConnector.setLayoutY(y);
			
			connectorGroup.getChildren().add(outConnector);
			
			/*
			 * Label
			 */
			
			Label label = new Label(response.getText());
			label.setFont(textFont);
			label.setMaxWidth(WIDTH - 20f);
			label.setTextFill(Color.BLACK);
			label.setMouseTransparent(true);
			
			label.setLayoutY(y);
			
			labelGroup.getChildren().add(label);
		}
		
		labelGroup.setTranslateY(TITLE_HEIGHT/2);
		
		connectorGroup.setTranslateX(WIDTH/2 + (int) (incrementH * 0.18));
		connectorGroup.setTranslateY(TITLE_HEIGHT/2);
		
		getChildren().add(labelGroup);
		getChildren().add(connectorGroup);
		
		setBackgroundHeight(extendedH);
		
		requestLayout();
		
		//Refresh existing lines
		//BoundLines are checked to see if their connected to now outdated Arcs, if so, they're updated to use the new one
		//We then add the line to a cache that's called when this layout is re-ordered so that the positioning will also be updated
		PannablePane pannablePane = core.getPannablePane();
		
		for (int i = 0; i < connectorGroup.getChildren().size(); i++) {
			DialogueNodeConnectorArc connector = (DialogueNodeConnectorArc) connectorGroup.getChildren().get(i);
			Response response = responses.get(i);
			
			for (int j = 0; j < pannablePane.getChildren().size(); j++) {
				Node node = pannablePane.getChildren().get(j);
				
				if (node instanceof BoundLine) {
					BoundLine boundLine = (BoundLine) node;
	
					if (boundLine.getConnector1() == response.getOutConnector()) {
						boundLine.setNode1(connector, response.getOutConnector());
					}
					
					if (boundLine.getConnector2() == response.getOutConnector()) {
						boundLine.setNode2(connector, response.getOutConnector());
					}
				}
			}
		}
	}
}
