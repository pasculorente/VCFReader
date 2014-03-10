package vcfreader;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author uichuimi03
 */
public class VCFDataParserTest {

    public VCFDataParserTest() {
    }

    @Test
    public void testParser() {
        try {
            VCFDataParser parser = new VCFDataParser("sample.vcf");
            new Thread(parser).start();
            VCFData data = parser.get();
            assertEquals("1", "1");
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(VCFDataParserTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

    }
}
