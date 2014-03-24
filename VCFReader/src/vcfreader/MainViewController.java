package vcfreader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author Pascual Lorente Arencibia
 */
public class MainViewController {

    @FXML private VBox infoFilters;
    @FXML private TextField filter;
    @FXML private Label lines;
    @FXML private ScrollPane tableContainer;
    @FXML private TextField chromosome;
    @FXML private TextField posFrom;
    @FXML private TextField posTo;
    @FXML private TextField idFilter;
    @FXML private TextField qualMax;
    @FXML private TextField qualMin;
    @FXML private ProgressBar progress;
    @FXML private Button saveButton;
    private VCFDataParser parser;
    private VCFData data;
    private List<Filter> filters;
    private TableView<Variant> variantsTable;
    private static File lastPath;

    public static final String VCF_EXTENSION = ".vcf";
    public static final String VCF_DESCRIPTION = "Variant Call Format";
    public static final String[] VCF_FILTERS = new String[]{"*.vcf"};

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        saveButton.setDisable(true);
        lines.setText("");
        filters = new ArrayList<>();
        switch (System.getProperty("os.name")) {
            case "Windows 7":
                if (System.getenv("user.dir") != null) {
                    lastPath = new File(System.getenv("user.dir"));
                }
                break;
            case "Linux":
            default:
                lastPath = new File(System.getenv("PWD"));
        }
    }

    @FXML
    private void openFile() {
        File f = openVCF(VCFReader.getStage());
        if (f != null) {
            VCFReader.setTitle(f.getAbsolutePath());
            parser = new VCFDataParser(f.getAbsolutePath());
            parser.setOnSucceeded((WorkerStateEvent t) -> {
                restartTable();
            });
            progress.progressProperty().bind(parser.progressProperty());
            lines.textProperty().bind(parser.messageProperty());
            new Thread(parser).start();
        }
    }

    @FXML
    private void save() {
        File f = saveVCF(VCFReader.getStage());
        data.exportVCF(f.getAbsolutePath());
    }

    private void loadInfoFilters() {
        filters.clear();
        infoFilters.getChildren().clear();
        for (int index = 0; index < data.getInfos().size(); index++) {
            Map<String, String> info = data.getInfos().get(index);
            // Label title = new Label(info.get("ID"));
            //title.setTooltip(new Tooltip(info.get("Description")));
            switch (info.get("Type")) {
                case "Integer":
                case "Float":
                    TextField max = new TextField();
                    max.setPromptText("Max");
                    max.setPrefWidth(80);
                    max.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    TextField min = new TextField();
                    min.setPromptText(info.get("ID"));
                    min.setPrefWidth(80);
                    min.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    HBox hbox = new HBox(min, max);
                    hbox.setSpacing(5);
                    infoFilters.getChildren().addAll(hbox);
                    filters.add(new Filter("numeric", index, min, max));
                    break;
                case "Character":
                case "String":
                    TextField value = new TextField();
                    value.setPromptText(info.get("ID"));
                    value.setPrefWidth(165);
                    value.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    infoFilters.getChildren().addAll(value);
                    filters.add(new Filter("text", index, value));
                    break;
                case "Flag":
                    ToggleButton yes = new ToggleButton("Yes");
                    ToggleButton no = new ToggleButton("No");
                    yes.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    no.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    ToggleGroup group = new ToggleGroup();
                    group.getToggles().addAll(yes, no);
                    Label title = new Label(info.get("ID"));
                    HBox box = new HBox(title, yes, no);
                    box.setSpacing(5);
                    box.setAlignment(Pos.CENTER);
                    infoFilters.getChildren().addAll(box);
                    filters.add(new Filter("flag", index, yes, no));
                    break;
            }
        }
    }

    @FXML
    private void filter() {
        if (data == null) {
            return;
        }
        data.reset();
        // CHROM
        if (!chromosome.getText().isEmpty()) {
            data.filterChrom(false, chromosome.getText());
        }
        // POS
        if (!posFrom.getText().isEmpty() && !posTo.getText().isEmpty()) {
            try {
                int min = Integer.valueOf(posFrom.getText());
                int max = Integer.valueOf(posTo.getText());
                data.filterPos(true, min, max);
            } catch (NumberFormatException ex) {
                System.err.println("Bad number format in Position");
            }
        }
        // QUAL
        if (!qualMin.getText().isEmpty() && !qualMax.getText().isEmpty()) {
            try {
                double min = Double.valueOf(qualMin.getText());
                double max = Double.valueOf(qualMax.getText());
                data.filterQual(true, min, max);
            } catch (NumberFormatException ex) {
                System.err.println("Bad number format in Quality");
            }
        }
        // ID
        if (!idFilter.getText().isEmpty()) {
            data.filterId(true, idFilter.getText());
        }
        // FILTER
//        data.filterFilter(true, selectedFilters());
        if (!filter.getText().isEmpty()) {
            data.filterFilter(true, filter.getText());
        }
        // INFOS
        for (Filter f : filters) {
            switch (f.type) {
                case "numeric":
                    String minString = ((TextField) f.nodes[0]).getText();
                    String maxString = ((TextField) f.nodes[1]).getText();
                    if (!minString.isEmpty() && !maxString.isEmpty()) {
                        try {
                            double max = Double.valueOf(maxString);
                            double min = Double.valueOf(minString);
                            data.filterInfoNumeric(true, f.index, min, max);

                        } catch (NumberFormatException ex) {
                            System.err.println("Bad number: " + minString + " or " + maxString);
                        }
                    }
                    break;
                case "text":
                    String value = ((TextField) f.nodes[0]).getText();
                    if (!value.isEmpty()) {
                        data.filterInfoText(true, f.index, value, false);
                    }
                    break;
                case "flag":
                    if (((ToggleButton) f.nodes[0]).isSelected()) {
                        data.filterInfoFlag(true, f.index, true);
                    } else if (((ToggleButton) f.nodes[1]).isSelected()) {
                        data.filterInfoFlag(true, f.index, false);
                    }
            }
        }
        variantsTable.getItems().setAll(FXCollections.observableArrayList(
                data.getCached()));
        lines.setText(data.getCached().size() + " (total: " + data.
                getVariants().size() + ")");
    }

    private void restartTable() {
        try {
            data = parser.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        TableView<Variant> table = new TableView<>(FXCollections.
                observableArrayList(data.getVariants()));
        TableColumn<Variant, String> chrColumn = new TableColumn<>("CHR");
        TableColumn<Variant, Integer> posColumn = new TableColumn<>("POS");
        TableColumn<Variant, String> idColumn = new TableColumn<>("ID");
        TableColumn<Variant, String> refColumn = new TableColumn<>("REF");
        TableColumn<Variant, String> altColumn = new TableColumn<>("ALT");
        TableColumn<Variant, Double> qualColumn = new TableColumn<>("QUAL");
        TableColumn<Variant, String> filterColumn = new TableColumn<>("FILTER");
        TableColumn infoColumn = new TableColumn("INFO");
        chrColumn.setCellValueFactory(new PropertyValueFactory<>("chrom"));
        posColumn.setCellValueFactory(new PropertyValueFactory<>("pos"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        refColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        qualColumn.setCellValueFactory(new PropertyValueFactory<>("qual"));
        filterColumn.setCellValueFactory(new PropertyValueFactory<>("filter"));
        // Fill info column
        for (int i = 0; i < data.getInfos().size(); i++) {
            final int index = i;
            TableColumn<Variant, String> iColumn = new TableColumn<>(data.getInfos().get(i).
                    get("ID"));
            iColumn.setCellValueFactory((TableColumn.CellDataFeatures<Variant, String> p)
                    -> new SimpleStringProperty(p.getValue().getInfos().get(index)));
            infoColumn.getColumns().add(iColumn);
        }
        table.getColumns().setAll(chrColumn, posColumn, idColumn,
                refColumn, altColumn, qualColumn, filterColumn, infoColumn);
        // Fill formats
        if (!data.getSamples().isEmpty()) {
            TableColumn fColumn = new TableColumn("FORMAT");
            for (int i = 0; i < data.getSamples().size(); i++) {
                TableColumn sColumn = new TableColumn(data.getSamples().get(i));
                for (int j = 0; j < data.getFormats().size(); j++) {
                    TableColumn<Variant, String> qColumn = new TableColumn<>(data.getFormats().
                            get(j).get("ID"));
                    final int sIndex = i;
                    final int fIndex = j;
                    qColumn.setCellValueFactory((
                            TableColumn.CellDataFeatures<Variant, String> p) -> {
                        return new SimpleStringProperty(p.getValue().getSamples().get(sIndex).get(
                                fIndex));
                    });
                    sColumn.getColumns().add(qColumn);
                }
                fColumn.getColumns().add(sColumn);
            }
            table.getColumns().add(fColumn);
        }
        table.setSortPolicy((TableView<Variant> p) -> false);
        tableContainer.setContent(table);
        variantsTable = table;
        lines.textProperty().unbind();
        progress.progressProperty().unbind();
        progress.setProgress(0);
        lines.setText(data.getVariants().size() + " (total: " + data.getVariants().size() + ")");
//        loadContigFilters();
        loadInfoFilters();
        saveButton.setDisable(false);
    }

    /**
     * Opens a dialog to select a VCF file.
     *
     * @param parent The parent window to block.
     * @return the file selected by the user, or null if the operation was canceled.
     */
    public static File openVCF(Window parent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Variant Call Format File");
        fileChooser.setInitialDirectory(lastPath);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(VCF_DESCRIPTION, VCF_FILTERS));
        File file = fileChooser.showOpenDialog(parent);
        if (file != null) {
            lastPath = file.getParentFile();
            return file;
        }
        return null;
    }

    /**
     * Opens a dialog for the user to create a Variant Call File (.vcf). The file is not created
     * immediately, just stored as text.
     *
     * @param parent the parent window to block
     * @return the file or null if user canceled
     */
    public static File saveVCF(Window parent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Variant Call Format File");
        fileChooser.setInitialDirectory(lastPath);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(VCF_DESCRIPTION,
                VCF_FILTERS));
        File file = fileChooser.showSaveDialog(parent);
        if (file != null) {
            lastPath = file.getParentFile();
            // Add extension to bad named files
            return file.getAbsolutePath().endsWith(".vcf") ? file : new File(file.getAbsolutePath()
                    + ".vcf");
        }
        return null;
    }

    class Filter {

        private final String type;
        private final Node[] nodes;
        private final int index;

        public Filter(String type, int index, Node... nodes) {
            this.type = type;
            this.nodes = nodes;
            this.index = index;
        }

        public Node[] getNodes() {
            return nodes;
        }

        public String getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }

    }

}
