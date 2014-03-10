package vcfreader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VariantCell extends ListCell<Variant> {

    private Parent node;
    private final FXMLLoader loader;
    private final VariantCellController controller;

    public VariantCell() {
        loader = new FXMLLoader(getClass().getResource("VariantCell.fxml"));
        try {
            node = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(VariantCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = loader.getController();
    }

    @Override
    protected void updateItem(Variant t, boolean bln) {
        super.updateItem(t, bln);
        if (t != null) {
            controller.getChrom().setText(t.getChrom());
            controller.getFilter().setText(t.getFilter());
            controller.getId().setText(t.getId());
            controller.getPosition().setText(t.getPos() + "");
            controller.getQual().setText(t.getQual() + "");
            setGraphic(node);
        } else {
            setGraphic(null);
        }

    }

}
