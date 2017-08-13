package dean.zopa;

import org.javamoney.moneta.Money;
import org.junit.Ignore;
import org.junit.Test;

import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoanAlgorithmTest {

	@Test
	public void Splits_Up_Borrowers_Amount_into_1s() {
		MonetaryAmount amount = Money.of(3, "GBP");
		LoanAlgorithm algorithm = new LoanAlgorithm(null);
		MonetaryAmount expectedAmount = Money.of(1, Config.CURRENCY);
		assertThat(algorithm.splitBorrowerAmount(amount), is(Arrays.asList(expectedAmount,expectedAmount,expectedAmount)));
	}

	@Test
	public void Given_LenderPoolWithOneLender_Return_LenderRatioMap() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);
		Lender lender = new Lender("BobbyDropTables", new BigDecimal("0.1"), borrowerAmount);
		LenderPool lenderPool = new LenderPool(Collections.singletonList(lender));
		Map<Lender, BigDecimal> expectedRatios = Collections.singletonMap(lender, new BigDecimal("1.000"));
		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcLenderRatios(borrowerAmount), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithTwoLendersOfSameRates_Return_LenderRatioMap() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 =  new Lender("Bob", new BigDecimal("0.2"), null);
		Lender lender2 =  new Lender("Charlie", new BigDecimal("0.2"), null);
		List<Lender> lenders = Arrays.asList(lender1, lender2);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.500"));
		expectedRatios.put(lender2, new BigDecimal("0.500"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(borrowerAmount), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithTwoLendersWithDifferentRates_Return_WeightedLenderRatioMap() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender("Bob", new BigDecimal("0.2"), null);
		Lender lender2 = new Lender("Charlie", new BigDecimal("0.4"), null);
		List<Lender> lenders = Arrays.asList(lender1, lender2);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.667"));
		expectedRatios.put(lender2, new BigDecimal("0.333"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(borrowerAmount), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithFourLendersWithDifferentRates_Return_WeightedLenderRatioMap() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender("a", new BigDecimal("0.05"), null);
		Lender lender2 = new Lender("b", new BigDecimal("0.3"), null);
		Lender lender3 = new Lender("c", new BigDecimal("0.02"), null);
		Lender lender4 = new Lender("d", new BigDecimal("0.075"), null);
		List<Lender> lenders = Arrays.asList(lender1, lender2, lender3, lender4);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.169"));
		expectedRatios.put(lender2, new BigDecimal("0.045"));
		expectedRatios.put(lender3, new BigDecimal("0.674"));
		expectedRatios.put(lender4, new BigDecimal("0.112"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(borrowerAmount), is(expectedRatios));
	}

	@Test
	public void Given_LenderRatiosMapWithOneLender_Return_MapOfAmountToBeBorrowedFromTheLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender = new Lender(null, null, null);
		List<Lender> lenders = Collections.singletonList(lender);

		Map<Lender, BigDecimal> lenderRatios = Collections.singletonMap(lender, new BigDecimal("1.000"));
		Map<Lender, MonetaryAmount> expectedResult = Collections.singletonMap(lender, borrowerAmount);

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

	@Test
	public void Given_TwoEqualLenderRatios_Return_MapOfAmountToBeBorrowedPerLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender(null, null, null);
		Lender lender2 = new Lender(null, null, null);
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		lenderRatios.put(lender1, new BigDecimal("0.500"));
		lenderRatios.put(lender2, new BigDecimal("0.500"));

		Map<Lender, MonetaryAmount> expectedResult = new HashMap<>();
		expectedResult.put(lender1, Money.of(5, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(5, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

	@Test
	public void Given_TwoDifferentLenderRatios_Return_MapOfAmountToBeBorrowedPerLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender(null, null, null);
		Lender lender2 = new Lender(null, null, null);
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		lenderRatios.put(lender1, new BigDecimal("0.667"));
		lenderRatios.put(lender2, new BigDecimal("0.333"));

		Map<Lender, MonetaryAmount> expectedResult = new HashMap<>();
		expectedResult.put(lender1, Money.of(6.67, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(3.33, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

	@Test
	public void Given_FourDifferentLenderRatios_Return_MapOfAmountToBeBorrowedPerLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender(null, null, null);
		Lender lender2 = new Lender(null, null, null);
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		lenderRatios.put(lender1, new BigDecimal("0.200"));
		lenderRatios.put(lender2, new BigDecimal("0.400"));
		lenderRatios.put(lender1, new BigDecimal("0.300"));
		lenderRatios.put(lender2, new BigDecimal("0.100"));

		Map<Lender, MonetaryAmount> expectedResult = new HashMap<>();
		expectedResult.put(lender1, Money.of(2.00, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(4.00, Config.CURRENCY));
		expectedResult.put(lender1, Money.of(3.00, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(1.00, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

}