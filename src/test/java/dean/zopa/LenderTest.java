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

}