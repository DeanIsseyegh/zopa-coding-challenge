package dean.zopa;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public class Lender implements Comparable {

	private String name;
	private BigDecimal rate;
	private MonetaryAmount available;

	public Lender(String name, BigDecimal rate, MonetaryAmount available) {
		this.name = name;
		this.rate = rate;
		this.available = available;
	}

	public MonetaryAmount getAvailable() {
		return available;
	}

	public void sub(MonetaryAmount subAmount) {
		available = available.subtract(subAmount);
	}

	public BigDecimal getRate() {
		return rate;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Object o) {
		return this.getRate().compareTo(((Lender) o).getRate());
	}
}
