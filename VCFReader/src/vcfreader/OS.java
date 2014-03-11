package vcfreader;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Contains methods to control files in DNAnalytics and fields containing file filters. Open and
 * save files, set TextFileds with the name of the files and compress and uncompress files.
 *
 * @author Pascual Lorente Arencibia
 */
public class OS {

    private static File lastPath;

    public static final String VCF_EXTENSION = ".vcf";
    public static final String VCF_DESCRIPTION = "Variant Call Format";
    public static final String[] VCF_FILTERS = new String[]{"*.vcf"};

    public OS() {
        switch (System.getProperty("os.name")) {
            case "Windows 7":
                lastPath = new File(System.getenv("user.dir"));
                break;
            case "Linux":
            default:
                lastPath = new File(System.getenv("PWD"));
        }
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
            return file.getAbsolutePath().endsWith(".vcf") ? file : new File(
                    file.getAbsolutePath() + ".vcf");
        }
        return null;
    }
}
