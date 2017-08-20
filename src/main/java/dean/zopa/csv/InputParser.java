package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InputParser {

	private List<String> content;
	private InputValidator inputValidator;

	public InputParser(List<String> content, InputValidator inputValidator) {
		this.content = content;
		this.inputValidator = inputValidator;
	}

	public List<Lender> parseLenders() throws IOException {
		List<Lender> lenders = new ArrayList<>();
		removeFirstLine();
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

	private void removeFirstLine() {
		content = content.subList(1, content.size());
	}

}
