package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static dean.zopa.logic.LoanCalculator.MIN_LEFTOVER_AMOUNT_THRESHOLD;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoanCalculatorTest {

	@Test
	public void Returns_AmountToBorrowPerLender() {
		LoanAlgorithm loanAlgorithm = mock(LoanAlgorithm.class);
		LoanCalculator loanCalculator = new LoanCalculator(loanAlgorithm);
		MonetaryAmount amountToBorrow = Money.of(10, Config.CURRENCY);

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		when(loanAlgorithm.calcLenderRatios()).thenReturn(lenderRatios);

		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		when(loanAlgorithm.calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios)).thenReturn(amountToBorrowPerLender);

		MonetaryAmount leftOverAmount = Money.of(0, Config.CURRENCY);
		when(loanAlgorithm.updateLenderAmountsAndReturnLeftOverAmount(amountToBorrowPerLender)).thenReturn(leftOverAmount);

		Map<Lender, MonetaryAmount> result = loanCalculator.calcAmountToBorrowPerLender(amountToBorrow);

		assertThat(result, is(amountToBorrowPerLender));
		verify(loanAlgorithm, times(1)).calcLenderRatios();
		verify(loanAlgorithm, times(1)).calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios);
		verify(loanAlgorithm, times(1)).updateLenderAmountsAndReturnLeftOverAmount(amountToBorrowPerLender);
	}

	@Test
	public void Keeps_Using_LoanAlgorithm_Until_LeftOverAmount_Is_PastThreshold() {
		LoanAlgorithm loanAlgorithm = mock(LoanAlgorithm.class);
		LoanCalculator loanCalculator = new LoanCalculator(loanAlgorithm);
		MonetaryAmount amountToBorrow = Money.of(10, Config.CURRENCY);

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		when(loanAlgorithm.calcLenderRatios()).thenReturn(lenderRatios);

		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		when(loanAlgorithm.calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios))
				.thenReturn(amountToBorrowPerLender);

		MonetaryAmount tenPounds = Money.of(10, Config.CURRENCY);
		when(loanAlgorithm.updateLenderAmountsAndReturnLeftOverAmount(amountToBorrowPerLender))
				.thenReturn(tenPounds, tenPounds, tenPounds, MIN_LEFTOVER_AMOUNT_THRESHOLD);

		Map<Lender, MonetaryAmount> result = loanCalculator.calcAmountToBorrowPerLender(amountToBorrow);

		assertThat(result, is(amountToBorrowPerLender));
		verify(loanAlgorithm, times(4)).mergeMaps(amountToBorrowPerLender, amountToBorrowPerLender);
		verify(loanAlgorithm, times(4)).calcLenderRatios();
		verify(loanAlgorithm, times(4)).calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios);
		verify(loanAlgorithm, times(4)).updateLenderAmountsAndReturnLeftOverAmount(amountToBorrowPerLender);
	}

	@Test
	public void Given_Lenders_Then_ReturnLoanRate() {
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new TreeMap<>();

		LoanAlgorithm loanAlgorithm = mock(LoanAlgorithm.class);
		LoanCalculator loanCalculator = new LoanCalculator(loanAlgorithm);

		when(loanCalculator.calcLoanRate(amountToBorrowPerLender)).thenReturn(new BigDecimal("0.1000"));
		assertThat(loanCalculator.calcLoanRate(amountToBorrowPerLender), is(new BigDecimal("0.1000")));
	}

	@Test
	public void Given_Amount_And_Rate_And_PaymentPeriods_Then_ReturnMonthlyPayment() {
		LoanCalculator loanCalculator = new LoanCalculator(null);
		MonetaryAmount principalAmount = Money.of(1000, Config.CURRENCY);
		BigDecimal rate = new BigDecimal("0.07");
		MonetaryAmount monthlyRepayment = loanCalculator.calcMonthlyRepayment(principalAmount, rate, 36);
		String expectedAmount = Config.CURRENCY + " 30.87";
		assertTrue(monthlyRepayment.toString().startsWith(expectedAmount));
	}

	@Test
	public void Given_MonthlyRepaymentAmount_Then_ReturnTotalRepayment() {
		MonetaryAmount monthlyAmount = Money.of(10, Config.CURRENCY);
		LoanCalculator loanCalculator = new LoanCalculator(null);
		assertThat(loanCalculator.calcTotalRepayment(monthlyAmount), is(Money.of(360, Config.CURRENCY)));
	}
}