package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
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

	public MonetaryAmount parseAmount(String amount, LenderPool lenderPool) {
		BigDecimal convertedAmount = new BigDecimal(amount);
		inputValidator.validateAmountRequested(convertedAmount, lenderPool);
		MonetaryAmount parsedAmount = Money.of(convertedAmount, Config.CURRENCY);
		return  parsedAmount;
	}

}
