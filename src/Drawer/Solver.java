package Drawer;

public class Solver {
	
	public static double solveHyp(int iX, int iY, int fX, int fY) {
		return Math.sqrt(Math.pow(Math.abs(fX-iX), 2) + Math.pow(Math.abs(fY-iY), 2));		
	}
	
	public static double solveAdj(double angle, double hype) {		
		return Math.cos(Math.toRadians(angle)) *hype;
	}
	
	public static double solveOpp(double angle, double hype) {				
		return Math.sin(Math.toRadians(angle))*hype;
	}
	
	public static double solveAngle(double adj, double opp) {				
		return Math.toDegrees(Math.atan(opp/adj));
	}

}
