//class for storing a paint-by-numbers image
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PaintByNumber {
	//	Here defined image filters to be used in edgeDetection()
	
	public static final double[][] VERTICAL_ED = {{-1,0,1}, {-1,0,1}, {-1,0,1}};
	public static final double[][] HORIZONTAL_ED = {{-1,-1,-1}, {0,0,0}, {1,1,1}};
	
	public static final double[][] SOBEL_VERT = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
	public static final double[][] SOBEL_HORZ = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
	
	public static final double[][] SCHARR_VERT = {{3, 0, -3}, {10, 0, -10}, {3, 0, -3}};
	public static final double[][] SCHARR_HORZ = {{3, 10, 3}, {0, 0, 0}, {-3, -10, -3}};
	
	public static final double[][] GAUSSIAN_BLUR = {{0.111f,0.111f,0.111f}, {0.111f,0.111f,0.111f}, {0.111f,0.111f,0.111f}};
	
	//the following definitions are just for test purposes. these would change based on the image file selected
	//we will probably have to use ArrayList or something instead of arrays
	private int[][] pixels; //ints corresponding to pixel colors
	private int[][] grid; //ints corresponding to color user has painted in
	private ArrayList<Color> palette;//colors in image. index = grid number corresponding to color
	private ArrayList<Color> userPalette;
	
	private int width;
	private int height;
	
	public PaintByNumber(BufferedImage buffImg) {
		
		pixels = new int[buffImg.getHeight()][buffImg.getWidth()];
		grid = new int[buffImg.getHeight()][buffImg.getHeight()];
		/*
		
		//instead of filling pixels with 0s, fill with its values. grid can stay 0s
		//this image is stored like a paint-by-numbers. So rather than an array of pixels with colors associated, the array is filled with numbers
		//each pixel has an int corresponding to its color
		//palette[] is then filled with colors, the index in palette is the int associated with the color
		//technically 0 should not be used in pixels and the 0 index of palette should stay Color.LIGHT_GRAY
		
		//redefine pixels and palette below 
		
		for (int i = 0; i < buffImg.getHeight(); i++) {
			for (int j = 0; j <buffImg.getWidth(); j++) {
				
				pixels[i][j] = fixRGBRange(mergedArr[i][j]);
				//pixels[i][j] =2;
				grid[i][j] = 0;
				
			}
		}
		*/
		int[][] colors = new int[buffImg.getWidth() * buffImg.getHeight()][5];
		
		for (int x = 0; x < buffImg.getWidth(); x++) {
            for (int y = 0; y < buffImg.getHeight(); y++) {
				grid[y][x] = 0;
				int index = x + y * buffImg.getWidth();
				Color col = new Color(buffImg.getRGB(x, y));
                colors[index][0] = x;
				colors[index][1] = y;
				colors[index][2] = col.getRed();
				colors[index][3] = col.getGreen();
				colors[index][4] = col.getBlue();
            }
        }
		
		palette = new ArrayList<Color>(1);
		palette.add(Color.LIGHT_GRAY);
		
		createBuckets(colors, 3, 0);
		
		width = pixels[0].length;
		height = pixels.length;
	}
	
	private void createBuckets(int[][] colors, int maxDepth, int depth) {
		//num colors = 2^maxDepth
		if (depth < maxDepth) {		
			int rLow = 255;
			int rHigh = 0;
			int gLow = 255;
			int gHigh = 0;
			int bLow = 255;
			int bHigh = 0;

			for (int i = 0; i < colors.length; i++) {
				rLow = Math.min(rLow, colors[i][2]);
				rHigh = Math.max(rHigh, colors[i][2]);
				gLow = Math.min(gLow, colors[i][3]);
				gHigh = Math.max(gHigh, colors[i][3]);
				bLow = Math.min(bLow, colors[i][4]);
				bHigh = Math.max(bHigh, colors[i][4]);
			}
			
			int rRange = rHigh - rLow;
			int gRange = gHigh - gLow;
			int bRange = bHigh - bLow;
			
			int indexToSortBy = 2;
			
			if (rRange > gRange && rRange >= bRange)  {
				indexToSortBy = 2;
			} else if (gRange > bRange && gRange >= rRange) {
				indexToSortBy = 3;
			} else {
				indexToSortBy = 4;
			}
			
			for (int i = 0; i < colors.length - 1; i++) {
				int minIndex = i;
				for (int j = i + 1; j < colors.length; j++) {
					if (colors[j][indexToSortBy] < colors[minIndex][indexToSortBy]) {
						minIndex = j;
					}
				}
				
				int[] temp = colors[minIndex];
				colors[minIndex] = colors[i];
				colors[i] = temp;
			}
			
			int[][] bucket1 = new int[colors.length / 2][5];
			int[][] bucket2 = new int[colors.length - (colors.length / 2)][5];
			
			for (int i = 0; i < colors.length; i++) {
				if (i < colors.length / 2) {
					bucket1[i] = colors[i];
				} else {
					bucket2[i - (colors.length / 2)] = colors[i];
				}
			}
			
			createBuckets(bucket1, maxDepth, depth + 1);
			createBuckets(bucket2, maxDepth, depth + 1);
		} else {
			int rTotal = 0;
			int gTotal = 0;
			int bTotal = 0;
			for (int i = 0; i < colors.length; i++) {
				rTotal += colors[i][2];
				gTotal += colors[i][3];
				bTotal += colors[i][4];
			}
			Color averageCol = new Color(rTotal / colors.length, gTotal / colors.length, bTotal / colors.length);
			
			for (int i = 0; i < colors.length; i++) {
				pixels[colors[i][1]][colors[i][0]] = palette.size();
			}
			
			palette.add(averageCol);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int paletteSize() {
		return palette.size();
	}
	
	public Color paletteColor(int i) {
		return palette.get(i);
	}
	
	public Color getColor(int x, int y) {
		//System.out.println(userPalette.size());
		return palette.get(grid[y][x]);
	}
	
	//for debugging purposes
	public Color getActualColor(int x, int y) {
		return palette.get(pixels[y][x]);
	}
	
	public void setGridColor(int x, int y, int color) {
		grid[y][x] = color;
	}
	
	public int getNumber(int x, int y) {
		return pixels[y][x];
	}
	
	public int colorToPalette(Color col) {
		return palette.indexOf(col);
	}
	
	public void setGridNumber(int x, int y, int num) {
		grid[y][x] = num;
	}
	/**
	 * Takes a BufferedImage object and a filter/kernel to use and applies the filter to that image
	 * @param buffImg			BufferedImage we want to convert
	 * @param filter			filter to apply... could be more than just edge detection
	 * @return					BufferedImage with filter applied to it.
	 */
	public BufferedImage edgeDetection(BufferedImage buffImg, double[][] filter,double[][] filter2) {
		// BufferedImage to 3D array
		double[][][] imgArr = imageToArray(buffImg);
		// apply convolution on 3D array and store result as 2D array
		double[][] convArr = convOnImgArr(imgArr, buffImg.getWidth(),buffImg.getHeight(),
				filter, filter.length, filter[0].length);
		// second array will hold horizontal edge detection
		double[][] convArr2 = convOnImgArr(imgArr,buffImg.getWidth(),buffImg.getHeight(),
				filter2, filter2.length, filter[0].length);
		double[][] mergedArr = new double[convArr.length][convArr[0].length];
		for (int i =0; i<mergedArr.length; i++){
			for (int j=0; j<mergedArr[0].length; j++) {
				mergedArr[i][j] = convArr[i][j] + convArr2[i][j];
			}
		}
		
		
		// return a BufferedImage from the 2D array
		return makeImgFromConvMatrix(buffImg,mergedArr);
		
	}
	
	/**
	 * Transforms BufferedImage to a 3D array.
	 * 
	 * @param buffImg	 BufferedImage object
	 * @return double [][][] representing the image
	 */
	public double [][][] imageToArray(BufferedImage buffImg){
		int h = buffImg.getHeight();
		int w = buffImg.getWidth();
		
		// need to make a 2D array for each red,green,blue
		double[][][] imgArr = new double[3][h][w];
		
		for (int i =0; i < h; i++) {
			for (int j=0; j < w; j++) {
				Color col = new Color(buffImg.getRGB(j,i));
				// get Color object value properties for each color, then store them in array
				imgArr[0][i][j] = col.getRed();
				imgArr[1][i][j] = col.getGreen();
				imgArr[2][i][j] = col.getBlue();
			}
		}
		return imgArr;
	}
	
	/**
	 * Method will apply convolution to a 3D array that represents the BufferedImage
	 * @param imgArr			3D array representing BufferedImage
	 * @param w					Image array width
	 * @param h					Image array height
	 * @param kernel			2D array representing filter/kernel to be applied to image array
	 * @param kH				kernel array height
	 * @param kW				kernel array width
	 * @return					2D array representing convolution-ed matrix
	 */
	private double [][] convOnImgArr(double[][][] imgArr, int w, int h, double[][] kernel,
			int kH, int kW){
		Convolution op = new Convolution();
		
		// we need to apply convolusion to each of the 3 (one for each color) 2D matrices
		double[][] rConv = op.matrixConvolution(imgArr[0], w, h, kernel, kH, kW);
	    double[][] gConv = op.matrixConvolution(imgArr[1], w, h, kernel, kH, kW);
	    double[][] bConv = op.matrixConvolution(imgArr[2], w, h, kernel, kH, kW);
	    
	    double[][] combConv = new double[h][w];
	    
	    for (int i =0; i < h; i++) {
	    	for (int j = 0; j < w; j++) {
	    		// the final matrix is the sum of each convolutioned matrices
	    		combConv[i][j] = rConv[i][j] + gConv[i][j] + bConv[i][j];
	    	}
	    }
	    // blur the image to make it easier to edge detect
	    //combConv = op.matrixConvolution(combConv,w,h,GAUSSIAN_BLUR,kH,kW);
		return combConv;
	}
	
	/**
	 * Converts a matrix into a BufferedImage
	 * @param userInImage				BufferedImage input we want to convert
	 * @param convMatrix				Matrix used to generate BufferedImage
	 * @return 							new BufferedImage object
	 */
	private BufferedImage makeImgFromConvMatrix (BufferedImage userInImage, double[][] convMatrix) {
		BufferedImage res = new BufferedImage(userInImage.getWidth(),userInImage.getHeight(),BufferedImage.TYPE_INT_RGB);
		
		for (int i = 0; i < convMatrix.length; i++) {
			for (int j = 0; j < convMatrix[i].length; j++) {
				// need to make sure all elements of convMatrix are within valid range
				int tempC = fixRGBRange(convMatrix[i][j]);
				// because we merge the three 2D matrices, the RGB values will all be the same for the current pixel
				Color col = new Color(tempC,tempC,tempC);
				res.setRGB(j, i, col.getRGB());
			}
		}
		
		return res;
	}
	
	/**
	 * Checks that the given number is within [0,255]. If the number is not within range, 
	 * it corrects it.
	 * @param col
	 * @return
	 */
	private int fixRGBRange(double col) {
	    
	    if (col < 0.0)
	    	col = (int)-col;
	    if (col > 255.0)
	    	col = 255;
	    
		return (int)col;
	}
	
	public void revealImage() {
		grid = pixels;
	}
}