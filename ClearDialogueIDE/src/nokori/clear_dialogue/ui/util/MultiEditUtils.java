package nokori.clear_dialogue.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.apps.ClearInputApp;
import nokori.clear.windows.GLFWException;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

public class MultiEditUtils {
	
	private static final int INPUT_WIDTH = 300;
	private static final int INPUT_HEIGHT = 200;
	
	private static final File FONT_LOCATION = new File("res/fonts/NotoSans/");
	private static final ClearColor BUTTON_OUTLINE_FILL = ClearColor.CORAL;
	
	public static void addTagsToAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		
		String title = "Multi-Tag (Insertion)";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH, INPUT_HEIGHT, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				title,
				"Input a tag to insert into all of the selected DialogueNodes.\n\n*Note: Spaces must be added manually.",
				"New Tag") {

			@Override
			protected void confirmButtonPressed(String tags) {
				if (tags != null) {
					int additions = 0;
					
					for (int i = 0; i < nodes.size(); i++) {
						DraggableDialogueWidget w = nodes.get(i);
						Dialogue d = w.getDialogue();
						d.setTags(d.getTags() + tags);
						additions++;
					}
					
					TinyFileDialog.showMessageDialog(title, tags + " was inserted successfully into " + additions + " nodes.", TinyFileDialog.Icon.INFORMATION);
					
					sharedResources.refreshCanvas();
				} else {
					TinyFileDialog.showMessageDialog(title, "No tag was inputted.", TinyFileDialog.Icon.ERROR);
				}
			}
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeTagsFromAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		String title = "Multi-Tag (Removal)";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH, INPUT_HEIGHT, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				title,
				"Input a tag to remove from all of the selected DialogueNodes.\n\n*Note: Spaces must be added manually.",
				"") {

			@Override
			protected void confirmButtonPressed(String tags) {
				if (tags != null) {
					int removals = 0;
					
					for (int i = 0; i < nodes.size(); i++) {
						DraggableDialogueWidget w = nodes.get(i);
						Dialogue d = w.getDialogue();
						
						if (d.getTags().contains(tags)) {
							d.setTags(d.getTags().replace(tags, ""));
							removals++;
						}
					}
					
					if (removals > 0) {
						TinyFileDialog.showMessageDialog(title, "\"" + tags + "\" was removed successfully from " + removals + " nodes.", TinyFileDialog.Icon.INFORMATION);
						sharedResources.refreshCanvas();
					} else {
						TinyFileDialog.showMessageDialog(title, "\"" + tags + "\" wasn't found in any nodes.", TinyFileDialog.Icon.INFORMATION);
					}

				} else {
					TinyFileDialog.showMessageDialog(title, "No tag was inputted.", TinyFileDialog.Icon.ERROR);
				}
			}
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
		}
	}
	
	public static void multiTitle(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		/*
		 * Sort the list based on X/Y coordinates so that if the #NUM tag is used, the numbers appear in descending order
		 */
		
		Collections.sort(nodes, new Comparator<DraggableDialogueWidget>() {
			public int compare(DraggableDialogueWidget w1, DraggableDialogueWidget w2) {
				Dialogue n1 = w1.getDialogue();
				Dialogue n2 = w2.getDialogue();

				int result = Float.compare(n1.getY(), n2.getY());
				
				if (result == 0) {
					result = Float.compare(n1.getX(), n2.getX());
				}
				
				return result;
			}
		});

		/*
		 * Begin renaming process
		 */
		
		String windowTitle = "Multi-Title";
		String autoNumTag = "[[#NUM]]";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH + 100, INPUT_HEIGHT + 50, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				windowTitle,
				"Input a title for the selected dialogue nodes."
						+ "\n\n*Note: You can add " + autoNumTag + " to the name to automatically add the node's number to the name."
						+ "\n\nFor example, Node " + autoNumTag + " would result in Node 1, Node 2, so on.",
				"New Title [[#NUM]]") {

			@Override
			protected void confirmButtonPressed(String title) {
				int numNames = 0;
						
				for (int i = 0; i < nodes.size(); i++) {
					DraggableDialogueWidget w = nodes.get(i);
					Dialogue d = w.getDialogue();
						
					d.setTitle(title.replace(autoNumTag, Integer.toString(numNames + 1)));
					numNames++;
				}
					
				TinyFileDialog.showMessageDialog(windowTitle, "\"" + title + "\" was set on " + numNames + " nodes.", TinyFileDialog.Icon.INFORMATION);
					
				sharedResources.refreshCanvas();
			}
			
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
		}
	}
}