package vcfreader;

import java.util.TreeMap;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class Variant {

    private final String chrom, id, ref, alt, filter;
    private final int pos;
    private final double qual;
    private final TreeMap<String, String> infos;

    Variant(String chrom, int pos, String id, String ref, String alt, double qual, String filter) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.infos = new TreeMap<>();
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

    public TreeMap<String, String> getInfos() {
        return infos;
    }
}
