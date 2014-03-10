package vcfreader;

import java.io.File;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
    private TableView<Variant> variantsTable;
    @FXML
    private TableColumn<Variant, String> chrColumn;
    @FXML
    private TableColumn<Variant, Integer> posColumn;
    @FXML
    private TableColumn<Variant, String> idColumn;
    @FXML
    private TableColumn<Variant, String> refColumn;
    @FXML
    private TableColumn<Variant, String> altColumn;
    @FXML
    private TableColumn<Variant, Double> qualColumn;
    @FXML
    private TableColumn<Variant, String> filterColumn;
    @FXML
    private TableColumn infoColumn;
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
    private ArrayList<Variant> filtered;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        data = new VCFData();
        filtered = new ArrayList<>();
        lines.setText("");
        chrColumn.setCellValueFactory(new PropertyValueFactory<>("chrom"));
        posColumn.setCellValueFactory(new PropertyValueFactory<>("pos"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        refColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        qualColumn.setCellValueFactory(new PropertyValueFactory<>("qual"));
        filterColumn.setCellValueFactory(new PropertyValueFactory<>("filter"));
        variantsTable.getColumns().setAll(chrColumn, posColumn, idColumn, refColumn,
                altColumn, qualColumn, filterColumn, infoColumn);
    }

    @FXML
    private void openFile() {
        String[] filts = new String[]{"*.vcf"};
        File f = OS.openFile("Variant Call Format", "Variant Call Format", filts);
        if (f != null) {
            data.loadVariants(f.getAbsolutePath());
            lines.setText(data.getVariants().size() + "");
            data.getInfos().stream().map((info) -> {
                TableColumn<Variant, String> tc = new TableColumn<>(info.getId());
                tc.setCellValueFactory((TableColumn.CellDataFeatures<Variant, String> p)
                        -> new SimpleStringProperty(p.getValue().getInfos().get(
                                        info.getId())));
                return tc;
            }).forEach((tc) -> {
                infoColumn.getColumns().add(tc);
            });
            loadContigFilters();
            loadFilterFilters();
            loadInfoFilters();
            variantsTable.setItems(FXCollections.observableArrayList(data.getVariants()));
        }
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
        data.getContigs().stream().map((contig) -> new ToggleButton(contig)).map((button) -> {
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

    private void loadFilterFilters() {
        filtersFilter.getChildren().clear();
//        for (Filter filter : data.getFilters()) {
//            ToggleButton button = new ToggleButton(filter.getId());
//            button.setMaxWidth(Integer.MAX_VALUE);
//            button.setTooltip(new Tooltip(filter.getDescription()));
//            button.setSelected(true);
//            button.setOnAction((ActionEvent t) -> {
//                filter();
//            });
//            filtersFilter.getChildren().add(button);
//        }
        data.getFilters().stream().map((filter) -> {
            ToggleButton button = new ToggleButton(filter.getId());
            button.setMaxWidth(Integer.MAX_VALUE);
            button.setTooltip(new Tooltip(filter.getDescription()));
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
        infoFilters.getChildren().clear();
        for (Info info : data.getInfos()) {
            Label title = new Label(info.getId());
            title.setTooltip(new Tooltip(info.getDescription()));
            switch (info.getType()) {
                case "Integer":
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
                    break;
                case "Float":
                    break;
                case "Flag":
                    break;
                case "Character":
                    break;
                case "String":
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
        // Chromosome filter
        String[] contigs = selectedContigs();
        filtered = VCFData.filter(data.getVariants(), "CHROM", contigs);
        // Position filter
        if (!posFrom.getText().isEmpty() && !posTo.getText().isEmpty()) {
            try {
                Integer.valueOf(posFrom.getText());
                Integer.valueOf(posTo.getText());
                filtered = VCFData.filter(filtered, "POS", posFrom.getText(), posTo.getText());
            } catch (NumberFormatException ex) {
                System.err.println("Bad number format");
            }
        }
        // ID Filter
        filtered = VCFData.filter(filtered, "ID", idFilter.getText());
        // Qual filter
        if (!qualMin.getText().isEmpty() && !qualMax.getText().isEmpty()) {
            try {
                Double.valueOf(qualMin.getText());
                Double.valueOf(qualMax.getText());
                filtered = VCFData.filter(filtered, "QUAL", qualMin.getText(), qualMax.getText());
            } catch (NumberFormatException ex) {
                System.err.println("Bad number format");
            }
        }
        // FILTER filter
        filtered = VCFData.filter(filtered, "FILTER", selectedFilters());
        variantsTable.getItems().setAll(FXCollections.observableArrayList(filtered));
        lines.setText(filtered.size() + "");

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
}
