package com.besideYou.textant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.besideYou.textant.Dto.FileDto;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/main.text", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/write.text", method = RequestMethod.GET)
	public String writeForm() {
		return "writeForm";
	}
	
	@RequestMapping(value = "/write.text", method = RequestMethod.POST)
	public String write(String title, String content, @RequestPart("textFile") List<MultipartFile> mFile) {
		for(MultipartFile file : mFile) {
			System.out.println(file.getOriginalFilename());
			File files = new File("d:/temp/temp/"+file.getOriginalFilename());
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
			PdfImage pdfImage = new PdfImage(file.getOriginalFilename());
			
			Thread thread = new Thread(pdfImage);
			
			thread.start();
		}
		
		return "home";
	}
	@RequestMapping(value="/read.text")
	public String read() throws Exception{
		return "content";
	}
	
	
	@RequestMapping(value="/displayFile.text")
	public ResponseEntity<byte[]> displayFile(String fileName) throws IOException {
		
		File file = new File("d:/temp/Converted_PdfFiles_to_Image/FirstPdf.pdf/FirstPdf_"+fileName+".jpg");
		byte[] data = null;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		ResponseEntity<byte[]> entity = null;

			try {
				
				HttpHeaders headers = new HttpHeaders();
				System.out.println(file);

				headers.add("Content-Disposition",
						"attachment; filename=\"" + URLEncoder.encode(file.getName(), "utf-8").replace("+", "%20") + "\"");

				entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(bis), headers, HttpStatus.CREATED);
			} catch (Exception e) {
				e.printStackTrace();
				entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
			} finally {
				bis.close();
			}
			return entity;
			
	}
	
	
}
