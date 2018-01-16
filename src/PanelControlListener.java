import javafx.scene.control.Control;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;

/**
 * Created by jamal on 19/07/2017.
 */
class PanelControlListener {
    private static InnerShadow errorShadow = new InnerShadow(7, javafx.scene.paint.Color.RED);
    private final Control parent;

    PanelControlListener(Control parent) {
        this.parent = parent;
        errorShadow.setWidth(15);
        errorShadow.setHeight(15);
        errorShadow.setBlurType(BlurType.ONE_PASS_BOX);
        errorShadow.setChoke(0.3);
    }

    void setHighlighted(boolean highlighted) {
        boolean highlighted1 = highlighted;
        if (highlighted) {
            parent.setEffect(errorShadow);
        } else {
            parent.setEffect(null);
        }
    }
}
