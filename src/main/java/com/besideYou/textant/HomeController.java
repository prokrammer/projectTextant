package com.besideYou.textant;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Autowired
	PdfService pdfService;

	
	PDFRenderer pdfRenderer = null;

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/main.text", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws IOException {
		/*if (document != null) {
			document.close();
		}*/
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		File file = new File("D:\\temp\\Converted_PdfFiles_to_Image\\");
		File[] files = file.listFiles();
		ArrayList<String> fileNames = new ArrayList<String>();
		for (File lis : files) {
			System.out.println("파일이름 : " + lis.getName());
			fileNames.add(lis.getName());
		}
		model.addAttribute("fileList", fileNames);

		return "home";
	}

	@RequestMapping(value = "/write.text", method = RequestMethod.GET)
	public String writeForm() {
		return "writeForm";
	}

	@RequestMapping(value = "/write.text", method = RequestMethod.POST)
	public String write(String title, String content, @RequestPart("textFile") List<MultipartFile> mFile, Model model) {
		for (MultipartFile file : mFile) {

			System.out.println(file.getOriginalFilename());
			File files = new File("d:/temp/temp/" + file.getOriginalFilename());

			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(files));
				bos.flush();
				bos.write(file.getBytes());
				bos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdfService.setOldFileName(files.getName());
			pdfService.setModel(model);
			Thread thread = new Thread(pdfService);
			thread.start();
			// pdfService.run();
		}

		return "progress";
	}

	@RequestMapping(value = "/getProgress.text", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public HashMap<String, String> getProgress(Model model) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pdfService.getProgress(model);
	}

	@RequestMapping(value = "/read.text")
	public String read(String fileName, Model model) throws Exception {
		PDDocument document = null;
		int totalPageNum = 0;
		System.out.println(fileName);
		try {
			// File file = new
			// File("d:\\temp\\Converted_PdfFiles_to_Image\\"+fileName+"\\"+fileName);
			File file = new File("d:\\temp\\temp\\" + fileName);
			document = PDDocument.load(file);
			
			totalPageNum = document.getNumberOfPages();
			/*
			 * File[] filefile = file.listFiles(); for(File lfile : filefile) { String
			 * imgFile = lfile.getName(); System.out.println(imgFile);
			 * if((imgFile.lastIndexOf("."))!=-1) {
			 * if((imgFile.substring(imgFile.lastIndexOf("."))).equals(".jpg")) {
			 * totalPageNum++; System.out.println("이미지 파일! " + totalPageNum); } }
			 * 
			 * }
			 */
			// if(document != null) {
			// document.close();
			// }
		} catch (Exception e) {
			System.out.println("파일에 문제가 있군요");
			e.printStackTrace();
		}
		model.addAttribute("fileName", fileName);
		model.addAttribute("totalPageNum", totalPageNum);
		document.close();
		return "content";
	}

	@RequestMapping(value = "/displayFile.text")
	public ResponseEntity<byte[]> displayFile(String fileName, String pageNum) throws IOException {

		 String sourceDir = "D:/temp/temp/";
		// PDDocument document = null;
		ByteArrayOutputStream baos = null;

		BufferedImage bim = null;
		BufferedInputStream bis = null;
		ResponseEntity<byte[]> entity = null;
		PDDocument document = null;
//		 String realName = fileName.substring(0,fileName.lastIndexOf("."));
//		 File file = new
//		 File("d:/temp/Converted_PdfFiles_to_Image/"+fileName+"/"+realName+"_"+pageNum+".jpg");
		long currTime;
		try {
			currTime = System.currentTimeMillis();
			 document = PDDocument.load(new File(sourceDir+fileName));
			 System.out.println("읽는시간 : "+(System.currentTimeMillis()-currTime));
			 currTime = System.currentTimeMillis();
			try {
				pdfRenderer = new PDFRenderer(document);
				System.out.println("렌더링시간 : "+(System.currentTimeMillis()-currTime));
				currTime = System.currentTimeMillis();
//				bim = pdfRenderer.renderImageWithDPI(Integer.parseInt(pageNum) - 1, 144, ImageType.RGB);
				bim = pdfRenderer.renderImage(Integer.parseInt(pageNum) - 1, 2);
				System.out.println("이미지읽는시간 : "+(System.currentTimeMillis()-currTime));
				if(bim==null) {
					bim = pdfRenderer.renderImageWithDPI(Integer.parseInt(pageNum) - 1, 300, ImageType.RGB);
				}
				currTime = System.currentTimeMillis();
			} catch (IndexOutOfBoundsException e) {
				System.out.println("마지막 페이지 입니다");
				// e1.printStackTrace();
				bis = new BufferedInputStream(new FileInputStream("d:/temp/temp/TEXTANT.png"));
				entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(bis), HttpStatus.BAD_REQUEST);
				bis.close();
				if(document!=null) {
					document.close();
				}
				return entity;
			}

			baos = new ByteArrayOutputStream();
			ImageIO.write(bim, "jpg", baos);
			baos.flush();
			System.out.println("ImageIO.write시간 : "+(System.currentTimeMillis()-currTime));
			 currTime = System.currentTimeMillis();
			// document.close();
			// bis = new BufferedInputStream(new FileInputStream(file));
			
		} catch (FileNotFoundException e1) {
			System.out.println("마지막 페이지 입니다");
			bis = new BufferedInputStream(new FileInputStream("d:/temp/temp/TEXTANT.png"));
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(bis), HttpStatus.BAD_REQUEST);
			bis.close();
			if(document!=null) {
				document.close();
			}
			return entity;
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			// System.out.println(file);
			headers.add("Content-Disposition",
					"attachment; filename=\"" + URLEncoder.encode("", "utf-8").replace("+", "%20") + "\"");
			entity = new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
			System.out.println("보내는시간 : "+(System.currentTimeMillis()-currTime));
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			// if(document!=null) {
			// document.close();
			// }
			if (bis != null) {
				bis.close();
			}
			if(baos!=null) {
				baos.close();
			}
			if(document!=null) {
				document.close();
			}
		}
		return entity;
	}

}
