package com.liquidpresentaion.cocktailservice.util;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.CocktailBrand;
import com.liquidpresentaion.cocktailservice.model.CocktailGlassStyle;
import com.liquidpresentaion.cocktailservice.model.Presentation;

@Component
public class PptxGenaratorUtil {
	private static final Logger logger = LoggerFactory.getLogger(PptxGenaratorUtil.class);

	public ByteArrayInputStream createPptx(Presentation pr) throws Exception {
		XMLSlideShow ppt = null;
		ByteArrayOutputStream baos = null;
		try {
			// 获取cocktail列表
			List<Cocktail> cocktailList = pr.getCocktails();
			// 初始化ppt对象
			ppt = new XMLSlideShow();
			
			// 首页部分处理 begin
			XSLFSlide slideFirst = ppt.createSlide();
			URL url = null;
			URLConnection conn  = null;
			
			//TODO
			/*Resource tpllogo = new ClassPathResource("\\logo.jpg");
			File logo = tpllogo.getFile();
			InputStream inStreamlogo = new FileInputStream(logo);
			byte[] pictureDatalogo = IOUtils.toByteArray(inStreamlogo);
			XSLFPictureData pictureIndexlogo = ppt.addPicture(pictureDatalogo, PictureType.JPEG);
			XSLFPictureShape piclogo = slideFirst.createPicture(pictureIndexlogo);
			piclogo.setAnchor(new java.awt.Rectangle(60, 40, 120, 120));*/
			
			// 插入logo图片
			byte[] pictureDataLogo = null;
			if(StringUtils.isNotBlank(pr.getAccountLogo())) {
				try {
					url = new URL(pr.getAccountLogo());
					conn = url.openConnection();
					conn.setConnectTimeout(2000);
					InputStream inStreamLogo = conn.getInputStream();
					pictureDataLogo = IOUtils.toByteArray(inStreamLogo);
					XSLFPictureData pictureIndexLogo = ppt.addPicture(pictureDataLogo, PictureType.JPEG);
					XSLFPictureShape picLogo = slideFirst.createPicture(pictureIndexLogo);
					picLogo.setAnchor(new java.awt.Rectangle(60, 40, 120, 80));
				}catch(Exception e) {
					logger.error("Get account logo failed.", e);
				}
			}
//			createTextBox(slideFirst, null, pr.getTitle(), null, 32d, true, 60d, 180d, 300d, 80d);
			SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
			String dateStr = "";
			if(pr.getDate()!= null){
				dateStr = formatter.format(pr.getDate());
			}
			XSLFTextBox textBox = slideFirst.createTextBox();
			textBox.clearText();
			textBox.setAnchor(new Rectangle2D.Double(60, 180, 300, 80));
			XSLFTextRun _richTextTitle = textBox.appendText(pr.getTitle(), false);
			_richTextTitle.setFontSize(32d);
			_richTextTitle.setBold(true);
			XSLFTextRun _richTextDate = textBox.appendText(dateStr, true);
			_richTextDate.setFontSize(14d);
			_richTextDate.setBold(false);
//			createTextBox(slideFirst, null, dateStr, null, 14d, false, 60d, 270d, 300d, 20d);
			createTextBox(slideFirst, new Color(238,233,233), null, null, null, true, 0d, 320d, 360d, 220d);
			createTextBox(slideFirst, null, "Contact", null, 28d, true, 60d, 350d, 300d, 50d);
			createTextBox(slideFirst, null, pr.getContactInfo(), null, 14d, true, 60d, 400d, 300d, 76d);
			
			// 创建首页右半部分的北京色
			createTextBox(slideFirst, new Color(173,216,230), null, null, null, false, 360d, 0d, 360d, 540d);
			// 插入brandedElement图片
			if(StringUtils.isNotBlank(pr.getBrandedElement())) {
				try {
					// test use
					url = new URL(pr.getBrandedElement());
					conn = url.openConnection();
					conn.setConnectTimeout(2000);
					InputStream inStreamBe = conn.getInputStream();
					byte[] pictureDataBe = IOUtils.toByteArray(inStreamBe);
					
					//读图像  
			        inStreamBe = new ByteArrayInputStream(pictureDataBe);
					BufferedImage bi = ImageIO.read(inStreamBe);
					double oWidth = bi.getWidth(); // 原始宽度
					double oHeight = bi.getHeight(); // 原始高度
					int maxWidth = 360; // 最大规定宽度
					double maxHeight = 540d; // 最大固定高度
					Double tempHeight = new Double(oHeight * maxWidth / oWidth); // 计算缩放后的高度
					int coordinatesY = new Double((maxHeight - oHeight * maxWidth/oWidth)/2).intValue(); // 计算图片纵坐标放置位置
					if(tempHeight >= maxHeight) { // 图片超出最大高度按最大高度算，纵坐标置为0
						tempHeight = maxHeight;
						coordinatesY = 0;
					}
					int nHeight = tempHeight.intValue();
					
					XSLFPictureData pictureIndexBe = ppt.addPicture(pictureDataBe, PictureType.PNG);
					XSLFPictureShape picBe = slideFirst.createPicture(pictureIndexBe);
	//				picBe.setAnchor(new java.awt.Rectangle(360, 0, nWidth, 540));
					picBe.setAnchor(new java.awt.Rectangle(360, coordinatesY, maxWidth, nHeight));
				} catch (Exception e) {
					logger.error("Get brand image failed.");
				}
			}
			// 首页部分处理 end

			/*Resource tpl = new ClassPathResource("\\bgPic.png");
			File bgpicFile = tpl.getFile();
			InputStream inStreamBg = new FileInputStream(bgpicFile);*/
			InputStream inStreamBg = Resource.class.getResourceAsStream("/bgPic.png");
			byte[] pictureDataBg = IOUtils.toByteArray(inStreamBg);
			
			//TODO
			/*Resource tpl2 = new ClassPathResource("\\goodsPic.jpg");
			File bgpicFile2 = tpl2.getFile();
			InputStream inStreamBg2 = new FileInputStream(bgpicFile2);
			byte[] pictureDataBg2 = IOUtils.toByteArray(inStreamBg2);*/
			
			int index = 0;
			for (Cocktail cocktail : cocktailList) {
				
				XSLFSlide slideBody = ppt.createSlide();
				// 背景图
				XSLFPictureData pictureIndexBg = ppt.addPicture(pictureDataBg, PictureType.PNG);
				XSLFPictureShape picBg = slideBody.createPicture(pictureIndexBg);
				picBg.setAnchor(new java.awt.Rectangle(40, 20, 300, 360));
				
				// 插入图片
				if(StringUtils.isNotBlank(cocktail.getImage())) {
					try{
						url = new URL(cocktail.getImage());
						conn = url.openConnection();
						conn.setConnectTimeout(2000);
						InputStream inStreamCi = conn.getInputStream();
						byte[] pictureDataCi = IOUtils.toByteArray(inStreamCi);
						
						//读图像  
						inStreamCi = new ByteArrayInputStream(pictureDataCi);
						BufferedImage bi = ImageIO.read(inStreamCi);
						double oWidth = bi.getWidth(); // 原始宽度
						double oHeight = bi.getHeight(); // 原始高度
						double maxWidth = 240; // 最大规定宽度
						double maxHeight = 330; // 最大固定高度
						
						double ratX = oWidth > maxWidth ? maxWidth/oWidth : 1;
						double ratY = oHeight > maxHeight ? maxHeight/oHeight : 1;
						double rat = ratX >= ratY ? ratY : ratX;
						Double tempWidth = new Double(oWidth * rat); // 计算缩放后的宽度
						Double tempHeight = new Double(oHeight * rat); // 计算缩放后的高度
						int coordinatesX = new Double(40 + 140 - tempWidth/2).intValue(); // 计算图片横坐标放置位置
						int coordinatesY = new Double(20 + 170 - tempHeight/2).intValue(); // 计算图片纵坐标放置位置
						int nWidth = tempWidth.intValue();
						int nHeight = tempHeight.intValue();
						
						XSLFPictureData pictureIndexCi = ppt.addPicture(pictureDataCi, PictureType.PNG);
	//					XSLFPictureData pictureIndexCi = ppt.addPicture(pictureDataBg2, PictureType.PNG);
						XSLFPictureShape picCi = slideBody.createPicture(pictureIndexCi);
						picCi.setAnchor(new java.awt.Rectangle(50, 25, 240, 345));
//						picCi.setAnchor(new java.awt.Rectangle(coordinatesX, coordinatesY, nWidth, nHeight));
					}catch(Exception e) {
						logger.error("Get cocktail image failed.", e);
					}
				}
				createTextBox(slideBody, null, "RECIPE COMMENTS:", null, 10d, false, 40d, 390d, 310d, 20d);
				drawLine(ppt, slideBody, 45, 420, 280, 1);
				drawLine(ppt, slideBody, 45, 430, 280, 1);
				createTextBox(slideBody, null, "Taste:    1    2    3    4    5    6    7    8    9    10", null, 10d, false, 40d, 440d, 315d, 10d);
				createTextBox(slideBody, null, "Drink Name Ideas:", null, 10d, false, 40d, 455d, 310d, 20d);
				drawLine(ppt, slideBody, 45, 478, 280, 1);
				
				
				Set<CocktailGlassStyle> glassStyleSet = cocktail.getGlassStypleSet();
				List<String> glasswareList = glassStyleSet.stream().map(cocktailGlassStyle -> cocktailGlassStyle.getGlassStyle().toString()).collect(Collectors.toList());
				String glassware = StringUtils.join(glasswareList, "  ");
				
				Set<CocktailBrand> brandSet = cocktail.getBrandSet();
				List<String> brandList = brandSet.stream().map(cocktailBrand -> quantityString(cocktailBrand.getQuantity()) + " " + cocktailBrand.getUom() + " " + cocktailBrand.getBrandName()).collect(Collectors.toList());
				String ingredients = StringUtils.join(brandList, "\n");
				String garnish = cocktail.getGarnish();
				String mixologistNotes = cocktail.getComments();
				
				createTextBox(slideBody, null, cocktail.getBrandName(), null, 30d, true, 370d, 20d, 340d, 80d);
				createTextBox(slideBody, null, "METHOD", null, 12d, true, 370d, 100d, 350d, 20d);
				createTextBox(slideBody, null, cocktail.getMethod(), null, 10d, false, 370d, 120d, 350d, 30d);
				createTextBox(slideBody, null, "GLASSWARE", null, 12d, true, 370d, 150d, 350d, 20d);
				createTextBox(slideBody, null, glassware, null, 10d, false, 370d, 170d, 350d, 40d);
				createTextBox(slideBody, null, "INGREDIENTS", null, 12d, true, 370d, 210d, 350d, 20d);
				createTextBox(slideBody, null, ingredients, null, 10d, false, 370d, 230d, 350d, 70d);
				
				if(StringUtils.isNotBlank(garnish)) {
					createTextBox(slideBody, null, "GARNISH", null, 12d, true, 370d, 300d, 350d, 20d);
					createTextBox(slideBody, null, garnish, null, 10d, false, 370d, 320d, 350d, 30d);
				}
				if(StringUtils.isNotBlank(mixologistNotes)) {
					createTextBox(slideBody, null, "MIXOLOGIST NOTES", null, 12d, true, 370d, 350d, 350d, 20d);
					createTextBox(slideBody, null, mixologistNotes, null, 10d, false, 370d, 370d, 350d, 30d);
				}
				createTextBox(slideBody, null, "DEGREE OF DIFFICULTY", null, 12d, true, 370d, 400d, 350d, 20d);
				
				int degreeOfDiff = cocktail.getDegreeOfDiff();
				createTextBox(slideBody, null, "1", null, 10d, false, 370d, 430d, 20d, 20d);
				createTextBox(slideBody, null, "2", null, 10d, false, 400d, 430d, 20d, 20d);
				createTextBox(slideBody, null, "3", null, 10d, false, 430d, 430d, 20d, 20d);
				createTextBox(slideBody, null, "4", null, 10d, false, 460d, 430d, 20d, 20d);
				createTextBox(slideBody, null, "5", null, 10d, false, 490d, 430d, 20d, 20d);
//				Resource dediffPic = null;
				InputStream inStreamddp = null;
				java.awt.Rectangle rectangle = null;
				if(degreeOfDiff==1) {
//					dediffPic = new ClassPathResource("\\degreeDiff1.png");
					inStreamddp = Resource.class.getResourceAsStream("/degreeDiff1.png");
					rectangle = new java.awt.Rectangle(370, 430, 20, 20);
				}
				if(degreeOfDiff==2) {
//					dediffPic = new ClassPathResource("\\degreeDiff2.png");
					inStreamddp = Resource.class.getResourceAsStream("/degreeDiff2.png");
					rectangle = new java.awt.Rectangle(400, 430, 20, 20);
				}
				if(degreeOfDiff==3) {
//					dediffPic = new ClassPathResource("\\degreeDiff3.png");
					inStreamddp = Resource.class.getResourceAsStream("/degreeDiff3.png");
					rectangle = new java.awt.Rectangle(430, 430, 20, 20);
				}
				if(degreeOfDiff==4) {
//					dediffPic = new ClassPathResource("\\degreeDiff4.png");
					inStreamddp = Resource.class.getResourceAsStream("/degreeDiff4.png");
					rectangle = new java.awt.Rectangle(460, 430, 20, 20);
				}
				if(degreeOfDiff==5) {
//					dediffPic = new ClassPathResource("\\degreeDiff5.png");
					inStreamddp = Resource.class.getResourceAsStream("/degreeDiff5.png");
					rectangle = new java.awt.Rectangle(490, 430, 20, 2);
				}
				
				if(inStreamddp!=null && rectangle!=null) {
//					File ddp = dediffPic.getFile();
//					InputStream inStreamddp = new FileInputStream(ddp);
					byte[] pictureDataddp = IOUtils.toByteArray(inStreamddp);
					XSLFPictureData pictureIndexddp = ppt.addPicture(pictureDataddp, PictureType.PNG);
					XSLFPictureShape picddp = slideBody.createPicture(pictureIndexddp);
					picddp.setAnchor(rectangle);
				}
				
				
				index ++;
				// 720*540
				createTextBox(slideBody, null, String.valueOf(index), null, 12d, true, 360d, 510d, 250d, 30d);
				if(pictureDataLogo != null) {
					XSLFPictureData pictureIndexlogoFoot = ppt.addPicture(pictureDataLogo, PictureType.PNG);
					XSLFPictureShape piclogoFoot = slideBody.createPicture(pictureIndexlogoFoot);
					piclogoFoot.setAnchor(new java.awt.Rectangle(650, 490, 60, 40));
				}
				/*//TODO begin
				XSLFPictureData pictureIndexlogoFoot = ppt.addPicture(pictureDatalogo, PictureType.JPEG);
				XSLFPictureShape piclogoFoot = slideBody.createPicture(pictureIndexlogoFoot);
				piclogoFoot.setAnchor(new java.awt.Rectangle(670, 490, 50, 40));
				//TODO end
				 */				
			}

//			ppt.write(new FileOutputStream("D:\\MyWorkFolder\\test_Ppt\\ppt1.pptx"));
			baos = new ByteArrayOutputStream();
			baos.flush();
			ppt.write(baos);
			return new ByteArrayInputStream(baos.toByteArray());
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if(ppt != null) {
				try {
					ppt.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
			if(baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
		}
        
	}
	
	private String quantityString(double quantity) {
		//TODO operate
		
		DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
		return decimalFormat.format(quantity);
	}
	
	
	private void createTextBox(XSLFSlide slide, Color bgColor, String text, String typeface, Double fontSize, Boolean isBold, double x, double y, double width, double height) {
		XSLFTextBox textBox = slide.createTextBox();
		textBox.clearText();
		textBox.setAnchor(new Rectangle2D.Double(x, y, width, height));
		if(bgColor != null) {
			textBox.setFillColor(bgColor);
		}
		XSLFTextRun textRun = textBox.addNewTextParagraph().addNewTextRun();
		if(StringUtils.isEmpty(text)) {
			text = "";
		}
		textRun.setText(text);
		textRun.setFontFamily("Avenir"); // Avenir
		if(StringUtils.isNotEmpty(typeface)) {
			textRun.setFontFamily(typeface);
		}
		if(fontSize != null) {
			textRun.setFontSize(fontSize);
		}
		if(isBold != null) {
			textRun.setBold(isBold);
		}
	}
	
	private void drawLine(XMLSlideShow ppt, XSLFSlide slide, int x, int y, int width, int height) throws IOException {
		/*Resource tpl = new ClassPathResource("\\line.png");
		File pic = tpl.getFile();
		InputStream inStreamPic = new FileInputStream(pic);*/
		InputStream inStreamPic = Resource.class.getResourceAsStream("/line.png");
		byte[] pictureDataPic = IOUtils.toByteArray(inStreamPic);
		XSLFPictureData pictureIndexPic = ppt.addPicture(pictureDataPic, PictureType.PNG);
		XSLFPictureShape picShape = slide.createPicture(pictureIndexPic);
		picShape.setAnchor(new java.awt.Rectangle(x, y, width, height));
		picShape.setFillColor(Color.black);
	}
	
}