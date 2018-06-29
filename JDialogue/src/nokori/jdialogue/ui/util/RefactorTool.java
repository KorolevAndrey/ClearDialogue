package nokori.jdialogue.ui.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import nokori.jdialogue.io.JDialogueIO;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.io.JDialogueSerializerIO;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;

/**
 * 
 * A useful tool for mass find/replace of text in dialogue files.
 * 
 * It supports all supported filetypes of JDialogue, including .dialogue and .json.
 *
 */
public class RefactorTool {
	public static void run(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Project Files");
		fileChooser.setInitialDirectory(new File("."));
		
		List<File> files = fileChooser.showOpenMultipleDialog(stage);

		if (files != null && !files.isEmpty()) {
			Pair<String, String> refactorInfo = openRefactorDialog(stage);
			
			if (refactorInfo == null) {
				return;
			}
			
			JDialogueIO[] ioTypes = {
					new JDialogueJsonIO(),
					new JDialogueSerializerIO()
			};
			
			//Searches files, for each file, check if it's compatible with the ioType, if so, refactor, then continue
			fileLoop:
			for (File f : files) {
				for (int i = 0; i < ioTypes.length; i++) {
					
					List<String> fileExtensions = ioTypes[i].getExtensionFilter().getExtensions();
					
					for (int j = 0; j < fileExtensions.size(); j++) {
						//Removes the * at the beginning
						String extension = fileExtensions.get(j).substring(1, fileExtensions.get(j).length());
						
						if (f.getName().endsWith(extension)) {
							refactor(f, ioTypes[i], refactorInfo.getKey(), refactorInfo.getValue());
							continue fileLoop;
						}
					}
				}
			}
			
			showInformation(stage, "Refactor successful.");
		}
	}
	
	/**
	 * Refactors the project file with the following parameters
	 */
	private static void refactor(File f, JDialogueIO io, String find, String replace) {
		try {
			Project project = io.importProject(f);
			
			for (int i = 0; i < project.getNumNodes(); i++) {
				DialogueNode node = project.getNode(i);
				
				node.setName(node.getName().replace(find, replace));
				node.setTag(node.getTag().replace(find, replace));
				
				if (node instanceof DialogueTextNode) {
					DialogueTextNode textNode = (DialogueTextNode) node;
					textNode.setText(textNode.getText().replace(find, replace));
				}
				
				if (node instanceof DialogueResponseNode) {
					DialogueResponseNode responseNode = (DialogueResponseNode) node;
					
					for (int j = 0; j < responseNode.getResponses().size(); j++) {
						Response response = responseNode.getResponses().get(j);
						
						response.setText(response.getText().replaceAll(find, replace));
					}
				}
			}
			
			//It should exist but I'm just being thorough
			if(f.exists()) {
				File output = new File(f.getParentFile(), f.getName() + ".backup");
				Files.copy(f.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			
			io.exportProject(project, f);
			
		} catch(Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Caught " + e.getClass().getSimpleName());
			alert.setHeaderText("Failed to import projects.");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}
	
	/**
	 * Opens a dialog that asks what the user wants to be found and replaced in the various project files
	 * 
	 * Pulled from: http://code.makery.ch/blog/javafx-dialogs-official/
	 */
	private static Pair<String, String> openRefactorDialog(Stage stage) {
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Refactor Projects");
		dialog.setHeaderText("Each Project will be imported, and the contained DialogueNodes will be modified."
				+ "\nAll exact instances of the \"Find\" input will be replaced with the \"Replace with\" input."
				+ "\nThe modified Project files will be backed up before saving the new versions."
				+ "\n\nExample of backup: YourProject.dialogue.backup");
		
		//Set icons
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		// Set the button types.
		ButtonType confirmButtonType = new ButtonType("Refactor", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField find = new TextField();
		TextField replace = new TextField();

		grid.add(new Label("Find:"), 0, 0);
		grid.add(find, 1, 0);
		grid.add(new Label("Replace with:"), 0, 1);
		grid.add(replace, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
		confirmButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		find.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> find.requestFocus());

		// Convert the result to a username-password-pair when the login button is
		// clicked.
		dialog.setResultConverter(dialogButton -> {
			
			if (dialogButton == confirmButtonType) {
				return new Pair<>(find.getText(), replace.getText());
			}
			
			return null;
		});

		try {
			return dialog.showAndWait().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	/**
	 * Shortcut function
	 */
	private static void showInformation(Stage stage, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Refactor Information");
		alert.setHeaderText(message);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		alert.showAndWait();
	}
}
