package helloworld;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.oaky.cuke4duke.FeatureConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(Cuke4DukeJUnit4Runner.class)
@FeatureConfiguration("features/HelloWorld.feature")
@ContextConfiguration("HelloWorldFeature-context.xml")
@Transactional
public class HelloWorldFeature {

    @Autowired
    DataSource ds;

    @BeforeTransaction
    public void setupDb() {
        System.out.println("BeforeTransaction");
    }

    @AfterTransaction
    public void tearDownDb() {
        System.out.println("AfterTransaction");
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(ds);
        Assert.assertEquals(0, jdbc.queryForInt("SELECT count(*) FROM Entries"));
    }

    @Before
    public void setupScenario() {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(ds);
        jdbc.update("INSERT INTO Entries(entryid,entryname) VALUES(1,'1st')");
    }

    @After
    public void teardownScenario() {
    }    
}
