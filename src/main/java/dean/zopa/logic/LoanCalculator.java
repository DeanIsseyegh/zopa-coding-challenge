package dean.zopa.logic;

import dean.zopa.lender.Lender;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Map;

public interface LoanCalculator {

	Map<Lender, MonetaryAmount> calcAmountToBorrowPerLender(MonetaryAmount amountToBorrow);

	BigDecimal calcLoanRate(Map<Lender, MonetaryAmount> amountsToBorrowPerLender);

	MonetaryAmount calcMonthlyRepayment(MonetaryAmount amount, BigDecimal rate, int repaymentPeriod);

	MonetaryAmount calcTotalRepayment(MonetaryAmount monthlyRepayment);

}
