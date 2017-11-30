import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.*;


public class FoodLogger {

	public static void readBarcode() {
		try {
			File imageFile = new File("G:\\Recall_app\\Sample Images\\barcode_01.jpg");
			BufferedImage image;
			image = ImageIO.read(imageFile);
			
			LuminanceSource source = new BufferedImageLuminanceSource(image);
		    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		    MultiFormatReader barcodeReader = new MultiFormatReader();
		    Result result;
		    String finalResult;
		    
	         result = barcodeReader.decode(bitmap);
	         finalResult = String.valueOf(result.getText());
	         System.out.println(finalResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void productLookup() {
		
	}
	
	public static void main(String[] args) {
		readBarcode();
	}
}
