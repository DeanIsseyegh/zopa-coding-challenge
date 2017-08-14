package dean.zopa;

import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class LenderTest {

	@Test
	public void Gets_Lender_Name() {
		Lender lender = new Lender("bob", null, null);
		assertThat(lender.getName(), is("bob"));
	}

	@Test
	public void Gets_Lender_Rate() {
		Lender lender = new Lender(null, new BigDecimal(100), null);
		assertThat(lender.getRate(), is(new BigDecimal(100)));
	}

	@Test
	public void Gets_Weighted_Lender_Rate() {
		Lender lender = new Lender(null, new BigDecimal("0.8"), null);
		assertThat(lender.getWeightedRate(), is(new BigDecimal("0.2")));

		lender = new Lender(null, new BigDecimal("0.3"), null);
		assertThat(lender.getWeightedRate(), is(new BigDecimal("0.7")));
	}

	@Test
	public void Gets_Lender_AvailableAmount() {
		Lender lender = new Lender(null, null, Money.of(100, "GBP"));
		assertThat(lender.getAvailable(), is(Money.of(100, "GBP")));
	}

	@Test
	public void Subtracts_AvailableAmount() {
		Lender lender = new Lender(null, null, Money.of(100, "GBP"));
		lender.sub(Money.of(10, "GBP"));
		assertThat(lender.getAvailable(), is(Money.of(90, "GBP")));
	}

	@Test
	public void Given_LendersWithDifferentRates_CompareBasedOnRates() {
		Lender lender1 = new Lender("l1", new BigDecimal("0.2"), null);
		Lender lender2 = new Lender("l2", new BigDecimal("0.4"), null);
		lender1.compareTo(lender2);
		assertThat(lender1.compareTo(lender2), is(1));
		assertThat(lender2.compareTo(lender1), is(-1));
	}

	@Test
	public void Given_LendersWithSameRates_CompareBasedOnName() {
		Lender lender1 = new Lender("a", new BigDecimal("0.2"), null);
		Lender lender2 = new Lender("b", new BigDecimal("0.2"), null);
		lender1.compareTo(lender2);
		assertThat(lender1.compareTo(lender2), is(-1));
		assertThat(lender2.compareTo(lender1), is(1));
	}

}