package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class InputValidator {

	public void generalValidate(String content) {
		if (content == null || content.length() == 0 || content.trim().length() == 0) {
			throw new IllegalArgumentException("Row or file content is empty for content:\n" + content);
		}
	}

	public void validateRow(String content) {
		generalValidate(content);
		String[] commaSplitRow = content.split(",");
		if (commaSplitRow.length != 3) {
			throw new IllegalArgumentException("Row must contain 3 columns for content:\n" + content);
		} else {
			Stream.of(commaSplitRow).forEach(val -> generalValidate(val));
		}
	}

	public void validateLender(Lender lender) {
		//generalValidate(lender.getName());
		if (lender.getAvailable().isLessThanOrEqualTo(Money.of(0, Config.CURRENCY))) {
			throw new IllegalArgumentException("Lender had 0 or less amount of money:\n" + lender);
		}
	}

}