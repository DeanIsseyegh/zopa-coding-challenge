package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class InputParserTest {

	@Test
	public void Given_ContentOfOneLender_Then_ReturnListOfLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60");
		List<Lender> expectedLenders = Arrays.asList(
				new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY))
		);
		InputParser inputParser = new InputParser(content, new InputValidator());
		assertThat(inputParser.parseLenders().toString(),
				is(expectedLenders.toString()));
	}

	@Test
	public void Given_ContentOfLenders_Then_ReturnListOfLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60", "Jane,0.069,480");
		List<Lender> expectedLenders = Arrays.asList(
				new Lender("Angela", new BigDecimal("0.071"), Money.of(60, Config.CURRENCY)),
				new Lender("Jane", new BigDecimal("0.069"), Money.of(480, Config.CURRENCY))
		);
		InputParser inputParser = new InputParser(content, new InputValidator());
		assertThat(inputParser.parseLenders().toString(),
				is(expectedLenders.toString()));
	}

	@Test
	public void Given_ContentOfOneLender_Then_ValidateLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60");
		InputValidator inputValidator = mock(InputValidator.class);
		InputParser inputParser = new InputParser(content, inputValidator);

		inputParser.parseLenders();

		Mockito.verify(inputValidator, times(1)).validateLender(any(Lender.class));
		Mockito.verify(inputValidator, times(1)).validateUniqueLenders(any(List.class));
	}

	@Test
	public void Given_ContentOfLenders_Then_ValidateLenders() throws IOException {
		List<String> content = Arrays.asList("Lender,Rate,Available", "Angela,0.071,60", "Jane,0.069,480");
		InputValidator inputValidator = mock(InputValidator.class);
		InputParser inputParser = new InputParser(content, inputValidator);

		inputParser.parseLenders();

		Mockito.verify(inputValidator, times(2)).validateLender(any(Lender.class));
		Mockito.verify(inputValidator, times(1)).validateUniqueLenders(any(List.class));
	}

}