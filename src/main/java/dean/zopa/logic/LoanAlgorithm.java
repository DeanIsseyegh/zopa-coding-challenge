package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Algorithm has weak point that if a lender does not have a lot of money but offers a good rate, and the borrower
 * requests a lot of money, all of the lenders money could end up for that one borrower.
 *
 * Alternative algorithm could be to not allow any lender to give more than 50% of their money to one borrower.
 */
public class LoanAlgorithm {

	private LenderPool lenderPool;
	private int divisorScale = 6;
	public final BigDecimal numOfMonths = new BigDecimal("12");

	public LoanAlgorithm(LenderPool lenderPool) {
		this.lenderPool = lenderPool;
	}

	Map<Lender, BigDecimal> calcLenderRatios() {
		BigDecimal summedRates = lenderPool.sumAllWeightedRates();
		Map<Lender, BigDecimal> ratioToBorrowPerLender = new TreeMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			BigDecimal ratio = lender.getWeightedRate().divide(summedRates, divisorScale, BigDecimal.ROUND_HALF_UP);
			ratioToBorrowPerLender.put(lender, ratio);
		}
		return ratioToBorrowPerLender;
	}

	Map<Lender, MonetaryAmount> calcAmountsToBorrowPerLender(MonetaryAmount borrowerAmount, Map<Lender, BigDecimal> lenderRatios) {
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = new HashMap<>();
		for (Lender lender : lenderPool.getLenders()) {
			MonetaryAmount amountToBorrowFromLender = borrowerAmount.multiply(lenderRatios.get(lender));
			amountToBorrowPerLender.put(lender, amountToBorrowFromLender);
		}
		return amountToBorrowPerLender;
	}

	MonetaryAmount updateLenderAmountsAndReturnLeftOverAmount(Map<Lender, MonetaryAmount> amountToBorrowPerLender) {
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

	public BigDecimal calcLoanRate(Map<Lender, MonetaryAmount> amountsToBorrowPerLender) {
		List<BigDecimal> rates = new ArrayList<>();
		MonetaryAmount total = calcTotalAmountToBorrow(amountsToBorrowPerLender);
		for (Map.Entry<Lender, MonetaryAmount> mapEntry: amountsToBorrowPerLender.entrySet()) {
			MonetaryAmount divided = mapEntry.getValue().divide(total.getNumber());
			BigDecimal dividedAsBigDec = new BigDecimal(divided.getNumber().toString());
			BigDecimal weightedRate = dividedAsBigDec.multiply(mapEntry.getKey().getRate());
			rates.add(weightedRate);
		}
		return rates.stream().reduce(BigDecimal::add).get().setScale(4, BigDecimal.ROUND_HALF_EVEN);
	}

	public MonetaryAmount calcTotalAmountToBorrow(Map<Lender, MonetaryAmount> amountsToBorrowPerLender) {
		return amountsToBorrowPerLender.entrySet().stream().
				map(it -> it.getValue()).
				reduce(MonetaryFunctions.sum()).get();
	}

	/**
	 * formula is c = (Pr / 1 - (1 / (1+r)^n))
	 *
	 * where:
	 * c = monthly repayment
	 * P = principal (amount)
	 * r = monthly interest rate
	 * n = number of payment periods
	 *
	 * This formula for the monthly payment on a U.S. mortgage is exact and is what banks use.
	 */
	public MonetaryAmount calcMonthlyRepayment(MonetaryAmount amount, BigDecimal rate, int repaymentPeriod) {
		BigDecimal monthlyInterest = rate.divide(numOfMonths, 6, BigDecimal.ROUND_HALF_UP);
		MonetaryAmount pr = amount.multiply(monthlyInterest);
		BigDecimal onePlusR = BigDecimal.ONE.add(monthlyInterest);
		Double onePlusPowN = Math.pow(onePlusR.doubleValue(), (new BigDecimal(-repaymentPeriod)).doubleValue());
		return pr.divide(BigDecimal.ONE.subtract(new BigDecimal(onePlusPowN)));
	}

	Map<Lender, MonetaryAmount> mergeMaps(Map<Lender, MonetaryAmount> map1, Map<Lender, MonetaryAmount> map2) {
		return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						MonetaryAmount::add
				));
	}

}
