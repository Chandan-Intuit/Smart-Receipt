package PostTest;
import com.intuit.karate.cucumber.CucumberRunner;
import com.intuit.karate.cucumber.KarateStats;
import com.intuit.karate.junit4.Karate;

import cucumber.api.CucumberOptions;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Karate.class)
@CucumberOptions(tags = {"~@ignore"})
public class PostTest {
    private static final Logger LOGGER = LoggerFactory.getLogger( PostTest.class);
    @Test
    public void testParallel() throws IOException {

        String karateOutputPath = "target/surefire-reports";
        KarateStats stats = null;

        stats = CucumberRunner.parallel(getClass(), 7, karateOutputPath);

        generateReport(karateOutputPath);
        if(stats!=null) {
            assertTrue("there are scenario failures", stats.getFailCount() == 0);
        } else {
            fail("There are scenario failures due to unexpected errors");
        }
    }
    private static void generateReport(String karateOutputPath) throws IOException {

        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList(jsonFiles.size());
        for (File file : jsonFiles) {
            jsonPaths.add(file.getAbsolutePath());
        }
        Configuration config = new Configuration(new File("target"), "ghcdemo-tests-karate");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}
