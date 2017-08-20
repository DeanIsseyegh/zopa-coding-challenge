package dean.zopa;

import dean.zopa.csv.InputParser;
import dean.zopa.csv.InputValidator;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import dean.zopa.logic.LoanAlgorithm;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

	@Test
	public void integrationTest() throws IOException {
		Stream<String> stream = Files.lines(Paths.get("marketTest.csv"));
		List<String> fileLines = stream.collect(Collectors.toList());
		InputParser inputParser = new InputParser(fileLines, new InputValidator());
		List<Lender> lenders = inputParser.parseLenders();
		LenderPool lenderPool = new LenderPool(lenders);
		LoanCalculator loanCalculator = new LoanCalculator(new LoanAlgorithm(lenderPool));
		Quote quote = new Quote(Money.of(1000, Config.CURRENCY), loanCalculator);
		String expectedQuote = "Requested amount: £1,000.00\nRate: 8.0%\nMonthly repayment: £31.34\nTotal repayment: £1,128.12";
		assertThat(quote.toString(), is(expectedQuote));
	}
}
