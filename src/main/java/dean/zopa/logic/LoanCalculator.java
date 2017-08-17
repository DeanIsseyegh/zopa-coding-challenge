package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoanCalculator {

	private LoanAlgorithm loanAlgorithm;
	private Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
	public final static MonetaryAmount MIN_LEFTOVER_AMOUNT_THRESHOLD = Money.of(0.001, Config.CURRENCY);
	private final static BigDecimal NUM_OF_MONTHS = new BigDecimal("12");

	public LoanCalculator(LoanAlgorithm loanAlgorithm) {
		this.loanAlgorithm  = loanAlgorithm;
	}

	public Map<Lender, MonetaryAmount> calcAmountToBorrowPerLender(MonetaryAmount amountToBorrow) {
		Map<Lender, BigDecimal> lenderRatios = loanAlgorithm.calcLenderRatios();
		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = loanAlgorithm.calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios);
		MonetaryAmount leftOverAmount = loanAlgorithm.updateLenderAmountsAndReturnLeftOverAmount(amountsToBorrowPerLender);
		this.amountsToBorrowPerLender = loanAlgorithm.mergeMaps(this.amountsToBorrowPerLender, amountsToBorrowPerLender);
		if (leftOverAmount.isGreaterThan(MIN_LEFTOVER_AMOUNT_THRESHOLD)) {
			calcAmountToBorrowPerLender(leftOverAmount);
		}
		return amountsToBorrowPerLender;
	}

	public BigDecimal calcLoanRate(Map<Lender, MonetaryAmount> amountsToBorrowPerLender) {
		ArrayList<BigDecimal> rates = new ArrayList<>();
		for (Map.Entry<Lender, MonetaryAmount> mapEntry: amountsToBorrowPerLender.entrySet()) {
			rates.add(mapEntry.getKey().getRate());
		}
		BigDecimal summedRates = rates.stream().reduce(BigDecimal::add).get();
		return summedRates.divide(new BigDecimal(rates.size()), 4, BigDecimal.ROUND_HALF_UP);
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
		BigDecimal monthlyInterest = rate.divide(NUM_OF_MONTHS, 6, BigDecimal.ROUND_HALF_UP);
		MonetaryAmount pr = amount.multiply(monthlyInterest);

		BigDecimal onePlusR = BigDecimal.ONE.add(monthlyInterest);

		Double onePlusPowN = Math.pow(onePlusR.doubleValue(), (new BigDecimal(-repaymentPeriod)).doubleValue());

		return pr.divide(BigDecimal.ONE.subtract(new BigDecimal(onePlusPowN)));
	}

	public MonetaryAmount calcTotalRepayment(MonetaryAmount monthlyRepayment) {
		return monthlyRepayment.multiply(NUM_OF_MONTHS);
	}

}
