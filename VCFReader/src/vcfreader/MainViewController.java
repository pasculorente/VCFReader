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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Pascual Lorente Arencibia
 */
public class MainViewController {

    @FXML
    private TextField input;
    @FXML
    private VBox contigsFilter;
    @FXML
    private VBox filtersFilter;
    @FXML
    private VBox infoFilters;
    @FXML
    private Label lines;
    @FXML
    private ScrollPane tableContainer;
    private TableView<Variant> variantsTable;

    @FXML
    private TextField posFrom;
    @FXML
    private TextField posTo;
    @FXML
    private TextField idFilter;
    @FXML
    private TextField qualMax;
    @FXML
    private TextField qualMin;
    @FXML
    private VCFData data;
    private List<Filter> filters;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        data = new VCFData();
        lines.setText("");
        filters = new ArrayList<>();
    }

    @FXML
    private void openFile() {
        String[] filts = new String[]{"*.vcf"};
        File f = OS.
                openFile("Variant Call Format", "Variant Call Format", filts);
        if (f != null) {
            try {
                VCFDataParser parser = new VCFDataParser(f.getAbsolutePath());
                new Thread(parser).start();
                data = parser.get();
                restartTable();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            lines.setText(data.getVariants().size() + " (total: " + data.
                    getVariants().size() + ")");
            loadContigFilters();
            loadFilterFilters();
            loadInfoFilters();
        }
    }

    @FXML
    private void save() {
        File f = OS.saveVCF();
        data.exportVCF(f.getAbsolutePath());
    }

    private void loadContigFilters() {
        contigsFilter.getChildren().clear();
//        for (String contig : data.getContigs()) {
//            ToggleButton button = new ToggleButton(contig);
//            button.setMaxWidth(Integer.MAX_VALUE);
//            button.setOnAction((ActionEvent t) -> {
//                filter();
//            });
//            button.setSelected(true);
//            contigsFilter.getChildren().add(button);
//        }
        data.getContigs().stream().map((contig) -> new ToggleButton(contig)).
                map((button) -> {
            button.setMaxWidth(Integer.MAX_VALUE);
            return button;
        }).map((button) -> {
            button.setOnAction((ActionEvent t) -> {
                filter();
            });
            return button;
        }).map((button) -> {
            button.setSelected(true);
            return button;
        }).forEach((button) -> {
            contigsFilter.getChildren().add(button);
        });
    }

//        for (Map<String, String> filter : data.getFilters()) {
//            ToggleButton button = new ToggleButton(filter.get("Id"));
//            button.setMaxWidth(Integer.MAX_VALUE);
//            button.setTooltip(new Tooltip(filter.get("Description")));
//            button.setSelected(true);
//            button.setOnAction((ActionEvent t) -> {
//                filter();
//            });
//            filtersFilter.getChildren().add(button);
//        }
    private void loadFilterFilters() {
        filtersFilter.getChildren().clear();
        data.getFilters().stream().map((filter) -> {
            ToggleButton button = new ToggleButton(filter.get("ID"));
            button.setMaxWidth(Integer.MAX_VALUE);
            button.setTooltip(new Tooltip(filter.get("Description")));
            return button;
        }).map((button) -> {
            button.setSelected(true);
            return button;
        }).map((button) -> {
            button.setOnAction((ActionEvent t) -> {
                filter();
            });
            return button;
        }).forEach((button) -> {
            filtersFilter.getChildren().add(button);
        });
    }

    private void loadInfoFilters() {
        filters.clear();
        infoFilters.getChildren().clear();
        for (int index = 0; index < data.getInfos().size(); index++) {
            Map<String, String> info = data.getInfos().get(index);
            Label title = new Label(info.get("ID"));
            title.setTooltip(new Tooltip(info.get("Description")));
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
                    min.setPromptText("Min");
                    min.setPrefWidth(80);
                    min.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    HBox hbox = new HBox(min, max);
                    hbox.setSpacing(5);
                    infoFilters.getChildren().addAll(title, hbox);
                    filters.add(new Filter("numeric", index, min, max));
                    break;
                case "Character":
                case "String":
                    TextField value = new TextField();
                    value.setPromptText("Value");
                    value.setPrefWidth(165);
                    value.setOnAction((ActionEvent t) -> {
                        filter();
                    });
                    infoFilters.getChildren().addAll(title, value);
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
                    HBox box = new HBox(yes, no);
                    box.setSpacing(5);
                    box.setAlignment(Pos.CENTER);
                    infoFilters.getChildren().addAll(title, box);
                    filters.add(new Filter("flag", index, yes, no));
                    break;
            }
        }
    }

    @FXML
    private void selectAllContigs() {
        contigsFilter.getChildren().stream().forEach((node) -> {
            ((ToggleButton) node).setSelected(true);
        });
        filter();
    }

    @FXML
    private void selectNoneContigs() {
        contigsFilter.getChildren().stream().forEach((node) -> {
            ((ToggleButton) node).setSelected(false);
        });
        filter();
    }

    @FXML
    private void selectAllFilters() {
        filtersFilter.getChildren().stream().forEach((node) -> {
            ((ToggleButton) node).setSelected(true);
        });
        filter();
    }

    @FXML
    private void selectNoneFilters() {
        filtersFilter.getChildren().stream().forEach((node) -> {
            ((ToggleButton) node).setSelected(false);
        });
        filter();
    }

    @FXML
    private void filter() {
        // CHROM
        data.filterChrom(false, selectedContigs());
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
        data.filterFilter(true, selectedFilters());
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
                            System.err.println("Bad number: " + minString
                                    + " or " + maxString);
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

    private String[] selectedContigs() {
        ArrayList<String> cs = new ArrayList<>();
        contigsFilter.getChildren().stream().map((node)
                -> (ToggleButton) node).filter((button)
                        -> (button.isSelected())).forEach((button) -> {
            cs.add(button.getText());
        });
        String[] ret = new String[cs.size()];
        for (int i = 0; i < cs.size(); i++) {
            ret[i] = cs.get(i);
        }
        return ret;
    }

    private String[] selectedFilters() {
        ArrayList<String> fs = new ArrayList<>();
        filtersFilter.getChildren().stream().map((node)
                -> (ToggleButton) node).filter((button)
                        -> (button.isSelected())).forEach((button) -> {
            fs.add(button.getText());
        });
        String[] ret = new String[fs.size()];
        for (int i = 0; i < fs.size(); i++) {
            ret[i] = fs.get(i);
        }
        return ret;
    }

    private void restartTable() {
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
            TableColumn<Variant, String> iColumn = new TableColumn<>(data.
                    getInfos().get(i).get("ID"));
            iColumn.setCellValueFactory((
                    TableColumn.CellDataFeatures<Variant, String> p)
                    -> new SimpleStringProperty(p.getValue().getInfos().get(
                                    index)));
            infoColumn.getColumns().add(iColumn);
        }
        table.getColumns().setAll(chrColumn, posColumn, idColumn,
                refColumn, altColumn, qualColumn, filterColumn, infoColumn);
        tableContainer.setContent(table);
        variantsTable = table;
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
