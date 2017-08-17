package dean.zopa;

import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import dean.zopa.logic.LoanAlgorithm;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

	@Test
	public void integrationTest() {
		Lender lender1 = new Lender("Bob", new BigDecimal("0.075"), Money.of(640, Config.CURRENCY));
		Lender lender2 = new Lender("Jane", new BigDecimal("0.069"), Money.of(480, Config.CURRENCY));
		Lender lender3 = new Lender("Fred", new BigDecimal("0.071"), Money.of(520, Config.CURRENCY));
		Lender lender4 = new Lender("Mary", new BigDecimal("0.104"), Money.of(170, Config.CURRENCY));
		Lender lender5 = new Lender("John", new BigDecimal("0.081"), Money.of(320, Config.CURRENCY));
		Lender lender6 = new Lender("Dave", new BigDecimal("0.074"), Money.of(140, Config.CURRENCY));
		Lender lender7 = new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY));

		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2, lender3, lender4, lender5, lender6, lender7));
		LoanCalculator loanCalculator = new LoanCalculator(new LoanAlgorithm(lenderPool));
		Quote quote = new Quote(Money.of(1000, Config.CURRENCY), loanCalculator);

		String expectedQuote = "Requested amount: £1,000.00\nRate: 8.0%\nMonthly repayment: £31.34\nTotal repayment: £1,128.12";
		assertThat(quote.toString(), is(expectedQuote));
	}
}
