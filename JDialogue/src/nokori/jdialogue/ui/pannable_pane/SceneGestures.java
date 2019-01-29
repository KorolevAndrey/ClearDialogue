package nokori.jdialogue.ui.pannable_pane;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import nokori.jdialogue.ui.node.DialogueNodePane;

/**
 * Listeners for making the scenes canvas draggable and zoomable
 * 
 * Found here:
 * https://stackoverflow.com/questions/32220042/pick-and-move-a-node-in-a-pannable-zoomable-pane
 * 
 */
public class SceneGestures {

	private static final double MAX_SCALE = 1.0;
	private static final double MIN_SCALE = 0.1;

	private DragContext sceneDragContext = new DragContext();

	private PannablePane pannablePane;
	private RectangleHighlightNode mouseHighlighter = null;
	
	public SceneGestures(PannablePane pannablePane) {
		this.pannablePane = pannablePane;
	}

	public DragContext getSceneDragContext() {
		return sceneDragContext;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}
	
	public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
		return onMouseReleasedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {
			//LMB -> Panning
			if (event.isPrimaryButtonDown() && !event.isSecondaryButtonDown()) {
				sceneDragContext.mouseAnchorX = event.getSceneX();
				sceneDragContext.mouseAnchorY = event.getSceneY();
			
				sceneDragContext.translateAnchorX = pannablePane.getTranslateX();
				sceneDragContext.translateAnchorY = pannablePane.getTranslateY();
			}
			
			//RMB -> Highlight
			if (event.isSecondaryButtonDown() && !event.isPrimaryButtonDown()) {
				setMouseHighlighter(new RectangleHighlightNode(pannablePane.getScaledMouseX(event), pannablePane.getScaledMouseY(event)));
			}
		}

	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			setMouseHighlighter(null);
			
			for (int i = 0; i < pannablePane.getChildren().size(); i++) {
				Node node = pannablePane.getChildren().get(i);
				
				if (node instanceof DialogueNodePane) {
					DialogueNodePane dNode = (DialogueNodePane) node;
					dNode.setMultiSelected(false);
				}
			}
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			//LMB -> Panning
			if (event.isPrimaryButtonDown() && !event.isSecondaryButtonDown()) {
				double newTranslateX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
				double newTranslateY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;
				
				pannablePane.setTranslateX(newTranslateX);
				pannablePane.setTranslateY(newTranslateY);
	
				mouseDragged(event, newTranslateX, newTranslateY);
			}
			
			//RMB -> Highlight
			if (event.isSecondaryButtonDown() && !event.isPrimaryButtonDown()) {
				mouseHighlighter.update(pannablePane.getScaledMouseX(event), pannablePane.getScaledMouseY(event));
			}
			
			event.consume();
		}
	};
	
	private void setMouseHighlighter(RectangleHighlightNode h) {
		if (mouseHighlighter != null) {
			pannablePane.getChildren().remove(mouseHighlighter);
		}
		
		mouseHighlighter = h;
		
		if (h != null) {
			pannablePane.getChildren().add(mouseHighlighter);
		}
	}
	
	public void mouseDragged(MouseEvent event, double newTranslateX, double newTranslateY) {
		
	}

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {

			double delta = 1.2;

			double scale = pannablePane.getScale(); // currently we only use Y, same value is used for X
			double oldScale = scale;

			if (event.getDeltaY() < 0) {
				scale /= delta;
			} else {
				scale *= delta;
			}

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;

			double dx = (event.getSceneX() - (pannablePane.getBoundsInParent().getWidth()/2 + pannablePane.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY() - (pannablePane.getBoundsInParent().getHeight()/2 + pannablePane.getBoundsInParent().getMinY()));

			pannablePane.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			pannablePane.setPivot(f * dx, f * dy);
			
			mouseScrolled(event, scale);

			event.consume();
		}

	};
	
	public void mouseScrolled(ScrollEvent event, double newScale) {
		
	}

	private static double clamp(double value, double min, double max) {

		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}
}