package vcfreader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFData {

    static Header toHeader(String line) {
        String[] keyvalue = line.substring(2).split("=");
        return new Header(keyvalue[0], keyvalue[1]);
    }

    private final ArrayList<Info> infos;
    private final ArrayList<Format> formats;
    private final ArrayList<Variant> variants;
    private final ArrayList<Filter> filters;
    private final ArrayList<String> contigs;

    public VCFData() {
        this.infos = new ArrayList<>();
        this.variants = new ArrayList<>();
        this.formats = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.contigs = new ArrayList<>();
    }

    static Variant toVariant(String line) {
        String[] fields = line.split("\t");
        Variant v = new Variant(fields[0], Integer.valueOf(fields[1]), fields[2], fields[3],
                fields[4],
                Double.valueOf(fields[5]), fields[6]);
        String[] infos = fields[7].split(";");
        for (String info : infos) {
            String[] keyvalue = info.split("=");
            if (keyvalue.length == 1) {
                v.getInfos().put(keyvalue[0], "Yes");
            } else {
                v.getInfos().put(keyvalue[0], keyvalue[1]);
            }
        }
        return v;
    }

    static Format toFormat(String line) {
        String line2 = line.substring(10);
        // between = and ,
        int equals = line2.indexOf("=");
        int comma = line2.indexOf(",");
        String id = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        equals = line2.indexOf("=");
        comma = line2.indexOf(",");
        String number = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        equals = line2.indexOf("=");
        comma = line2.indexOf(",");
        String type = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        int squote = line2.indexOf("\"");
        String desc = line2.substring(squote + 1, line2.length() - 2);
        return new Format(id, number, type, desc);
    }

    static Info toInfo(String line) {
        String line2 = line.substring(8);
        // between = and ,
        int equals = line2.indexOf("=");
        int comma = line2.indexOf(",");
        String id = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        equals = line2.indexOf("=");
        comma = line2.indexOf(",");
        String number = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        equals = line2.indexOf("=");
        comma = line2.indexOf(",");
        String type = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        int squote = line2.indexOf("\"");
        String desc = line2.substring(squote + 1, line2.length() - 2);
        return new Info(id, number, type, desc);
    }

    static Filter toFilter(String line) {
        String line2 = line.substring(10);
        // between = and ,
        int equals = line2.indexOf("=");
        int comma = line2.indexOf(",");
        String id = line2.substring(equals + 1, comma);
        line2 = line2.substring(comma + 1);
        int squote = line2.indexOf("\"");
        String desc = line2.substring(squote + 1, line2.length() - 2);
        return new Filter(id, desc);
    }

    public ArrayList<Info> getInfos() {
        return infos;
    }

    public ArrayList<Variant> getVariants() {
        return variants;
    }

    public ArrayList<Format> getFormats() {
        return formats;
    }

    ArrayList<Filter> getFilters() {
        return filters;
    }

    static ArrayList<Variant> filter(ArrayList<Variant> variants, String type, String... filter) {
        ArrayList<Variant> filtered = new ArrayList<>();
        switch (type) {
            case "CHROM":
                for (Variant variant : variants) {
                    for (String chr : filter) {
                        if (variant.getChrom().equals(chr)) {
                            filtered.add(variant);
                            break;
                        }
                    }
                }
                break;
            case "ID":
                if (filter.length > 1) {
                    throw new IllegalArgumentException();
                }
                variants.stream().filter((variant) -> (variant.getId().contains(filter[0]))).forEach((variant) -> {
                    filtered.add(variant);
                });
                break;
            case "REF":
                if (filter.length > 1) {
                    throw new IllegalArgumentException();
                }
                variants.stream().filter((variant) -> (variant.getRef().contains(filter[0]))).forEach((variant) -> {
                    filtered.add(variant);
                });
                break;
            case "ALT":
                if (filter.length > 1) {
                    throw new IllegalArgumentException();
                }
                variants.stream().filter((variant) -> (variant.getAlt().contains(filter[0]))).forEach((variant) -> {
                    filtered.add(variant);
                });
                break;
            case "POS":
                int start = Integer.valueOf(filter[0]);
                int end = Integer.valueOf(filter[1]);
                variants.stream().filter((variant) -> (variant.getPos() >= start && variant.getPos() <= end)).forEach((variant) -> {
                    filtered.add(variant);
                });
                break;
            case "QUAL":
                double min = Double.valueOf(filter[0]);
                double max = Double.valueOf(filter[1]);
                variants.stream().filter((variant) -> (variant.getQual() >= min && variant.getQual() <= max)).forEach((variant) -> {
                    filtered.add(variant);
                });
                break;
            case "FILTER":
                for (Variant variant : variants) {
                    for (String f : filter) {
                        if (variant.getFilter().equals(f)) {
                            filtered.add(variant);
                            break;
                        }
                    }
                }
        }
        return filtered;
    }

    ArrayList<String> getContigs() {
        return contigs;
    }

    static String toContig(String line) {
        String line2 = line.substring(9);
        int equals = line2.indexOf("=");
        int comma = line2.indexOf(",");
        return line2.substring(equals + 1, comma);
    }

    void loadVariants(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            variants.clear();
            infos.clear();
            formats.clear();
            filters.clear();
            contigs.clear();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    if (line.startsWith("##INFO")) {
                        infos.add(toInfo(line));
                    } else if (line.startsWith("##FORMAT")) {
                        formats.add(toFormat(line));
                    } else if (line.startsWith("##FILTER")) {
                        filters.add(toFilter(line));
                    } else if (line.startsWith("##contig")) {
                        contigs.add(toContig(line));
                    }
                } else {
                    variants.add(toVariant(line));
                }
            }
            filters.add(0, new Filter("PASS", "All filters passed"));
            if (contigs.isEmpty()) {
                inferContigs();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VCFData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VCFData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "Infos\n"
                + "----------------------\n"
                + "id\tnumber\ttype\tdesc\n";
        ret = infos.stream().map((info) -> info.getId() + "\t" + info.getNumber() + "\t" + info.getType() + "\t"
                + info.getDescription() + "\n").reduce(ret, String::concat);
        ret += "\nFormats\n"
                + "----------------------\n"
                + "id\tnumber\ttype\tdesc\n";
        ret = formats.stream().map((format) -> format.getId() + "\t" + format.getNumber() + "\t" + format.getType() + "\t"
                + format.getDescription() + "\n").reduce(ret, String::concat);
        ret += "\nFilters\n"
                + "----------------------\n"
                + "id\tdesc\n";
        ret = filters.stream().map((filter) -> filter.getId() + "\t" + filter.getDescription() + "\n").reduce(ret, String::concat);
        return ret;
    }

    private void inferContigs() {
        variants.stream().filter((v) -> (!contigs.contains(v.getChrom()))).forEach((v) -> {
            contigs.add(v.getChrom());
        });
    }

}
