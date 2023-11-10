import fractals.FractalExplorer;

public class FractalsTest {

	public static void main(String[] args){
		FractalExplorer fracExp = new FractalExplorer(500);
		fracExp.createAndShowGUI();
		fracExp.drawFractal();
		}
}
