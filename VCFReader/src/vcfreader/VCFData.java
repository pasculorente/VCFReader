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

    /**
     * Gets the list with all the INFO headers, given as maps of key=value pairs.
     *
     * @return the list of INFO headers.
     */
    public List<Map<String, String>> getInfos() {
        return infos;
    }

    /**
     * Gets the list of variants.
     *
     * @return the list of variants.
     */
    public List<Variant> getVariants() {
        return variants;
    }

    /**
     * Gets the list with all the FORMAT headers, given as maps of key=value pairs.
     *
     * @return the list of FORMAT headers.
     */
    public List<Map<String, String>> getFormats() {
        return formats;
    }

    /**
     * Gets the list with all the FILTER headers, given as maps of key=value pairs.
     *
     * @return the list of FILTER headers.
     */
    public List<Map<String, String>> getFilters() {
        return filters;
    }

    /**
     * Gets the list with all the CONTIGS. These list is extracted from headers. If there are no
     * ##contig headers, they'll be guessed out from variants list.
     *
     * @return the list of contigs.
     */
    public List<String> getContigs() {
        return contigs;
    }

    /**
     * Gets the list with all the samples.
     *
     * @return the list with the samples.
     */
    public List<String> getSamples() {
        return samples;
    }

    /**
     * Gets the list with all the variants filtered in. Id est, all the variants that successfully
     * passed all the filters.
     *
     * @return the list of filtered variants.
     */
    public List<Variant> getCached() {
        return cached;
    }

    /**
     * Gets the list of headers as they were read if file. A header line is a line that starts with
     * #.
     *
     * @return the list with all the headers.
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * Calculates the index of a given id in the list of IFNO headers.
     *
     * @param id the id of the info.
     * @return the index if the id in the info list, or -1 if not found.
     */
    public int indexOfInfo(String id) {
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).get("ID").equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Calculates the index of a given id in the list of FORMAT headers.
     *
     * @param id the id of the format.
     * @return the index if the id in the format list, or -1 if not found.
     */
    public int indexOfFormat(String id) {
        for (int i = 0; i < formats.size(); i++) {
            if (formats.get(i).get("ID").equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Filters the variants per chromosome. All variants in the chromosomes passed by argument will
     * be in the return variants.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param chroms a list of chromosomes whose variants will be included in the filtered list.
     * @return a list with all the variants which have any of the given chromosomes.
     */
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

    /**
     * Filters the variants per position. All the variants between min and max will be included in
     * the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param min the min position, included.
     * @param max the max position, included.
     * @return a list of variants between min a max.
     */
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

    /**
     * Filters variants per id. All variants containing the value will be added to the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param value a string with the text to filter.
     * @return a list of variants whose id contains value.
     */
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

    /**
     * Filters variants per ref. All variants containing the value will be added to the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param value a string with the text to filter.
     * @return a list of variants whose ref contains value.
     */
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

    /**
     * Filters variants per alt. All variants containing the value will be added to the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param value a string with the text to filter.
     * @return a list of variants whose alt contains value.
     */
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

    /**
     * Filters the variants per quality. All the variants with quality between min and max will be
     * included in the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param min the min quality, included.
     * @param max the max quality, included.
     * @return a list of variants with quality between min a max.
     */
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

    /**
     * Filters the variants per filter. All variants with any of the filters passed by argument will
     * be in the return variants.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param filters a list of filters whose variants will be included in the filtered list.
     * @return a list with all the variants which passed any of the given filters.
     */
    public List<Variant> filterFilter(boolean cache, String value) {
        List<Variant> origin = cache ? cached : variants;
        cached = new ArrayList<>();
        for (Variant variant : origin) {
            if (variant.getFilter().contains(value)) {
                cached.add(variant);
            }
        }
        return cached;
    }

    /**
     * Filters the variants per filter. All variants with any of the filters passed by argument will
     * be in the return variants.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param filters a list of filters whose variants will be included in the filtered list.
     * @return a list with all the variants which passed any of the given filters.
     */
    public List<Variant> filterByFilter(boolean cache, String... filters) {
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

    /**
     * Filters variants using a numeric (Integer or Float) Info value. If a variant has no value, it
     * won't appear in the return list.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param index the position of the Info value.
     * @param min the min value, included.
     * @param max the max value, included.
     * @return a list of variants with an Info value in the index position between min and max.
     */
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

    /**
     * Filters variants using a text (Character or String) Info value. If a variant has no value, it
     * won't appear in the return list. If match is true, only exact matching will be returned, but
     * if match is false, all variants containing value will be in.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param index the position of the Info value.
     * @param value the value to filter.
     * @param match true for exact matching, false for containing matching.
     * @return a list of variants with an Info value matching param value.
     */
    public List<Variant> filterInfoText(boolean cache, int index, String value, boolean match) {
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

    /**
     * Filters variants using a Flag Info value. If yes is true, variants with the flag will be
     * returned. If false, variants without the flag will be returned.
     *
     * @param cache true if you want to use the cached variants as origin variants; false if you
     * want to use all the variants.
     * @param index the position of the Info value.
     * @param yes true if variants must have the flag, false if variants must not have the flag.
     * @return a list of variants with or without a Flag.
     */
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

    /**
     * Stores all the filtered variants in the file passed by argument. The headers are copied from
     * the input file, but the variants are reinterpreted, so they can change slightly. But the
     * information is the same,so don't be worried.
     *
     * @param file the file where to write the variants.
     */
    public void exportVCF(String file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String head : headers) {
                bw.write(head);
                bw.newLine();
            }
            for (Variant variant : cached) {
                // Variant
                bw.write(variant.getChrom() + "\t" + variant.getPos() + "\t" + variant.getId()
                        + "\t" + variant.getRef() + "\t" + variant.getAlt()
                        + "\t" + variant.getQual() + "\t" + variant.getFilter() + "\t");
                // Info
                for (int i = 0; i < infos.size(); i++) {
                    if (!variant.getInfos().get(i).isEmpty()) {
                        bw.write(infos.get(i).get("ID"));
                        if (!infos.get(i).get("Type").equals("Flag")) {
                            bw.write("=" + variant.getInfos().get(i));
                        }
                        if (i < infos.size() - 1) {
                            bw.write(";");
                        }
                    }
                }
                // Formats
                if (!samples.isEmpty()) {
                    // Write format column
                    bw.write("\t");
                    for (int i = 0; i < formats.size(); i++) {
                        bw.write(formats.get(i).get("ID"));
                        if (i < formats.size() - 1) {
                            bw.write(":");
                        }
                    }
                    bw.write("\t");
                    // Write samples columns
                    for (int i = 0; i < samples.size(); i++) {
                        for (int j = 0; j < formats.size(); j++) {
                            bw.write(variant.getSamples().get(i).get(j));
                            if (j < formats.size() - 1) {
                                bw.write(":");
                            }
                        }
                        if (i < samples.size() - 1) {
                            bw.write("\t");
                        }
                    }
                }
                bw.newLine();

            }
        } catch (IOException ex) {
            Logger.getLogger(VCFData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
