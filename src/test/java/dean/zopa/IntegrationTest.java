package dean.zopa;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

	@Test
	public void Given_Lenders_AndValidRequestedAmount_ReturnCorrectQuote() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"Fred,0.071,520\n" +
				"Mary,0.104,170\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,60\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.csv", "1000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.8%\nMonthly repayment: £31.26\nTotal repayment: £1,125.45";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test
	public void Given_DifferentSetOfLenders_AndValidRequestedAmount_ReturnCorrectQuote() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"Fred,0.071,520\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.csv", "1000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.04\nTotal repayment: £1,117.35";
		assertThat(quote.toString(), is(expectedQuote));
	}

	private void writeContentToTestFile(String content) throws IOException {
		Path path = Paths.get("testFile.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(content);
		}
	}

}
