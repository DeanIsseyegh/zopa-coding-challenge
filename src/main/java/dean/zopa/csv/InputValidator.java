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

	public void validateAmountRequested(BigDecimal amount) {
		BigDecimal remainder = amount.remainder(new BigDecimal(100));
		if (BigDecimal.ZERO.compareTo(remainder) != 0) {
			throw new IllegalArgumentException("Amount requested of " + amount + " must be a 100 pound increment");
		}
		if (isLessThan(amount, new BigDecimal("1000")) || isMoreThanOrEqualTo(amount, new BigDecimal("15000.01"))) {
			throw new IllegalArgumentException("Amount requested of " + amount + " must be more than or equal to 1000");
		}
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
		if (isMoreThanOrEqualTo(lender.getRate(), BigDecimal.ONE) || isLessThan(lender.getRate(), BigDecimal.ZERO)) {
			throw new IllegalArgumentException("Lender has a rate of 100% or more:\n" + lender);
		}
	}

	private Boolean isMoreThanOrEqualTo(BigDecimal x, BigDecimal y) {
		return x.compareTo(y) >= 0;
	}

	private Boolean isLessThan(BigDecimal x, BigDecimal y) {
		return x.compareTo(y) < 0;
	}

}