package nokori.clear_dialogue.ui.widget.node;

import java.util.Stack;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.util.NanoVGScaler;
import nokori.clear.vg.widget.HalfCircleWidget;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.Window;
import nokori.clear.windows.Cursor.Type;
import nokori.clear_dialogue.project.DialogueConnector;

import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

public class ConnectorWidget extends HalfCircleWidget {
	public static final int CONNECTOR_RADIUS = 20;
	
	public enum ConnectorType {
		IN(new ClearColor(228, 80, 65, 255).immutable(true)),
		OUT(new ClearColor(52, 205, 112, 255).immutable(true));
		
		private ClearColor color;
		
		private ConnectorType(ClearColor color) {
			this.color = color;
		}
		
		public Orientation getOrientation() {
			switch(this) {
			case OUT:
				return Orientation.RIGHT;
			case IN:
			default:
				return Orientation.LEFT;
			}
		}
	};
	
	private DialogueConnector connector;
	private ConnectorType connectorType;

	private boolean selected = false;
	
	public ConnectorWidget(NanoVGScaler scaler, DialogueConnector connector, ConnectorType connectorType) {
		this(scaler, connector, connectorType, CONNECTOR_RADIUS, true);
	}
	
	public ConnectorWidget(NanoVGScaler scaler, DialogueConnector connector, ConnectorType connectorType, float radius, boolean addWidgetClip) {
		super(radius, connectorType.color.copy(), connectorType.getOrientation());
		this.connector = connector;
		this.connectorType = connectorType;
		
		setScaler(scaler);
		
		if (addWidgetClip) {
			switch(connectorType) {
			case OUT:
				addChild(new WidgetClip(WidgetClip.Alignment.CENTER_RIGHT, radius, 0f));
				break;
			case IN:
			default:
				addChild(new WidgetClip(WidgetClip.Alignment.CENTER_LEFT, -radius, 0f));
				break;
			}
		}
		
		setOnMouseEnteredEvent(e -> {
			new FillTransition(TRANSITION_DURATION, getFill(), connectorType.color.multiply(1.2f)).play();
			
			if (ClearStaticResources.isFocusedOrCanFocus(this) || ClearStaticResources.getFocusedWidget() instanceof ConnectorWidget) {
				ClearStaticResources.getCursor(Type.HAND).apply(e.getWindow());
				
				if (ClearStaticResources.getFocusedWidget() == null) {
					ClearStaticResources.setFocusedWidget(this);
				}
			}
		});
		
		setOnMouseExitedEvent(e -> {
			new FillTransition(TRANSITION_DURATION, getFill(), connectorType.color).play();
			
			if (ClearStaticResources.isFocusedOrCanFocus(this) && !selected) {
				ClearStaticResources.getCursor(Type.ARROW).apply(e.getWindow());
				ClearStaticResources.setFocusedWidget(null);
			} else if (ClearStaticResources.getFocusedWidget() instanceof ConnectorWidget){
				ClearStaticResources.getCursor(Type.HORIZONTAL_RESIZE).apply(e.getWindow());
			}
		});
		
		setOnMouseButtonEvent(e -> {
			if (!e.isConsumed() && e.isPressed()) {
				select(e.getWindow());
			}
		});
	}
	
	private void select(Window window) {
		/*
		 * Selecting functionality
		 */
		
		if (selected) {
			
			//Check if the mouse is hovering over a connector; if so, then ignore the de-select and let the other connector handle it.
			Stack<Widget> intersecting = parent.getParent().getChildrenWithinMouse(window);
			
			while(!intersecting.isEmpty()) {
				Widget w = intersecting.pop();

				if (w != this && w instanceof ConnectorWidget) {
					return;
				}
			}
			
			//The mouse wasn't hovering over another connector, so we can go ahead and just cancel the selection
			endSelecting(window);
			
		} else if (isMouseIntersectingThisWidget(window)) {
			
			Widget w = ClearStaticResources.getFocusedWidget();

			//Enable connector selection mode if there isn't a focused widget
			if (ClearStaticResources.isFocusedOrCanFocus(this)) {
				beginSelecting(window);
			}
			
			//Connect to the focused ConenctorWidget if applicable
			if (w != this && w instanceof ConnectorWidget) {
				connect((ConnectorWidget) w);
			}
		}
	}
	
	private void beginSelecting(Window window) {
		connector.disconnectAll();
		selected = true;
		ClearStaticResources.getCursor(Type.HORIZONTAL_RESIZE).apply(window);
	}
	
	/**
	 * Ends selecting mode for this Connector and unfocuses it
	 */
	private void endSelecting(Window window) {
		selected = false;
		ClearStaticResources.setFocusedWidget(null);
		
		if (window != null) {
			ClearStaticResources.getCursor(Type.ARROW).apply(window);
		}
	}
	
	/**
	 * Connects this Connector to whichever one is currently selected
	 */
	private void connect(ConnectorWidget connectWith) {
		//Out Connectors can only have one connection at a time. In connectors can have as many connections as needed.
		if (connectWith.connectorType == ConnectorType.OUT) {
			connectWith.connector.disconnectAll();
		}
		
		//Connect the two connectors
		connector.connect(connectWith.connector);
		
		//End selecting
		connectWith.endSelecting(null);
	}
	

	public DialogueConnector getConnector() {
		return connector;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	public boolean isSelected() {
		return selected;
	}
}
