package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InputParser {

	private InputValidator inputValidator;

	public InputParser(InputValidator inputValidator) {
		this.inputValidator = inputValidator;
	}

	public List<Lender> parseLenders(List<String> content) throws IOException {
		List<Lender> lenders = new ArrayList<>();
		content = removeFirstLine(content);
		for (String string : content) {
			String[] spl = string.split(",");
			Lender lender = new Lender(spl[0],
					new BigDecimal(spl[1]),
					Money.of(new BigDecimal(spl[2]), Config.CURRENCY));
			inputValidator.validateLender(lender);
			lenders.add(lender);
		}
		inputValidator.validateUniqueLenders(lenders);
		return lenders;
	}

	private List<String> removeFirstLine(List<String> content) {
		return content.subList(1, content.size());
	}

	public BigDecimal parseAmount(String amount) {
		BigDecimal parsedAmount = new BigDecimal(amount);
		inputValidator.validateAmountRequested(parsedAmount);
		return  parsedAmount;
	}

}