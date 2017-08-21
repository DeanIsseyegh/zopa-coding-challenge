package dean.zopa;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

	@Test
	public void Given_Lenders_AndValidRequestedAmount_Then_ReturnCorrectQuote() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"Fred,0.071,520\n" +
				"Mary,0.104,170\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,60\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "1000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.8%\nMonthly repayment: £31.26\nTotal repayment: £1,125.45";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test
	public void Given_DifferentSetOfLenders_AndValidRequestedAmount_Then_ReturnCorrectQuote() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Rob,0.075,1440\n" +
				"Tod,0.079,423\n" +
				"Lou,0.041,1320\n" +
				"Will,0.034,1120\n" +
				"Marian,0.051,150\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "4300");
		String expectedQuote = "Requested amount: £4,300.00\nRate: 6.3%\nMonthly repayment: £131.48\nTotal repayment: £4,733.17";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_Lenders_RequestedAmountUnder1k_Then_ThrowException() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "900");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.06\nTotal repayment: £1,118.18";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_Lenders_RequestedAmountOver15k_Then_ThrowException() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,15000\n" +
				"Jane,0.069,480\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "15100");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.06\nTotal repayment: £1,118.18";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_Lenders_RequestedAmountNotIncrementOf100_Then_ThrowException() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "1001");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.06\nTotal repayment: £1,118.18";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_Lenders_RequestedAmountMoreThanLendersHave_Then_ThrowException() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"John,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "10000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.06\nTotal repayment: £1,118.18";
		assertThat(quote.toString(), is(expectedQuote));
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_Lenders_WithNonUniqueNames_Then_ThrowException() throws IOException {
		String content = "Lender,Rate,Available\n" +
				"Bob,0.075,640\n" +
				"Jane,0.069,480\n" +
				"Bob,0.081,320\n" +
				"Dave,0.074,140\n" +
				"Angela,0.071,300\n";
		writeContentToTestFile(content);
		Quote quote = Main.run("testFile.input", "1000");
		String expectedQuote = "Requested amount: £1,000.00\nRate: 7.4%\nMonthly repayment: £31.06\nTotal repayment: £1,118.18";
		assertThat(quote.toString(), is(expectedQuote));
	}

	private void writeContentToTestFile(String content) throws IOException {
		Path path = Paths.get("testFile.input");
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(content);
		}
	}

}
