package dean.zopa.csv;

import com.sun.org.apache.xpath.internal.operations.Bool;
import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

public class InputValidator {

	public void generalValidate(String content) {
		if (content == null || content.trim().length() == 0) {
			throw new IllegalArgumentException("Row or file content is empty for content:\n" + content);
		}
	}

	public void validateLender(Lender lender) {
		validateName(lender);
		validateAmount(lender);
		validateRate(lender);
	}

	private void validateName(Lender lender) {
		generalValidate(lender.getName());
	}

	private void validateAmount(Lender lender) {
		if (lender.getAvailable().isLessThanOrEqualTo(Money.of(0, Config.CURRENCY))) {
			throw new IllegalArgumentException("Lender had 0 or less amount of money:\n" + lender);
		}
	}

	private void validateRate(Lender lender) {
		if (isMoreThanOne(lender.getRate()) || isLessThanZero(lender.getRate())) {
			throw new IllegalArgumentException("Lender has a rate of 100% or more:\n" + lender);
		}
	}

	private Boolean isMoreThanOne(BigDecimal number) {
		return number.compareTo(BigDecimal.ONE) >= 0;
	}

	private Boolean isLessThanZero(BigDecimal number) {
		return number.compareTo(BigDecimal.ZERO) < 0;
	}

}