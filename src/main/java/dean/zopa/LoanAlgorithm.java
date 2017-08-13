package dean.zopa;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.*;

public class LoanAlgorithm {

	private LenderPool lenderPool;
	private int divisorScale = 3;

	public LoanAlgorithm(LenderPool lenderPool) {
		this.lenderPool = lenderPool;
	}

	public List<MonetaryAmount> splitBorrowerAmount(MonetaryAmount amount) {
		List<MonetaryAmount> splitAmounts = new ArrayList<>();
		for (long i = 0; i < amount.getNumber().longValue(); i++) {
			splitAmounts.add(Money.of(1, Config.CURRENCY));
		}
		return splitAmounts;
	}

	public Map<Lender, BigDecimal> calcLenderRatios(MonetaryAmount borrowerAmount) {
		BigDecimal summedRates = lenderPool.sumAllRates();
		Map<Lender, BigDecimal> ratioToBorrowPerLender = new TreeMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			BigDecimal ratio = lender.getRate().divide(summedRates, divisorScale, BigDecimal.ROUND_HALF_UP);
			ratioToBorrowPerLender.put(lender, ratio);
		}
		return reverseLenderRatios(ratioToBorrowPerLender);
	}

	//TODO This was kinda hacked, re-do with tdd
	private Map<Lender, BigDecimal> reverseLenderRatios(Map<Lender, BigDecimal> lenderRatios) {
		ArrayList<BigDecimal> arrayList = new ArrayList<>(lenderRatios.values());
		TreeMap<Lender, BigDecimal> reversedRatios = new TreeMap<>();
		int i = 0;
		for (Map.Entry<Lender, BigDecimal> entry : lenderRatios.entrySet()) {
			reversedRatios.put(entry.getKey(), arrayList.get((lenderRatios.size() - 1) - i));
			i++;
		}
		return reversedRatios;
	}

	public Map<Lender, MonetaryAmount> calcAmountsToBorrowPerLender(MonetaryAmount borrowerAmount, Map<Lender, BigDecimal> lenderRatios) {
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			MonetaryAmount amountToBorrowFromLender = borrowerAmount.multiply(lenderRatios.get(lender));
			amountToBorrowPerLender.put(lender, amountToBorrowFromLender);
		}
		return amountToBorrowPerLender;
	}

	public Map<Lender, MonetaryAmount> calcLenderRatioX(MonetaryAmount borrowerAmount) {
		BigDecimal summedRates = lenderPool.sumAllRates();
		HashMap<Lender, BigDecimal> ratioToBorrowPerLender = new HashMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			BigDecimal ratio = lender.getRate().divide(summedRates, 3, BigDecimal.ROUND_HALF_UP);
			System.out.println(ratio);
			ratioToBorrowPerLender.put(lender, ratio);
		}
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			BigDecimal lenderRatio = BigDecimal.ONE.subtract(ratioToBorrowPerLender.get(lender));
			MonetaryAmount amountToBorrowFromLender = borrowerAmount.multiply(lenderRatio);
			System.out.println(amountToBorrowFromLender);
			amountToBorrowPerLender.put(lender, amountToBorrowFromLender);
		}
		return amountToBorrowPerLender;
	}

}
