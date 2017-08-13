package dean.zopa;

import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
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
	public void Calculates_Sum_Of_Lender_Rates() {
		Lender lender1 = mock(Lender.class);
		Lender lender2 = mock(Lender.class);
		List<Lender> lenders = Arrays.asList(lender1, lender2);
		lenders.forEach(lender -> when(lender.getRate()).thenReturn(new BigDecimal("0.2")));
		LenderPool lenderPool = new LenderPool(lenders);

		assertThat(lenderPool.sumAllRates(), is(new BigDecimal("0.4")));
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

}