package dean.zopa;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

	@Test
	public void integrationTest() throws IOException {
		Quote quote = Main.run("marketTest.csv", "1000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 8.0%\nMonthly repayment: £31.34\nTotal repayment: £1,128.12";
		assertThat(quote.toString(), is(expectedQuote));
	}
}
