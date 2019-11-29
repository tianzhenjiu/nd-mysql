package io.mqttpush;

public class TestSeed {
	
	public static void main(String[] args) throws Exception {
		
		
		
		String seed="U]Sg/Q-+-@=NP(s01DCa";
		
		String password="Readt123!";
	
		
		byte[] bs=Security.scramble411(password, seed,"Cp1252");
		
		for (int i = 0; i < bs.length; i++) {
			System.out.print(bs[i]+"\t");
		}
		System.out.println();
		
		
		//System.out.println(new String(Security.scramble411("".getBytes(), "".getBytes())));
	}

}
