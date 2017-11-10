package com.besideYou.textant;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;


@SuppressWarnings("unchecked")
public class PdfImage implements Runnable {
	String oldFileName /*= "gameOfThrone.pdf"*/;
	
	public PdfImage(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	
	
	public void main(String[] args) {
		PdfImage myrun = new PdfImage(oldFileName);
		
		Thread t = new Thread(myrun); // 생성한 myrun 객체를 인수로 쓰레드 생성
		t.start();
		/*try {
			TextExtractor.getInstance().text();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		PDDocument document=null;
		try {
			String sourceDir = "D:/temp/"; // Pdf files are read from this folder
			String destinationDir = "D:/temp/Converted_PdfFiles_to_Image/"; // converted images from pdf document are
																			// saved here
			
			Long startTime = System.currentTimeMillis();
			System.out.println(startTime);

			File sourceFile = new File(sourceDir + oldFileName);
			File destinationFile = new File(destinationDir + oldFileName);

			if (!destinationFile.exists()) {
				destinationFile.mkdir();
				System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
			}
			
			if (sourceFile.exists()) {
				
				System.out.println("Images copied to Folder: " + destinationFile.getName());
				
				try {
					document = PDDocument.load(sourceFile);
//				PdfImageCounter counter = new PdfImageCounter();
//				counter.countImagesWithProcessor(document);
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					int pageCounter = 0;
					String fileName = sourceFile.getName().replace(".pdf", "");
					System.out.println("Total files to be converted -> " + document.getPages().getCount());
					
					int pageNumber = 1;
					for (PDPage page : document.getPages())	{
						// 기존의 방식
/*				    // note that the page number parameter is zero based
					    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
					    File outputfile = new File(
								destinationDir + oldFileName + "/" + fileName + "_" + pageNumber + ".png");
						
					    // suffix in filename will be used as the file format
					    ImageIO.write(bim, "png", outputfile);
*/
						//새로운 방식 <훨씬 빠른듯>
						
						BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 144, ImageType.RGB);
						
//					byte[] imageByte = ((DataBufferByte)bim.getData().getDataBuffer()).getData();
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write( bim, "jpg", baos );
						baos.flush();
						byte[] imageInByte = baos.toByteArray();
						baos.close();
//					DataBufferInt imageByte = (DataBufferInt)bim.getData().getDataBuffer();
//					int[] imageInt = imageByte.getData();
					    FileOutputStream fos3 = new FileOutputStream(new File(
								destinationDir + oldFileName + "/" + fileName + "_" + pageNumber + ".jpg"));
						BufferedOutputStream bos3 = new BufferedOutputStream(fos3);
//					ImageIO.write(bim, "jpg", bos3);
//					bos3.write(imageByte);
						bos3.write(imageInByte);
						bos3.close();
						fos3.close();
						
						
						
						PDFTextStripper reader = new PDFTextStripper();
						reader.setStartPage(pageNumber);
						reader.setEndPage(pageNumber);
						String pageText = reader.getText(document);
						new File(destinationDir + oldFileName + "/" + pageNumber).mkdir();
						/*FileOutputStream fos = new FileOutputStream(
								new File(destinationDir + oldFileName + "/" + pageNumber + "/"+fileName + ".txt"));*/
//					BufferedOutputStream bos = new BufferedOutputStream(fw);
						FileWriter fw = new FileWriter(new File(destinationDir + oldFileName + "/" + pageNumber + "/"+fileName + ".txt"));
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(pageText);
						System.out.println("TextCreated" + destinationDir + pageNumber + "/" + fileName + ".txt");
						bw.close();
						fw.close();
						
						
						PDResources pdResources = page.getResources();
						int imageCount = 1;
					    for (COSName cosName : pdResources.getXObjectNames()) {
					        PDXObject xobject = pdResources.getXObject(cosName);
					        if (xobject instanceof PDImageXObject) {
					        	//기존의 방식
					        	/*
					        	BufferedImage bi = ((PDImageXObject) xobject).getImage();
					        	String imageDest =destinationDir + oldFileName + "/" + pageNumber + "/" + fileName
										+ "_" + imageCount + ".png";
					        	System.out.println("Image created as " + imageDest);
					        	File file = new File(imageDest);
					        	ImageIO.write(bi, "png", file);
					        	*/
					        	
					        	//새로운 방식 <훨씬 빠른듯>
								
								
//							byte[] imageByte = ((DataBufferByte)bim.getData().getDataBuffer()).getData();
					        	BufferedImage bi = ((PDImageXObject) xobject).getImage();
								ByteArrayOutputStream baossa = new ByteArrayOutputStream();
								ImageIO.write( bi, "jpg", baossa );
								baossa.flush();
								byte[] imageInByteSa = baossa.toByteArray();
								baossa.close();
//							DataBufferInt imageByte = (DataBufferInt)bim.getData().getDataBuffer();
//							int[] imageInt = imageByte.getData();
								String imageDest4 =destinationDir + oldFileName + "/" + pageNumber + "/" + fileName
										+ "_" + imageCount + ".png";
							    FileOutputStream fos4 = new FileOutputStream(new File(imageDest4));
								BufferedOutputStream bos4 = new BufferedOutputStream(fos4);
//							bos3.write(imageByte);
								bos4.write(imageInByteSa);
								bos4.close();
								fos4.close();
					        	
					        	
					        	
					        	
					        	
					        	
					        	
					        	
					            imageCount++;
					            if (imageCount % 100 == 0) {
					                System.out.println("Found image: #" + imageCount);
					            }
					        }
					    }
					    
					   
					    
						/*PDResources pdResources = page.getResources();

						int totalImages = 1;
						Map pageImages = pdResources.getImages();
						if (pageImages != null) {

							Iterator imageIter = pageImages.keySet().iterator();
							while (imageIter.hasNext()) {
								String key = (String) imageIter.next();
								PDImageXObject pdxObjectImage = (PDImageXObject) pageImages.get(key);
								pdxObjectImage.(destinationDir + oldFileName + "/" + pageNumber + "/" + fileName
										+ "_" + totalImages);
								totalImages++;
							}
						}*/
					    System.out.println("pageNumber : " + pageNumber);
						pageNumber++;
						pageCounter++;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Thread.sleep(10);
					System.out.println("파일에 문제가 있습니다");
				}
				document.close();
				
				System.out.println("걸린시간 : " + ((double)(System.currentTimeMillis() - startTime)/1000)+"초");
				System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());
			} else {
				System.err.println(sourceFile.getName() + " File not exists");
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		}

		
	}
	
	
	
	
}