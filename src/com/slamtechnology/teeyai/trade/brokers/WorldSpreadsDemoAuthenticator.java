package com.slamtechnology.teeyai.trade.brokers;

public class WorldSpreadsDemoAuthenticator extends WorldSpreadsAuthenticator {

	
	public static void main(String[] args){
		WorldSpreadsDemoAuthenticator auth = new WorldSpreadsDemoAuthenticator();
		auth.destroySession();
		auth.getSession();
	}
	
	@Override
	protected String getLoginFormURL() {
		return "https://www.worldspreads.com/en/home.aspx";
	}

	@Override
	protected String getUsername() {
		return "2044570";//"2032229";//"2031428";//"2030142";//"2029560";//"2028795";//"2028214";//"2027530";//"2026431";//"2025789";//"2025182";//"2024333";//"2023847";//"2023220";//"2022813";//"2022155";
	}

	@Override
	protected String getPassword() {
		return "Q4XKLF11";//"5C761J51";//"8CDPY1X2";//"HP6342PQ";//"TR5663RZ";//"11C4X94H";//"AK4BGA13";//"982B1EG8";//"5LN874TC";//"5AX256WX";//"6T4G1232";//"16W16S33";//"X1326722";//"B3KZ1Y3N";//"S8HW76GM";
	}


}
