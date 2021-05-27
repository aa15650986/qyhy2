package com.qy.game.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 图片压缩、加水印
 * @author lhp
 *
 */
public class ImageCompressUtil {
	
	public static int IS_WATER_CENTER = 1;
	

	/***
	 * 功能：压缩图片变成小尺寸*** 参数1：oImage：原图；* 参数2：maxWidth：小尺寸宽度；*
	 * 参数3：maxHeight：小尺寸长度；* 参数4：newImageName：小尺寸图片存放的路径和新名字；*
	 * 参数5：fileType：小尺寸图片类型（png,gif,jpg...）
	 * @throws IOException 
	 ***/
	public static void compressImage(File oImage, int maxWidth, int maxHeight,
			String newImageName, String fileType) throws IOException {
		
		if(oImage.exists()){
			
			BufferedImage srcImage = ImageIO.read(oImage);
			int srcWidth = srcImage.getWidth();
			int srcHeight = srcImage.getHeight();
			if (srcWidth <= maxWidth && srcHeight <= maxHeight) {
				saveImage(srcImage, fileType, new FileOutputStream(newImageName));
				return;
			}
			Image scaledImage = srcImage.getScaledInstance(srcWidth, srcHeight,
					Image.SCALE_SMOOTH);
			double ratio = Math.min((double) maxWidth / srcWidth,
					(double) maxHeight / srcHeight);
			AffineTransformOp op = new AffineTransformOp(
					AffineTransform.getScaleInstance(ratio, ratio), null);
			scaledImage = op.filter(srcImage, null);
			saveImage((BufferedImage) scaledImage, fileType, new FileOutputStream(newImageName));// 存盘
		}
	}

	/***
	 * 功能：压缩图片变成小尺寸*** 参数1：oImage：原图；* 参数2：maxWidth：小尺寸宽度；*
	 * 参数3：maxHeight：小尺寸长度；* 参数4：newImageName：小尺寸图片存放的路径和新名字；*
	 * 参数5：fileType：小尺寸图片类型（png,gif,jpg...）
	 * @throws IOException 
	 ***/
	public static BufferedImage compressImageRuturnBuffer(File oImage, int maxWidth, int maxHeight,
			 String fileType) throws IOException {
		
		BufferedImage srcImage = ImageIO.read(oImage);
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		if (srcWidth <= maxWidth && srcHeight <= maxHeight) {
			return srcImage;
		}
		Image scaledImage = srcImage.getScaledInstance(srcWidth, srcHeight,
				Image.SCALE_SMOOTH);
		double ratio = Math.max((double) maxWidth / srcWidth,
				(double) maxHeight / srcHeight);
		AffineTransformOp op = new AffineTransformOp(
				AffineTransform.getScaleInstance(ratio, ratio), null);
		scaledImage = op.filter(srcImage, null);
		return (BufferedImage) scaledImage;
	}


	/***
	 * 功能：图片加logo图片水印*** 参数1：oImage：原图；* 参数2：newImageName：加logo图片水印存放的路径和新名字；*
	 * 参数3：fileType：加logo图片水印图片类型（png,gif,jpg...）； 参数4：logoPath：logo水印图片的存放路径
	 ****/
	public static boolean pressImage(File oImage, String newImageName,
			String fileType, String logoPath) throws IOException {
		File waterMarkImage = new File(logoPath);
		if (!waterMarkImage.exists()) {
			return false;
		}
		BufferedImage originalImage = ImageIO.read(oImage);
		BufferedImage watermarkImage = ImageIO.read(waterMarkImage);
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		int watermarkWidth = watermarkImage.getWidth();
		int watermarkHeight = watermarkImage.getHeight();
		
		int finalWidth=originalWidth;//>=watermarkWidth?originalWidth:watermarkWidth;
		int finalHeight=originalHeight;//>=watermarkHeight?originalHeight:watermarkHeight;
		
		BufferedImage newImage = new BufferedImage(finalWidth,
				finalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		
		int x=(finalWidth-originalWidth)/2;
		int y=(finalHeight-originalHeight)/2;
		g.drawImage(originalImage, x, y, originalWidth, originalHeight, null);
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 1f));
		
			
			if(watermarkHeight>finalHeight||watermarkWidth>finalWidth)
			{
				watermarkImage=compressImageRuturnBuffer(waterMarkImage, finalWidth, finalHeight, fileType);
				
			}
			x=(finalWidth-watermarkImage.getWidth())/2;
			y=(finalHeight-watermarkImage.getHeight())/2;
			g.drawImage(watermarkImage, x, y, watermarkImage.getWidth(), watermarkImage.getHeight(), null);
		
		g.dispose();
		saveImage(newImage, fileType, new FileOutputStream(newImageName));// 存盘
		originalImage.flush();
		watermarkImage.flush();
		return true;
	}
	
	
	
	/**
	 * 给图片加上水印并以流的形式输出
	 * @param oImage
	 * @param logoPath
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage pressImage(File oImage, String newImageName, String logoPath) throws IOException {
		File waterMarkImage = new File(logoPath);
		if (!waterMarkImage.exists()) {
			return null;
		}
		BufferedImage originalImage = compressImage(oImage, newImageName, 220, 400);
		BufferedImage watermarkImage = ImageIO.read(waterMarkImage);
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		int watermarkWidth = watermarkImage.getWidth();
		int watermarkHeight = watermarkImage.getHeight();
		
		BufferedImage newImage = new BufferedImage(originalWidth,
				originalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(originalImage, 0, 0, originalWidth, originalHeight, null);
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
		if (IS_WATER_CENTER == 0) {
			g.drawImage(watermarkImage, originalWidth - watermarkWidth - 10,
					originalHeight - watermarkHeight - 10, watermarkWidth,
					watermarkHeight, null);
		} else {
			g.drawImage(watermarkImage, 10, 10, originalWidth-20, originalHeight-20, null);
		}
		g.dispose();
		// 保存图片
		saveImage(newImage, "jpg", new FileOutputStream(newImageName));
		return newImage;
	}
	
	
	/**
	 * 图片压缩
	 * @param oImage
	 * @param maxWidth
	 * @param maxHeight
	 * @return 
	 * @throws IOException
	 */
	public static BufferedImage compressImage(File oImage, String newImageName, int maxWidth, int maxHeight) throws IOException {
		
		BufferedImage srcImage = ImageIO.read(oImage);
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		if (srcWidth <= maxWidth && srcHeight <= maxHeight) {
			// 保存图片
			saveImage(srcImage, "jpg", new FileOutputStream(newImageName));
			return srcImage;
		}
		Image scaledImage = srcImage.getScaledInstance(srcWidth, srcHeight,
				Image.SCALE_SMOOTH);
		double ratio = Math.min((double) maxWidth / srcWidth,
				(double) maxHeight / srcHeight);
		AffineTransformOp op = new AffineTransformOp(
				AffineTransform.getScaleInstance(ratio, ratio), null);
		scaledImage = op.filter(srcImage, null);
		// 保存图片
		saveImage((BufferedImage) scaledImage, "jpg", new FileOutputStream(newImageName));
		return (BufferedImage) scaledImage;
	}
	
	
	/**
	 * 保存图片
	 * @param scaledImage
	 * @param fileType
	 * @param fileOutputStream
	 * @throws IOException
	 */
	private static void saveImage(BufferedImage scaledImage, String fileType,
			FileOutputStream fileOutputStream) throws IOException {
		
		ImageIO.write(scaledImage, fileType, fileOutputStream);
		fileOutputStream.close();
	}
	
	public static void main(String[] args){
		
		File f = new File("D:/1_3.jpg");
		try {
			compressImage(f, 220, 400, "D:/1_3_sy.jpg", "jpg");
			File f1 = new File("D:/1_3_sy.jpg");
			pressImage(f1, "D:/1_3_sy.jpg", "png", "D:/shuiyin_small.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
