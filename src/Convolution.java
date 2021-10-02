
public class Convolution {
	
	public Convolution() {
	}
	
	/**
	 * Applies convolution to a single pixel at specified location
	 * @param src		2D array representing grayscale/flattened image
	 * @param x			x coordinate of pixel to operate on
	 * @param y			y coordinate of pixel to operate on
	 * @param kernel	2nd operand, filter used to manipulate image array
	 * @param kH		kernel height
	 * @param kW		kernel width
	 * @return			the new value for the pixel after operation
	 */
	 public double pixelConv(double[][] src, int x, int y, double[][] kernel,
			 	int kH, int kW) {
		 double newVal = 0;
		 for (int i =0; i < kW; i++) {
			 for (int j = 0; j < kH; j++) {
				 // this is formula for convolution. See Repo for discussion on image convolution
				 newVal += (src[y + j][x + i] * kernel[j][i]);
			 }
		 }
		 return newVal;
	 }
	 
	 /**
	  * Applies convolution to entire 2D image Array
	  * @param src		2D array for grayscale/flattened image
	  * @param w		width of image array
	  * @param h		height of image array
	  * @param kernel	2D array for filter on image
	  * @param kH		kernel height
	  * @param kW		kernel width
	  * @return			matrix after convolution and padding 
	  */
	 public double[][] matrixConvolution(double[][] src, int w, int h, double[][] kernel,
			 	int kH, int kW){
		 /* Since we can only apply convolution on values in src such that we can fit the entire kernel, 
		 	 the resultant matrix will have different dimensions. See Repo for more */
		 int innerW = w - kW + 1;			
		 int innerH = h - kH + 1;
		 
		 double[][] inner = new double[innerW][innerH];
		 
		 // apply convolution to each pixel element in image array
		 for (int i=0; i<innerW;i++) {
			 for (int j=0; j<innerH;j++) {
				 inner[j][i] = pixelConv(src,i,j,kernel,kH,kW);
			 }
		 }
		 
		 /* now we need to pad the resultant to match the size of the original image
		    so that we can deal with the numbers at the edges */
		 double[][] paddedInner = new double[w][h];
		 
		 for (int i = 0; i < innerW; i++) {
			 for (int j = 0; j < innerH; j++) {
				 paddedInner[j + kH/2][i + kW/2] = inner[j][i];
			 }
		 }
		 
		 return paddedInner;
	 }
	
	 
}
