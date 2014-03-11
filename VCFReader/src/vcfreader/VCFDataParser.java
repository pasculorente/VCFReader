package vcfreader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFDataParser extends Task<VCFData> {

    private final String vcfFile;
    private VCFData data;

    public VCFDataParser(String vcfFile) {
        this.vcfFile = vcfFile;
    }

    @Override
    protected VCFData call() {
        data = new VCFData();
        try (BufferedReader br = new BufferedReader(new FileReader(vcfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    data.getHeaders().add(line);
                    if (line.startsWith("##INFO")) {
                        data.getInfos().add(toInfo(line));
                    } else if (line.startsWith("##FORMAT")) {
                        data.getFormats().add(toFormat(line));
                    } else if (line.startsWith("##FILTER")) {
                        data.getFilters().add(toFilter(line));
                    } else if (line.startsWith("##contig")) {
                        data.getContigs().add(toContig(line));
                    } else if (line.startsWith("#CHROM")) {
                        String[] fields = line.split("\t");
                        if (fields.length > 8) {
                            for (int i = 9; i < fields.length; i++) {
                                data.getSamples().add(fields[i]);
                            }
                        }
                    }
                } else {
                    data.getVariants().add(toVariant(line));
                }
            }
            Map<String, String> pass = new TreeMap<>();
            pass.put("ID", "PASS");
            pass.put("Description", "All filters passed");
            data.getFilters().add(pass);
            if (data.getContigs().isEmpty()) {
                inferContigs(data);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VCFDataParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VCFDataParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return data;
    }

    Variant toVariant(String line) {
        String[] fields = line.split("\t");
        Variant v = new Variant(fields[0], Integer.valueOf(fields[1]), fields[2], fields[3],
                fields[4], Double.valueOf(fields[5]), fields[6], data.getInfos().size(), data.
                getSamples().size(), data.getFormats().size());
        String[] inf = fields[7].split(";");
        for (String info : inf) {
            String[] keyvalue = info.split("=");
            int index = data.indexOfInfo(keyvalue[0]);
            if (keyvalue.length == 1) {
                v.getInfos().set(index, "Yes");
            } else {
                v.getInfos().set(index, keyvalue[1]);
            }
        }
        if (fields.length > 8) {
            String[] fts = fields[8].split(":");
            for (int i = 0; i < data.getSamples().size(); i++) {
                String[] values = fields[i + 9].split(":");
                for (int j = 0; j < values.length; j++) {
                    int index = data.indexOfFormat(fts[j]);
                    v.getSamples().get(i).set(index, values[j]);
                }
            }

        }
        return v;
    }

    Map<String, String> toFormat(String line) {
        return toInfo(line);
    }

    Map<String, String> toInfo(String line) {
        String line2 = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
        Map<String, String> info = new TreeMap<>();
        // between = and ,
        int equals;
        while ((equals = line2.indexOf("=")) != -1) {
            if (line2.charAt(equals + 1) == '"') {
                int quote = line2.substring(equals + 2).indexOf('"') + equals
                        + 2;
                info.put(line2.substring(0, equals), line2.substring(equals + 2,
                        quote));
                line2 = line2.substring(quote);
            } else {
                int comma = line2.indexOf(",");
                if (comma == -1) {
                    comma = line2.length() - 1;
                    info.put(line2.substring(0, equals), line2.substring(equals
                            + 1, comma));
                    break;
                }
                info.put(line2.substring(0, equals), line2.substring(equals + 1,
                        comma));
                line2 = line2.substring(comma + 1);
            }
        }

        return info;
    }

    Map<String, String> toFilter(String line) {
        return toInfo(line);
    }

    String toContig(String line) {
        String line2 = line.substring(9);
        int equals = line2.indexOf("=");
        int comma = line2.indexOf(",");
        return line2.substring(equals + 1, comma);
    }

    private void inferContigs(VCFData data) {
        data.getVariants().stream().filter((v) -> (!data.getContigs().contains(
                v.getChrom()))).forEach((v) -> {
            data.getContigs().add(v.getChrom());
        });
    }

}
