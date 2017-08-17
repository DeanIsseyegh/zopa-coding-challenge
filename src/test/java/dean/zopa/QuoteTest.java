package dean.zopa;


import dean.zopa.lender.Lender;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuoteTest {

	@Test
	public void Given_AmountAndLoanCalculator_Then_ReturnQuoteOutput() {
		MonetaryAmount amount = Money.of(1000, Config.CURRENCY);
		LoanCalculator loanCalculator = mock(LoanCalculator.class);

		Map<Lender, MonetaryAmount> amountToBorrowPerLender = mock(Map.class);
		when(loanCalculator.calcAmountToBorrowPerLender(amount)).thenReturn(amountToBorrowPerLender);

		BigDecimal rate = new BigDecimal("0.07");
		when(loanCalculator.calcLoanRate(amountToBorrowPerLender)).thenReturn(rate);

		MonetaryAmount monthlyRepayment = Money.of(30.781, Config.CURRENCY);
		when(loanCalculator.calcMonthlyRepayment(amount, rate, Config.REPAYMENT_PERIOD_IN_MONTHS)).thenReturn(monthlyRepayment);

		MonetaryAmount totalRepayment = Money.of(1108.1, Config.CURRENCY);
		when(loanCalculator.calcTotalRepayment(monthlyRepayment)).thenReturn(totalRepayment);

		Quote quote = new Quote(amount, loanCalculator);
		String expectedOutput = "Requested amount: £1,000.00\nRate: 7.0%\nMonthly repayment: £30.78\nTotal repayment: £1,108.10";
		assertThat(quote.giveQuote(), is(expectedOutput));
	}

}