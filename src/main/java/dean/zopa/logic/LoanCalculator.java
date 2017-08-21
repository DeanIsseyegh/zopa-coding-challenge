package dean.zopa.logic;

import dean.zopa.Config;
import dean.zopa.Main;
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
	public final static MonetaryAmount MIN_LEFTOVER_AMOUNT_THRESHOLD = Money.of(0.00001, Config.CURRENCY);

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
		return loanAlgorithm.calcLoanRate(amountsToBorrowPerLender);
	}

	public MonetaryAmount calcMonthlyRepayment(MonetaryAmount amount, BigDecimal rate, int repaymentPeriod) {
		return loanAlgorithm.calcMonthlyRepayment(amount, rate, repaymentPeriod);
	}

	public MonetaryAmount calcTotalRepayment(MonetaryAmount monthlyRepayment) {
		return monthlyRepayment.multiply(Config.REPAYMENT_PERIOD_IN_MONTHS);
	}

}
