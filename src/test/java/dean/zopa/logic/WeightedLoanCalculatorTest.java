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

import static dean.zopa.logic.WeightedLoanCalculator.MIN_LEFTOVER_AMOUNT_THRESHOLD;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WeightedLoanCalculatorTest {

	@Test
	public void Returns_AmountToBorrowPerLender() {
		WeightedLoanAlgorithm loanAlgorithm = mock(WeightedLoanAlgorithm.class);
		WeightedLoanCalculator loanCalculator = new WeightedLoanCalculator(loanAlgorithm);
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
		WeightedLoanAlgorithm loanAlgorithm = mock(WeightedLoanAlgorithm.class);
		WeightedLoanCalculator loanCalculator = new WeightedLoanCalculator(loanAlgorithm);
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

		WeightedLoanAlgorithm loanAlgorithm = mock(WeightedLoanAlgorithm.class);
		WeightedLoanCalculator loanCalculator = new WeightedLoanCalculator(loanAlgorithm);

		when(loanCalculator.calcLoanRate(amountToBorrowPerLender)).thenReturn(new BigDecimal("0.1000"));
		assertThat(loanCalculator.calcLoanRate(amountToBorrowPerLender), is(new BigDecimal("0.1000")));
	}

	@Test
	public void Given_AmountAndRateAndPaymentPeriods_Then_ReturnMonthlyPayment() {
		WeightedLoanAlgorithm loanAlgorithm = mock(WeightedLoanAlgorithm.class);
		WeightedLoanCalculator loanCalculator = new WeightedLoanCalculator(loanAlgorithm);

		MonetaryAmount principalAmount = Money.of(1, Config.CURRENCY);
		BigDecimal rate = BigDecimal.ONE;

		MonetaryAmount expectedAmount = Money.of(1, Config.CURRENCY);
		when(loanAlgorithm.calcMonthlyRepayment(principalAmount, rate, 36)).thenReturn(expectedAmount);

		MonetaryAmount monthlyRepayment = loanCalculator.calcMonthlyRepayment(principalAmount, rate, 36);
		assertThat(monthlyRepayment, is(expectedAmount));
	}

	@Test
	public void Given_MonthlyRepaymentAmount_Then_ReturnTotalRepayment() {
		MonetaryAmount monthlyAmount = Money.of(10, Config.CURRENCY);
		WeightedLoanCalculator loanCalculator = new WeightedLoanCalculator(null);
		assertThat(loanCalculator.calcTotalRepayment(monthlyAmount), is(Money.of(360, Config.CURRENCY)));
	}
}