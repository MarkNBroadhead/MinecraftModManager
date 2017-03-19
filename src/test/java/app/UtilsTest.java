package app;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class UtilsTest extends TestCase {

    public void testTruncateVersionFromModFileName() throws Exception {
        List<String> testInput = new ArrayList<>();
        List<String> expectedOutput = new ArrayList<>();
        testInput.add("ActuallyAdditions-1.10.2-r105.jar");
        expectedOutput.add("ActuallyAdditions");
        testInput.add("ArsMagica2_1.5.0-6.jar");
        expectedOutput.add("ArsMagica2");
        testInput.add("CustomMobSpawner 3.10.1.jar");
        expectedOutput.add("CustomMobSpawner");
        testInput.add("davincisvessels-1.10.2-0006-full.jar");
        expectedOutput.add("davincisvessels");
        testInput.add("DrZharks MoCreatures Mod-10.0.6.jar");
        expectedOutput.add("DrZharks MoCreatures Mod");
        testInput.add("MCA-1.10.2-5.2.3-universal.jar");
        expectedOutput.add("MCA");
        testInput.add("movingworld-1.10.2-0007-full.jar");
        expectedOutput.add("movingworld");
        testInput.add("RadixCore-1.10.2-2.1.3-universal.jar");
        expectedOutput.add("RadixCore");
        testInput.add("BiblioCraft[v2.2.1][MC1.10.2].jar");
        expectedOutput.add("BiblioCraft");
        testInput.add("Botania r1.9-341.jar");
        expectedOutput.add("Botania");
        testInput.add("CustomMainMenu-MC1.10.2-2.0.2.jar");
        expectedOutput.add("CustomMainMenu");
        testInput.add("ElevatorMod[V.1.3.0][MC.1.10.2].jar");
        expectedOutput.add("ElevatorMod");
        testInput.add("EnderIO-1.10.2-3.1.156.jar");
        expectedOutput.add("EnderIO");
        testInput.add("Guide-API-1.10.2-2.0.3-46.jar");
        expectedOutput.add("Guide-API");
        testInput.add("InGameInfoXML-1.10.2-2.8.1.89-universal.jar");
        expectedOutput.add("InGameInfoXML");
        testInput.add("jei_1.10.2-3.14.7.416.jar");
        expectedOutput.add("jei");
        testInput.add("p455w0rdslib-1.10.2-1.0.13.jar");
        expectedOutput.add("p455w0rdslib");
        testInput.add("Pam's HarvestCraft 1.9.4-1.10.2h.jar");
        expectedOutput.add("Pam's HarvestCraft");
        testInput.add("WR-CBE-1.10.2-2.0.0.18-universal.jar");
        expectedOutput.add("WR-CBE");
        testInput = Utils.truncateVersionFromModFileName(testInput);
        assertEquals(expectedOutput, testInput);
    }
}
