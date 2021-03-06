package dean.zopa.lender;

import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LenderPoolTest {

	@Test
	public void Gets_Lenders() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		List<Lender> lenders = Arrays.asList(lender1, lender2);
		LenderPool lenderPool = new LenderPool(lenders);

		assertThat(lenderPool.getLenders(), is(lenders));
	}

	@Test
	public void Calculates_Weighted_Sum_Of_LenderRates() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		List<Lender> lenders = Arrays.asList(lender1, lender2);
		lenders.forEach(lender -> when(lender.getWeightedRate()).thenReturn(new BigDecimal("0.2")));
		LenderPool lenderPool = new LenderPool(lenders);

		assertThat(lenderPool.sumAllWeightedRates(), is(new BigDecimal("0.4")));
	}

	@Test
	public void Calculates_Sum_Of_Lender_Amounts() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		List<Lender> lenders = Arrays.asList(lender1, lender2);
		lenders.forEach(lender -> when(lender.getAvailable()).thenReturn(Money.of(100, "GBP")));
		LenderPool lenderPool = new LenderPool(lenders);

		assertThat(lenderPool.sumAllAvailableAmounts(), is(Money.of(200, "GBP")));
	}

	@Test
	public void Removes_Lenders_With_No_Available_Amounts_Left() {
		Lender lender = mock(Lender.class);
		when(lender.getAvailable()).thenReturn(Money.of(0, "GBP"));
		LenderPool lenderPool = new LenderPool(Collections.singletonList(lender));

		lenderPool.removeLendersWithNoAvailFromPool();

		assertThat(lenderPool.getLenders().size(), is(0));
	}

	@Test
	public void Does_Not_Remove_Lenders_With_Available_Amounts_Left() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);

		when(lender1.getAvailable()).thenReturn(Money.of(0, "GBP"));
		when(lender2.getAvailable()).thenReturn(Money.of(1, "GBP"));

		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2));

		lenderPool.removeLendersWithNoAvailFromPool();

		assertThat(lenderPool.getLenders().size(), is(1));
		assertThat(lenderPool.getLenders().get(0), is(lender2));
	}

	@Test
	public void Removes_Multiple_Lenders_With_No_Available_Amounts_Left() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		Lender lender3 = mock(Lender.class);
		Lender lender4 = mock(Lender.class);

		when(lender1.getAvailable()).thenReturn(Money.of(0, "GBP"));
		when(lender2.getAvailable()).thenReturn(Money.of(5, "GBP"));
		when(lender3.getAvailable()).thenReturn(Money.of(10, "GBP"));
		when(lender4.getAvailable()).thenReturn(Money.of(0, "GBP"));

		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2, lender3, lender4));

		lenderPool.removeLendersWithNoAvailFromPool();

		assertThat(lenderPool.getLenders().size(), is(2));
		assertTrue(lenderPool.getLenders().contains(lender2));
		assertTrue(lenderPool.getLenders().contains(lender3));
	}


}