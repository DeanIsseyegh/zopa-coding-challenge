package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoanAlgorithmTest {

	private MonetaryAmount onePound = Money.of(1, Config.CURRENCY);

	@Test
	public void Given_LenderPoolWithOneLender_Return_LenderRatioMap() {
		Lender lender = new Lender("l1", new BigDecimal("0.1"), onePound);
		LenderPool lenderPool = new LenderPool(Collections.singletonList(lender));
		Map<Lender, BigDecimal> expectedRatios = Collections.singletonMap(lender, new BigDecimal("1.000000"));
		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcLenderRatios(), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithTwoLendersOfSameRates_Return_LenderRatioMap() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.2"), onePound);
		Lender lender2 = new Lender("l2", new BigDecimal("0.2"), onePound);
		List<Lender> lenders = Arrays.asList(lender1, lender2);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.500000"));
		expectedRatios.put(lender2, new BigDecimal("0.500000"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithTwoLendersWithDifferentRates_Return_WeightedLenderRatioMap() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.2"), onePound);
		Lender lender2 = new Lender("l2", new BigDecimal("0.4"), onePound);
		List<Lender> lenders = Arrays.asList(lender1, lender2);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.571429"));
		expectedRatios.put(lender2, new BigDecimal("0.428571"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(), is(expectedRatios));
	}

	@Test
	public void Given_LenderPoolWithFourLendersWithDifferentRates_Return_WeightedLenderRatioMap() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.05"), onePound);
		Lender lender2 = new Lender("l2", new BigDecimal("0.3"), onePound);
		Lender lender3 = new Lender("l3", new BigDecimal("0.02"), onePound);
		Lender lender4 = new Lender("l4", new BigDecimal("0.075"), onePound);
		List<Lender> lenders = Arrays.asList(lender1, lender2, lender3, lender4);

		Map<Lender, BigDecimal> expectedRatios = new TreeMap<>();
		expectedRatios.put(lender1, new BigDecimal("0.267229"));
		expectedRatios.put(lender2, new BigDecimal("0.196906"));
		expectedRatios.put(lender3, new BigDecimal("0.275668"));
		expectedRatios.put(lender4, new BigDecimal("0.260197"));

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcLenderRatios(), is(expectedRatios));
	}

	@Test
	public void Given_LenderRatiosMapWithOneLender_Return_MapOfAmountToBeBorrowedFromTheLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender = new Lender("l1", null, onePound);
		List<Lender> lenders = Collections.singletonList(lender);

		Map<Lender, BigDecimal> lenderRatios = Collections.singletonMap(lender, new BigDecimal("1.000"));
		Map<Lender, MonetaryAmount> expectedResult = Collections.singletonMap(lender, borrowerAmount);

		LoanAlgorithm algorithm = new LoanAlgorithm(new LenderPool(lenders));

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

	@Test
	public void Given_TwoEqualLenderRatios_Return_MapOfAmountToBeBorrowedPerLender() {
		MonetaryAmount borrowerAmount = Money.of(10, Config.CURRENCY);

		Lender lender1 = new Lender("l1", null, onePound);
		Lender lender2 = new Lender("l2", null, onePound);
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

		Lender lender1 = new Lender("l1", null, onePound);
		Lender lender2 = new Lender("l2", null, onePound);
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

		Lender lender1 = new Lender("l1", null, onePound);
		Lender lender2 = new Lender("l2", null, onePound);
		Lender lender3 = new Lender("l3", null, onePound);
		Lender lender4 = new Lender("l4", null, onePound);
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2, lender3, lender4));

		Map<Lender, BigDecimal> lenderRatios = new HashMap<>();
		lenderRatios.put(lender1, new BigDecimal("0.200"));
		lenderRatios.put(lender2, new BigDecimal("0.400"));
		lenderRatios.put(lender3, new BigDecimal("0.300"));
		lenderRatios.put(lender4, new BigDecimal("0.100"));

		Map<Lender, MonetaryAmount> expectedResult = new HashMap<>();
		expectedResult.put(lender1, Money.of(2.00, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(4.00, Config.CURRENCY));
		expectedResult.put(lender3, Money.of(3.00, Config.CURRENCY));
		expectedResult.put(lender4, Money.of(1.00, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);

		assertThat(algorithm.calcAmountsToBorrowPerLender(borrowerAmount, lenderRatios), is(expectedResult));
	}

	@Test
	public void Given_LendersAllHaveEnough_DoNotModify_AndReturnZeroLeftoverAmount() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.1"), Money.of(2.00, Config.CURRENCY));
		Lender lender2 = new Lender("l2", new BigDecimal("0.1"), Money.of(4.00, Config.CURRENCY));
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
		amountsToBorrowPerLender.put(lender1, Money.of(2.00, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender2, Money.of(4.00, Config.CURRENCY));

		Map<Lender, MonetaryAmount> expectedResult = new TreeMap<>();
		expectedResult.put(lender1, Money.of(2.00, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(4.00, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);
		MonetaryAmount leftOverMoney = algorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);

		assertThat(amountsToBorrowPerLender, is(expectedResult));
		assertThat(leftOverMoney, is(Money.of(0, Config.CURRENCY)));
	}

	@Test
	public void Given_OneLender__Then_ModifyTheAmountToLend_AndReturnLeftOverAmount() {
		Lender lender = new Lender("l1", new BigDecimal("0.1"), Money.of(10, Config.CURRENCY));
		LenderPool lenderPool = new LenderPool(Collections.singletonList(lender));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new TreeMap<>();
		amountsToBorrowPerLender.put(lender, Money.of(20, Config.CURRENCY));

		Map<Lender, MonetaryAmount> expectedResult = new TreeMap<>();
		expectedResult.put(lender, Money.of(10, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);
		MonetaryAmount leftOverMoney = algorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);

		assertThat(amountsToBorrowPerLender, is(expectedResult));
		assertThat(leftOverMoney, is(Money.of(10, Config.CURRENCY)));
	}

	@Test
	public void Given_TwoLenders_AndOneDoesNotHaveEnoughToLend_Then_ModifyTheAmountToLend_AndReturnLeftOverAmount() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.1"), Money.of(10, Config.CURRENCY));
		Lender lender2 = new Lender("l2", new BigDecimal("0.1"), Money.of(10, Config.CURRENCY));
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
		amountsToBorrowPerLender.put(lender1, Money.of(10, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender2, Money.of(20, Config.CURRENCY));

		Map<Lender, MonetaryAmount> expectedResult = new TreeMap<>();
		expectedResult.put(lender1, Money.of(10, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(10, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);
		MonetaryAmount leftOverMoney = algorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);

		assertThat(amountsToBorrowPerLender, is(expectedResult));
		assertThat(leftOverMoney, is(Money.of(10, Config.CURRENCY)));
	}

	@Test
	public void Given_FourLenders_AndTwoDoNotHaveEnoughToLend_Then_ModifyTheAmountToLend_AndReturnLeftOverAmount() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.1"), Money.of(10, Config.CURRENCY));
		Lender lender2 = new Lender("l2", new BigDecimal("0.1"), Money.of(11, Config.CURRENCY));
		Lender lender3 = new Lender("l3", new BigDecimal("0.1"), Money.of(12, Config.CURRENCY));
		Lender lender4 = new Lender("l4", new BigDecimal("0.1"), Money.of(20, Config.CURRENCY));
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2, lender3, lender4));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new TreeMap<>();
		amountsToBorrowPerLender.put(lender1, Money.of(8, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender2, Money.of(12, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender3, Money.of(16, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender4, Money.of(20, Config.CURRENCY));

		Map<Lender, MonetaryAmount> expectedResult = new TreeMap<>();
		expectedResult.put(lender1, Money.of(8, Config.CURRENCY));
		expectedResult.put(lender2, Money.of(11, Config.CURRENCY));
		expectedResult.put(lender3, Money.of(12, Config.CURRENCY));
		expectedResult.put(lender4, Money.of(20, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);
		MonetaryAmount leftOverMoney = algorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);

		assertThat(leftOverMoney, is(Money.of(5, Config.CURRENCY)));
		assertThat(amountsToBorrowPerLender, is(expectedResult));
	}

	@Test
	public void Given_TwoLenders_Then_SubtractTheirAvailableAmountsTheyAreLending() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		when(lender1.getAvailable()).thenReturn(Money.of(8, Config.CURRENCY));
		when(lender2.getAvailable()).thenReturn(Money.of(4, Config.CURRENCY));
		when(lender1.getName()).thenReturn("l1");
		when(lender2.getName()).thenReturn("l2");
		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
		amountsToBorrowPerLender.put(lender1, Money.of(8, Config.CURRENCY));
		amountsToBorrowPerLender.put(lender2, Money.of(12, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(lenderPool);
		algorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);

		verify(lender1, times(1)).sub(Money.of(8, Config.CURRENCY));
		verify(lender2, times(1)).sub(Money.of(4, Config.CURRENCY));
	}

	@Test
	public void Merges_Maps_By_Adding_Money_From_Same_Lender() {
		Lender lender = mock(Lender.class);
		Map<Lender, MonetaryAmount> map1 = Collections.singletonMap(lender, Money.of(1, Config.CURRENCY));
		Map<Lender, MonetaryAmount> map2 = Collections.singletonMap(lender, Money.of(2, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(mock(LenderPool.class));

		Map<Lender, MonetaryAmount> expectedMergedMap = Collections.singletonMap(lender, Money.of(3, Config.CURRENCY));

		assertThat(algorithm.mergeMaps(map1, map2), is(expectedMergedMap));
	}

	@Test
	public void Merges_Maps_With_Multiple_Lenders() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);

		Map<Lender, MonetaryAmount> map1 = new HashMap<>();
		map1.put(lender1, Money.of(5, Config.CURRENCY));
		map1.put(lender2, Money.of(10, Config.CURRENCY));

		Map<Lender, MonetaryAmount> map2 = new HashMap<>();
		map2.put(lender1, Money.of(20, Config.CURRENCY));
		map2.put(lender2, Money.of(30, Config.CURRENCY));

		LoanAlgorithm algorithm = new LoanAlgorithm(mock(LenderPool.class));

		Map<Lender, MonetaryAmount> expectedMergedMap = new HashMap<>();
		expectedMergedMap.put(lender1, Money.of(25, Config.CURRENCY));
		expectedMergedMap.put(lender2, Money.of(40, Config.CURRENCY));

		assertThat(algorithm.mergeMaps(map1, map2), is(expectedMergedMap));
	}

}