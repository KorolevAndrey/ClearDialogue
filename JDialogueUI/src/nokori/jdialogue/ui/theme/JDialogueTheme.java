package nokori.jdialogue.ui.theme;

import lwjgui.Color;
import lwjgui.scene.layout.Font;
import lwjgui.theme.Theme;

public class JDialogueTheme extends Theme {

	private Font sans, serif;
	
	public JDialogueTheme() {
		sans = new Font("res/fonts/noto_sans/", "NotoSans-Regular.ttf", "NotoSans-Bold.ttf", "NotoSans-Italic.ttf", "NotoSans-Light.ttf");
		serif = new Font("res/fonts/noto_serif/", "NotoSerif-Regular.ttf", "NotoSerif-Bold.ttf", "NotoSerif-Italic.ttf", "NotoSerif-Light.ttf");
	}
	
	public Font getSansFont() {
		return sans;
	}

	public Font getSerifFont() {
		return serif;
	}

	@Override
	public Color getBackground() {
		return Color.WHITE_SMOKE;
	}
	
	@Override
	public Color getPane() {
		return Color.WHITE;
	}

	@Override
	public Color getSelection() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public Color getSelectionAlt() {
		return getSelection().brighter(0.9);
	}

	@Override
	public Color getSelectionPassive() {
		return Color.CORAL;
	}

	@Override
	public Color getShadow() {
		return new Color(32, 32, 32, 100);
	}

	@Override
	public Color getText() {
		return Color.DARK_GRAY;
	}

	@Override
	public Color getTextAlt() {
		return Color.WHITE_SMOKE;
	}
	
	@Override
	public Color getControl() {
		return Color.CORAL;
	}

	@Override
	public Color getControlAlt() {
		return Color.WHITE_SMOKE;
	}

	@Override
	public Color getControlOutline() {
		return Color.CORAL;
	}

	@Override
	public Color getControlHover() {
		return Color.DIM_GRAY;
	}
}
