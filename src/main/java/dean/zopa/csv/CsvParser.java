package dean.zopa.csv;

import dean.zopa.lender.Lender;

import java.util.ArrayList;
import java.util.List;

public class CsvParser {

	private String csvContent;

	public CsvParser(String csvContent) {
		this.csvContent = csvContent;
	}

	public List<Lender> parseLenders() {
		return new ArrayList<>();
	}

}
