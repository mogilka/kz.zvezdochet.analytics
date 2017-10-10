package kz.zvezdochet.analytics.test;

public class NumerologyTest {

	public NumerologyTest() {
	}

	public static void main(String[] args) {
		String sdate = "781975";
		int number = Integer.valueOf(sdate);
		while (number > 9) {
			String s = String.valueOf(number);
			number = 0;
			String[] syms = s.split("");
			for (int i = 0; i < syms.length; i++) {
				int n = Integer.valueOf(syms[i]);
				number += n;
			}
		}
		System.out.println(sdate + " - " + number);
	}
}
