package dean.zopa;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoanAlgorithm {

	private LenderPool lenderPool;
	private int divisorScale = 6;

	public LoanAlgorithm(LenderPool lenderPool) {
		this.lenderPool = lenderPool;
	}

	public Map<Lender, BigDecimal> calcLenderRatios() {
		BigDecimal summedRates = lenderPool.sumAllWeightedRates();
		Map<Lender, BigDecimal> ratioToBorrowPerLender = new TreeMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			BigDecimal ratio = lender.getWeightedRate().divide(summedRates, divisorScale, BigDecimal.ROUND_HALF_UP);
			ratioToBorrowPerLender.put(lender, ratio);
		}
		return ratioToBorrowPerLender;
	}

	public Map<Lender, MonetaryAmount> calcAmountsToBorrowPerLender(MonetaryAmount borrowerAmount, Map<Lender, BigDecimal> lenderRatios) {
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			MonetaryAmount amountToBorrowFromLender = borrowerAmount.multiply(lenderRatios.get(lender));
			amountToBorrowPerLender.put(lender, amountToBorrowFromLender);
		}
		return amountToBorrowPerLender;
	}

	//TODO See if can split this method up
	public MonetaryAmount updateLenderAmountsAndReturnLeftOverAmount(Map<Lender, MonetaryAmount> amountToBorrowPerLender) {
		MonetaryAmount leftOverAmount = Money.of(0, Config.CURRENCY);
		for (Lender lender : amountToBorrowPerLender.keySet()) {
			MonetaryAmount amountToBorrow = amountToBorrowPerLender.get(lender);
			MonetaryAmount maxAvail = lender.getAvailable();
			if (amountToBorrow.isGreaterThan(maxAvail)) {
				MonetaryAmount amountCantLend = amountToBorrow.subtract(maxAvail);
				leftOverAmount = leftOverAmount.add(amountCantLend);
				amountToBorrow = amountToBorrow.subtract(amountCantLend);
				amountToBorrowPerLender.put(lender, amountToBorrow);
			}
			lender.sub(amountToBorrow);
		}
		return leftOverAmount;
	}

	public Map<Lender, MonetaryAmount> mergeMaps(Map<Lender, MonetaryAmount> map1, Map<Lender, MonetaryAmount> map2) {
		return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						MonetaryAmount::add
				));
	}

}
