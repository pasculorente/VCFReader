package vcfreader;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Pascual Lorente Arencibia
 */
public class VariantCellController {
    @FXML
    private Label chrom;
    @FXML
    private Label position;
    @FXML
    private Label id;
    @FXML
    private Label refalt;
    @FXML
    private Label qual;
    @FXML
    private Label filter;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // TODO
    }

    public Label getChrom() {
        return chrom;
    }

    public Label getFilter() {
        return filter;
    }

    public Label getId() {
        return id;
    }

    public Label getPosition() {
        return position;
    }

    public Label getQual() {
        return qual;
    }

    public Label getRefalt() {
        return refalt;
    }

}
