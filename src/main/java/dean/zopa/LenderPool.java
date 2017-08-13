package dean.zopa;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;

public class LenderPool {

	private List<Lender> lenders;

	public LenderPool(List<Lender> lenders) {
		this.lenders = lenders;
	}

	public List<Lender> getLenders() {
		return lenders;
	}

	public BigDecimal sumAllRates() {
		return lenders.stream().
				map(Lender::getRate).
				reduce(BigDecimal::add).
				orElse(BigDecimal.ZERO);
	}

	public MonetaryAmount sumAllAvailableAmounts() {
		return lenders.stream().
				map(Lender::getAvailable).
				reduce(MonetaryFunctions.sum()).
				orElse(Money.of(0, Config.CURRENCY));
	}
}
