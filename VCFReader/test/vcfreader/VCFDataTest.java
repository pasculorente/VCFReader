package vcfreader;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFDataTest {

//    VCFData dataset;
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        dataset = new VCFData();
//        dataset.loadVariants("sample.vcf");
//    }
//
//    @Test
//    public void testToVariant() {
//        System.out.println("Parsing some variants");
//        Variant v = VCFData.toVariant(
//                "1\t14907\t.\tA\tG\t661.77\tPASS"
//                + "\tAC=1;AF=0.500;AN=2;DP=176;MLEAC=1;MLEAF=0.500;set=Intersection"
//                + "\tGT:AD:GQ:PL\t0/1:50,17:99:690,0,2735");
//        Variant v2 = VCFData.toVariant(
//                "2\t170783092\t.\tCAAAAACA\tC\t682.77\tPASS"
//                + "\tAC=1;AF=0.500;AN=2;DP=46;FS=0.000;MLEAC=1;MLEAF=0.500;MQ0=0;set=Intersection"
//                + "\tGT:AD:GQ:PL\t0/1:12,9:99:711,0,770");
//        // Test chrom
//        assertEquals("1", v.getChrom());
//        assertEquals("2", v2.getChrom());
//        // Test position
//        assertEquals(14907, v.getPos());
//        assertEquals(170783092, v2.getPos());
//        // Test id
//        assertEquals(".", v.getId());
//        assertEquals(".", v2.getId());
//        // Test ref
//        assertEquals("A", v.getRef());
//        assertEquals("CAAAAACA", v2.getRef());
//        // Test alt
//        assertEquals("G", v.getAlt());
//        assertEquals("C", v2.getAlt());
//        // Test qual
//        assertEquals(661.77, v.getQual(), 0.0);
//        assertEquals(682.77, v2.getQual(), 0.0);
//        // Test filter
//        assertEquals("PASS", v.getFilter());
//        assertEquals("PASS", v2.getFilter());
//        // Test info
//        assertEquals("1", v.getInfos().get("AC"));
//        assertEquals("176", v.getInfos().get("DP"));
//        assertEquals("0.500", v2.getInfos().get("MLEAF"));
//        // Test format: not yet
//    }
//
//    @Test
//    public void testLoadVariants() {
//        System.out.println("Loading a file");
//        dataset.loadVariants("sample.vcf");
//        // Test variants OK
//        assertEquals(7, dataset.getVariants().size());
//        assertEquals(170782037, dataset.getVariants().get(2).getPos());
//        // Test infos OK
//        assertEquals(19, dataset.getInfos().size());
//        assertEquals("PASS", dataset.getVariants().get(6).getFilter());
//        assertEquals("A", dataset.getInfos().get(1).getNumber());
//        assertEquals("dbSNP Membership", dataset.getInfos().get(5).getDescription());
//        assertEquals("Variant Confidence/Quality by Depth", dataset.getInfos().get(16).
//                getDescription());
//        // Test formats OK
//        assertEquals(5, dataset.getFormats().size());
//        assertEquals("Integer", dataset.getFormats().get(2).getType());
//        // Test filters OK
//        assertEquals(2, dataset.getFilters().size());
//        assertEquals("LowQual", dataset.getFilters().get(1).getId());
//        assertEquals(24, dataset.getContigs().size());
//    }
//
//    @Test
//    public void testLoadEmptyHeaderFile() {
//        System.out.println("Loading a file without headers");
//        VCFData data = new VCFData();
//        data.loadVariants("sample2.vcf");
//        assertEquals(3, data.getContigs().size());
//    }
//
//    @Test
//    public void testFilterChromosomes() {
//        System.out.println("Filtering some chromosomes");
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "CHROM", "1");
//        assertEquals(2, filtered.size());
//        assertEquals("1", filtered.get(0).getChrom());
//        assertEquals("1", filtered.get(1).getChrom());
//        assertEquals(645.77, filtered.get(1).getQual(), 0.0);
//        filtered = VCFData.filter(dataset.getVariants(), "CHROM", "2", "9");
//        assertEquals(5, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "CHROM", "4", "7");
//        assertEquals(0, filtered.size());
//    }
//
//    @Test
//    public void testFilterId() {
//        System.out.println("Filtering some ids");
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "ID", ".");
//        assertEquals(6, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "ID", "rs");
//        assertEquals(1, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "ID", "rs17");
//        assertEquals(1, filtered.size());
//
//    }
//
//    @Test
//    public void testFilterRef() {
//        System.out.println("Filtering some Refs");
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "REF", "A");
//        assertEquals(4, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "REF", "CAAAAACA");
//        assertEquals(1, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "REF", ".");
//        assertEquals(0, filtered.size());
//    }
//
//    @Test
//    public void testFilterAlt() {
//        System.out.println("Filtering some Alts");
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "ALT", "A");
//        assertEquals(0, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "ALT", "CAAAAACA");
//        assertEquals(0, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "ALT", "T");
//        assertEquals(3, filtered.size());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testFilterAltBadArguments() {
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "ALT", "A", "C");
//    }
//
//    @Test
//    public void testFilterPos() {
//        System.out.println("Filtering some positions");
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "POS", "10000", "20000");
//        assertEquals(2, filtered.size());
//        filtered = VCFData.filter(dataset.getVariants(), "POS", "0", "10000");
//        assertEquals(0, filtered.size());
//    }
//
//    @Test
//    public void testFilterCombined() {
//        ArrayList<Variant> filtered = VCFData.filter(dataset.getVariants(), "POS", "10000", "20000");
//        filtered = VCFData.filter(filtered, "REF", "A");
//        assertEquals(2, filtered.size());
//    }
//
//    /**
//     * Test of toFormat method, of class VCFData.
//     */
//    @Test
//    public void testToFormat() {
//        System.out.println("Parsing a format line");
//        Format f = VCFData.toFormat("##FORMAT=<ID=PL,Number=G,Type=Integer,"
//                + "Description=\"Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification\">");
//        assertEquals("PL", f.getId());
//        assertEquals("G", f.getNumber());
//        assertEquals("Integer", f.getType());
//        assertEquals(
//                "Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification",
//                f.getDescription());
//    }
//
//    @Test
//    public void testToHeader() {
//        System.out.println("Parsing a simple header line");
//        String line = "##fileformat=VCFv4.2";
//        String line2 = "##reference=file:///seq/references/1000GenomesPilot-NCBI36.fasta";
//        Header h = VCFData.toHeader(line);
//        assertEquals("fileformat", h.getKey());
//        assertEquals("VCFv4.2", h.getValue());
//        Header h1 = VCFData.toHeader(line2);
//        assertEquals("reference", h1.getKey());
//        assertEquals("file:///seq/references/1000GenomesPilot-NCBI36.fasta", h1.getValue());
//    }
//
//    /**
//     * Test of toInfo method, of class VCFData.
//     */
//    @Test
//    public void testToInfo() {
//        System.out.println("Parsing a info line");
//        Info i = VCFData.toInfo("##INFO=<ID=ClippingRankSum,Number=1,Type=Float,"
//                + "Description=\"Z-score From Wilcoxon rank sum test of Alt vs. Ref number of hard clipped bases\">");
//        assertEquals("ClippingRankSum", i.get("ID"));
//        assertEquals("1", i.get("Number"));
//        assertEquals("Float", i.get("Type"));
//        assertEquals(
//                "Z-score From Wilcoxon rank sum test of Alt vs. Ref number of hard clipped bases",
//                i.get("Description"));
//    }
//
//    /**
//     * Test of toInfo method, of class VCFData.
//     */
//    @Test
//    public void testToFilter() {
//        System.out.println("Parsing a filter line");
//        Filter f = VCFData.toFilter("##FILTER=<ID=LowQual,Description=\"Low quality\">");
//        assertEquals("LowQual", f.getId());
//        assertEquals("Low quality", f.getDescription());
//    }
//
//    @Test
//    public void testToContig() {
//        System.out.println("Parsing a contig line");
//        String conting = VCFData.toContig("##contig=<ID=14,length=107349540,assembly=b37>");
//        assertEquals("14", conting);
//    }
}
