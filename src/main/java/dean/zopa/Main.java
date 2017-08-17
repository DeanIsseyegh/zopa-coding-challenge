package dean.zopa;

import dean.zopa.logic.LoanAlgorithm;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Main {

	public static void main(String... args) {
		Lender lender1 = new Lender("Bob", new BigDecimal("0.075"), Money.of(640, Config.CURRENCY));
		Lender lender2 = new Lender("Jane", new BigDecimal("0.069"), Money.of(480, Config.CURRENCY));
		Lender lender3 = new Lender("Fred", new BigDecimal("0.071"), Money.of(520, Config.CURRENCY));
		Lender lender4 = new Lender("Mary", new BigDecimal("0.104"), Money.of(170, Config.CURRENCY));
		Lender lender5 = new Lender("John", new BigDecimal("0.081"), Money.of(320, Config.CURRENCY));
		Lender lender6 = new Lender("Dave", new BigDecimal("0.074"), Money.of(140, Config.CURRENCY));
		Lender lender7 = new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY));

		LenderPool lenderPool = new LenderPool(Arrays.asList(lender1, lender2, lender3, lender4, lender5, lender6, lender7));

		LoanCalculator loanCalculator = new LoanCalculator(new LoanAlgorithm(lenderPool));

		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = loanCalculator.calcAmountToBorrowPerLender(Money.of(200, Config.CURRENCY));
		MonetaryAmount totalAmount = Money.of(0, Config.CURRENCY);
		ArrayList<BigDecimal> rates = new ArrayList<>();
			for (Map.Entry<Lender, MonetaryAmount> mapEntry: amountsToBorrowPerLender.entrySet()) {
				totalAmount = totalAmount.add(mapEntry.getValue());
				rates.add(mapEntry.getKey().getRate());
			}

		BigDecimal totalRate = rates.stream().reduce(BigDecimal::add).get();
		BigDecimal averageWeight = totalRate.divide(new BigDecimal(rates.size()), 6, BigDecimal.ROUND_HALF_UP);
		averageWeight = averageWeight.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println("Amount to lend: " + totalAmount);
		System.out.println("Average weight: " + averageWeight);

	}

	//TODO: Deal with rounding issues/edge cases
}
