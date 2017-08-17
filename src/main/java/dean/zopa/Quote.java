package dean.zopa;

import dean.zopa.lender.Lender;
import dean.zopa.logic.LoanCalculator;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import static dean.zopa.Config.CURRENCY;
import static dean.zopa.Config.LOCALE;

public class Quote {

	private BigDecimal rate;
	private MonetaryAmount amount;
	private MonetaryAmount monthlyPaymentAmount;
	private MonetaryAmount totalRepaymentAmount;
	public final static int REPAYMENT_PERIOD_MONTHS = 36;

	public Quote(MonetaryAmount amount, LoanCalculator loanCalculator) {
		this.amount = amount;
		Map<Lender, MonetaryAmount> amountToBorrowPerLender = loanCalculator.calcAmountToBorrowPerLender(amount);
		rate = loanCalculator.calcLoanRate(amountToBorrowPerLender);
		monthlyPaymentAmount = loanCalculator.calcMonthlyRepayment(amount, rate, REPAYMENT_PERIOD_MONTHS);
		totalRepaymentAmount = loanCalculator.calcTotalRepayment(monthlyPaymentAmount);
	}

	@Override
	public String toString() {
		return "Requested amount: " + format(amount) + "\n" +
				"Rate: " + format(rate) + "%\n" +
				"Monthly repayment: " + format(monthlyPaymentAmount) + "\n" +
				"Total repayment: " + format(totalRepaymentAmount);
	}

	private String format(BigDecimal unformatted) {
		return unformatted.multiply(new BigDecimal(100)).setScale(1, BigDecimal.ROUND_HALF_EVEN).toString();
	}

	private String format(MonetaryAmount unformatted) {
		MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(LOCALE);
		String formatted = format.format(unformatted);
		return  formatted.replace(CURRENCY,
				Currency.getInstance(CURRENCY).getSymbol());
	}

}
