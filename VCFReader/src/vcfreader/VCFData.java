package vcfreader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFData {

    private final List<Map<String, String>> infos;
    private final List<Map<String, String>> formats;
    private final List<Map<String, String>> filters;
    private final List<Variant> variants;
    private final List<String> contigs;
    private final List<String> samples;
    private final List<String> headers;
    private List<Variant> cached;

    public VCFData() {
        this.infos = new ArrayList<>();
        this.variants = new ArrayList<>();
        this.formats = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.contigs = new ArrayList<>();
        this.samples = new ArrayList<>();
        this.headers = new ArrayList<>();
        cached = variants;
    }

    public List<Map<String, String>> getInfos() {
        return infos;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public List<Map<String, String>> getFormats() {
        return formats;
    }

    public List<Map<String, String>> getFilters() {
        return filters;
    }

    public List<String> getContigs() {
        return contigs;
    }

    public List<String> getSamples() {
        return samples;
    }

    public List<Variant> getCached() {
        return cached;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public int indexOfInfo(String id) {
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).get("ID").equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Variant> filterChrom(boolean cache, String... chroms) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            for (String chr : chroms) {
                if (variant.getChrom().equals(chr)) {
                    cached.add(variant);
                    break;
                }
            }
        }
        return cached;
    }

    public List<Variant> filterPos(boolean cache, int min, int max) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (min <= variant.getPos() && variant.getPos() <= max) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public List<Variant> filterId(boolean cache, String value) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (variant.getId().contains(value)) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public List<Variant> filterRef(boolean cache, String value) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (variant.getRef().contains(value)) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public List<Variant> filterAlt(boolean cache, String value) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (variant.getAlt().contains(value)) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public List<Variant> filterQual(boolean cache, double min, double max) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (min <= variant.getQual() && variant.getQual() <= max) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public List<Variant> filterFilter(boolean cache, String... filters) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            for (String chr : filters) {
                if (variant.getFilter().equals(chr)) {
                    cached.add(variant);
                    break;
                }
            }
        }
        return cached;
    }

    public List<Variant> filterInfoNumeric(boolean cache, int index, double min,
            double max) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            String[] values = variant.getInfos().get(index).split(",");
            for (String v : values) {
                double d = Double.valueOf(v);
                if (min <= d && d <= max) {
                    cached.add(variant);
                }
            }
        }
        return cached;
    }

    public List<Variant> filterInfoText(boolean cache, int index, String value,
            boolean match) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (!match && variant.getInfos().get(index).contains(value)) {
                cached.add(variant);
            } else if (match && variant.getInfos().get(index).equals(value)) {
                cached.add(variant);
            }

        }
        return cached;
    }

    public List<Variant> filterInfoFlag(boolean cache, int index, boolean yes) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (yes == variant.getInfos().get(index).equals("Yes")) {
                cached.add(variant);
            }
        }
        return cached;
    }

    public void exportVCF(String file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String head : headers) {
                bw.write(head);
                bw.newLine();
            }

            for (Variant variant : cached) {
                bw.write(variant + "");
                bw.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(VCFData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
