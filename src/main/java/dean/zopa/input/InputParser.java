package dean.zopa.input;

import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;

import javax.money.MonetaryAmount;
import java.util.List;

public interface InputParser {

	List<Lender> parseLenders(List<String> content);

	MonetaryAmount parseAmount(String amount, LenderPool lenderPool);
}
