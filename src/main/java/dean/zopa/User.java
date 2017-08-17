package dean.zopa;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "rate", "available"})
class User {
	public String name;
	public double rate;
	public double available;
	public User() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(int age) {
		this.rate = age;
	}
	public double getAvailable() {
		return  available;
	}
	public void setAvailable(double available) {
		this.available = available;
	}
}