package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class InputValidator {

	public void generalValidate(String content) {
		if (content == null || content.trim().length() == 0) {
			throw new IllegalArgumentException("Row or file content is empty for content:\n" + content);
		}
	}

	public void validateUniqueLenders(List<Lender> lenders) {
		List<String> lenderNames = lenders.stream().map(Lender::getName).collect(Collectors.toList());
		HashSet<String> uniqueLenders = new HashSet<>(lenderNames);
		if (lenderNames.size() != uniqueLenders.size()) {
			throw new IllegalArgumentException("Cannot have lenders with the same name.");
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
		if (isMoreThanOrEqualToOne(lender.getRate()) || isLessThanZero(lender.getRate())) {
			throw new IllegalArgumentException("Lender has a rate of 100% or more:\n" + lender);
		}
	}

	private Boolean isMoreThanOrEqualToOne(BigDecimal number) {
		return number.compareTo(BigDecimal.ONE) >= 0;
	}

	private Boolean isLessThanZero(BigDecimal number) {
		return number.compareTo(BigDecimal.ZERO) < 0;
	}

}