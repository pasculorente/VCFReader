package vcfreader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class Variant {

    private final String chrom, id, ref, alt, filter;
    private final int pos;
    private final double qual;
    private final List<String> infos;
    private final List<List<String>> samples;

//    private final Map<String, String> infos;
    Variant(String chrom, int pos, String id, String ref, String alt, double qual, String filter,
            int infos, int samples, int formats) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.infos = new ArrayList<>();
        for (int i = 0; i < infos; i++) {
            this.infos.add("");
        }
        this.samples = new ArrayList<>();
        for (int i = 0; i < samples; i++) {
            List<String> fts = new ArrayList<>();
            for (int j = 0; j < formats; j++) {
                fts.add("");
            }
            this.samples.add(fts);
        }

    }

    public String getChrom() {
        return chrom;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getFilter() {
        return filter;
    }

    public int getPos() {
        return pos;
    }

    public double getQual() {
        return qual;
    }

    public List<String> getInfos() {
        return infos;
    }

    public List<List<String>> getSamples() {
        return samples;
    }

}
