package dean.zopa;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoanCalculator {

	private LoanAlgorithm loanAlgorithm;
	private Map<Lender, MonetaryAmount> amountsToBorrowPerLender = new HashMap<>();
	public final static MonetaryAmount MIN_LEFTOVER_AMOUNT_THRESHOLD = Money.of(0.001, Config.CURRENCY);

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

}
