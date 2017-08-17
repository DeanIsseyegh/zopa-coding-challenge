package dean.zopa.lender;

import dean.zopa.Config;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class LenderPool {

	private List<Lender> lenders;

	public LenderPool(List<Lender> lenders) {
		this.lenders = lenders;
	}

	public List<Lender> getLenders() {
		return lenders;
	}

	public BigDecimal sumAllWeightedRates() {
		return lenders.stream().
				map(Lender::getWeightedRate).
				reduce(BigDecimal::add).
				orElse(BigDecimal.ZERO);
	}

	public MonetaryAmount sumAllAvailableAmounts() {
		return lenders.stream().
				map(Lender::getAvailable).
				reduce(MonetaryFunctions.sum()).
				orElse(Money.of(0, Config.CURRENCY));
	}

	public void removeLendersWithNoAvailFromPool() {
		lenders = lenders.stream()
				.filter(lender -> lender.getAvailable().isGreaterThan(Money.of(0, Config.CURRENCY)))
				.collect(Collectors.toList());
	}
}
