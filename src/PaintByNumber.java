//class for storing a paint-by-numbers image
import java.awt.*;
import java.awt.image.BufferedImage;

public class PaintByNumber {
	//	Here defined image filters to be used in edgeDetection()
	
	public static final double[][] VERTICAL_ED = {{1,0,-1}, {1,0,-1}, {1,0,-1}};
	public static final double[][] HORIZONTAL_ED = {{1,1,1}, {0,0,0}, {-1,-1,-1}};
	
	//the following definitions are just for test purposes. these would change based on the image file selected
	//we will probably have to use ArrayList or something instead of arrays
	private int[][] pixels; //ints corresponding to pixel colors
	private int[][] grid; //ints corresponding to color user has painted in
	private Color[] palette = {Color.LIGHT_GRAY, Color.RED, Color.BLUE, Color.CYAN}; //colors in image. index = grid number corresponding to color
	
	private int width;
	private int height;
	
	public PaintByNumber(/*take an image as a parameter here*/) {
		
		pixels = new int[50][50];
		grid = new int[50][50];
		
		
		//instead of filling pixels with 0s, fill with its values. grid can stay 0s
		//this image is stored like a paint-by-numbers. So rather than an array of pixels with colors associated, the array is filled with numbers
		//each pixel has an int corresponding to its color
		//palette[] is then filled with colors, the index in palette is the int associated with the color
		//technically 0 should not be used in pixels and the 0 index of palette should stay Color.LIGHT_GRAY
		
		//redefine pixels and palette below 
		
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 50; j++) {
				pixels[i][j] = 2;
				grid[i][j] = 0;
			}
		}
		width = pixels[0].length;
		height = pixels.length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Color getColor(int x, int y) {
		return palette[grid[y][x]];
	}
	
	//for debugging purposes
	public Color getActualColor(int x, int y) {
		return palette[pixels[y][x]];
	}
	
	public int getNumber(int x, int y) {
		return pixels[y][x];
	}
	
	public BufferedImage edgeDetection(BufferedImage buffImg, double[][] filter) {
		double[][][] imgArr = imageToArray(buffImg);
		double[][] convArr = convOnImgArr(imgArr, buffImg.getWidth(),buffImg.getHeight(),
				filter, filter.length, filter[0].length);
		return makeImgFromConvMatrix(buffImg,convArr);
	}
	
	/**
	 * Transforms Buffered Image to a 3D array.
	 * 
	 * @param buffImg: BufferedImage object
	 * @return double [][][] representing the image
	 */
	private double [][][] imageToArray(BufferedImage buffImg){
		int h = buffImg.getHeight();
		int w = buffImg.getWidth();
		// need to make a 2D array for each red,green,blue
		double[][][] imgArr = new double[3][h][w];
	
		for (int i =0; i < h; i++) {
			for (int j=0; j < w; j++) {
				Color col = new Color(buffImg.getRGB(j,i));
				imgArr[0][i][j] = col.getRed();
				imgArr[1][i][j] = col.getGreen();
				imgArr[2][i][j] = col.getBlue();
			}
		}
		return imgArr;
	}
	
	/**
	 * 
	 * @param imgArr
	 * @param w
	 * @param h
	 * @param kernel
	 * @param kH
	 * @param kW
	 * @return
	 */
	private double [][] convOnImgArr(double[][][] imgArr, int w, int h, double[][] kernel,
			int kH, int kW){
		Convolution op = new Convolution();
		
		double[][] rConv = op.matrixConvolution(imgArr[0], w, h, kernel, kH, kW);
		
	    double[][] gConv = op.matrixConvolution(imgArr[1], w, h, kernel, kH, kW);
	    
	    double[][] bConv = op.matrixConvolution(imgArr[2], w, h, kernel, kH, kW);
	    
	    double[][] combConv = new double[h][w];
	    
	    for (int i =0; i < h; i++) {
	    	for (int j = 0; j < w; j++) {
	    		combConv[i][j] = rConv[i][j] + gConv[i][j] + bConv[i][j];
	    	}
	    }
		return combConv;
	}
	
	private BufferedImage makeImgFromConvMatrix (BufferedImage userInImage, double[][] convMatrix) {
		BufferedImage res = new BufferedImage(userInImage.getWidth(),userInImage.getHeight(),BufferedImage.TYPE_INT_RGB);
		
		for (int i = 0; i < convMatrix.length; i++) {
			for (int j = 0; j < convMatrix[i].length; j++) {
				int tempC = fixRGBRange(convMatrix[i][j]);
				Color col = new Color(tempC,tempC,tempC);
				res.setRGB(j, i, col.getRGB());
			}
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param col
	 * @return
	 */
	private int fixRGBRange(double col) {
		int res = (col < 0) ? (int)-col : (int)col;
	    res = (col > 255) ? 255 : (int)col;
		return res;
	}
	
}