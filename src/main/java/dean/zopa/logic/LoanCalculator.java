package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanCalculator {

	private LoanAlgorithm loanAlgorithm;
	private Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
	public final static MonetaryAmount MIN_LEFTOVER_AMOUNT_THRESHOLD = Money.of(0.0001, Config.CURRENCY);
	public final static BigDecimal NUM_OF_MONTHS = new BigDecimal("12");

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
		} //TODO Deal with rounding issues
		return amountsToBorrowPerLender;
	}

	//Move logic into algorithm class
	public BigDecimal calcLoanRate(Map<Lender, MonetaryAmount> amountsToBorrowPerLender) {
		List<BigDecimal> rates = new ArrayList<>();
		MonetaryAmount total = amountsToBorrowPerLender.entrySet().stream().
				map(it -> it.getValue()).
				reduce(MonetaryFunctions.sum()).get();
		for (Map.Entry<Lender, MonetaryAmount> mapEntry: amountsToBorrowPerLender.entrySet()) {
			MonetaryAmount divided = mapEntry.getValue().divide(total.getNumber());
			BigDecimal dividedAsBigDec = new BigDecimal(divided.getNumber().toString());
			BigDecimal weightedRate = dividedAsBigDec.multiply(mapEntry.getKey().getRate());
			rates.add(weightedRate);
		}
		return rates.stream().reduce(BigDecimal::add).get().setScale(4, BigDecimal.ROUND_HALF_EVEN);
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
		return monthlyRepayment.multiply(Config.REPAYMENT_PERIOD_IN_MONTHS);
	}

}
