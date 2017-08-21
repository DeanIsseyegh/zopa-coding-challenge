package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InputParserTest {

	@Test
	public void Given_ContentOfOneLender_Then_ReturnListOfLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60");
		List<Lender> expectedLenders = Arrays.asList(
				new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY))
		);
		InputParser inputParser = new InputParser(new InputValidator());
		assertThat(inputParser.parseLenders(content).toString(),
				is(expectedLenders.toString()));
	}

	@Test
	public void Given_ContentOfLenders_Then_ReturnListOfLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60", "Jane,0.069,480");
		List<Lender> expectedLenders = Arrays.asList(
				new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY)),
				new Lender("Jane", new BigDecimal("0.069"), Money.of(480, Config.CURRENCY))
		);
		InputParser inputParser = new InputParser(new InputValidator());
		assertThat(inputParser.parseLenders(content).toString(),
				is(expectedLenders.toString()));
	}

	@Test
	public void Given_ContentOfOneLender_Then_ValidateLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60");
		InputValidator inputValidator = mock(InputValidator.class);
		InputParser inputParser = new InputParser(inputValidator);

		inputParser.parseLenders(content);

		verify(inputValidator, times(1)).validateLender(any(Lender.class));
		verify(inputValidator, times(1)).validateUniqueLenders(any(List.class));
	}

	@Test
	public void Given_ContentOfLenders_Then_ValidateLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60", "Jane,0.069,480");
		InputValidator inputValidator = mock(InputValidator.class);
		InputParser inputParser = new InputParser(inputValidator);

		inputParser.parseLenders(content);

		verify(inputValidator, times(2)).validateLender(any(Lender.class));
		verify(inputValidator, times(1)).validateUniqueLenders(any(List.class));
	}

	@Test
	public void Given_AmountRequested_Then_ReturnParsedAmount() {
		InputParser inputParser = new InputParser(mock(InputValidator.class));
		assertThat(inputParser.parseAmount("1000", null), is(Money.of(1000, Config.CURRENCY)));
	}

	@Test
	public void Given_AmountRequested_Then_ValidateAmount() {
		InputValidator inputValidator = mock(InputValidator.class);
		InputParser inputParser = new InputParser(inputValidator);
		LenderPool lenderPool = new LenderPool(null);
		inputParser.parseAmount("1000", lenderPool);
		verify(inputValidator, times(1)).validateAmountRequested(new BigDecimal("1000"), lenderPool);
	}

}