package com.liquidpresentaion.cocktailservice.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.lowagie.text.DocumentException;

@Component
public class PdfGenaratorUtil {
	@Autowired
	private TemplateEngine templateEngine;

	public ByteArrayInputStream createPdf(String templateName, Presentation p) throws IOException, DocumentException {
		Assert.notNull(templateName, "The templateName can not be null");
		Context ctx = new Context();
		/*if (map != null) {
			Iterator itMap = map.entrySet().iterator();
			while (itMap.hasNext()) {
				Map.Entry pair = (Map.Entry) itMap.next();
				ctx.setVariable(pair.getKey().toString(), pair.getValue());
			}
		}*/
		ctx.setVariable("accountLogo", p.getAccountLogo());
		ctx.setVariable("title", p.getTitle());
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
		String dateStr = "";
		if(p.getDate()!= null){
			dateStr = formatter.format(p.getDate());
		}
		ctx.setVariable("date", dateStr);
		String[] contactInfo = {};
		if(StringUtils.isNotBlank(p.getContactInfo())) {
			contactInfo = p.getContactInfo().split("\n");
		}
		ctx.setVariable("contactInfo", contactInfo);
		ctx.setVariable("brandedElement", p.getBrandedElement());
		ctx.setVariable("cocktails", p.getCocktails());
		String processedHtml = templateEngine.process(templateName, ctx);
		FileOutputStream os = null;
		ByteArrayOutputStream out = null;
		String fileName = UUID.randomUUID().toString();
		try {
			final File outputFile = File.createTempFile(fileName, ".pdf");
//			final File outputFile = new File("D:\\MyWorkFolder\\test_Ppt\\pdf11.pdf");
			os = new FileOutputStream(outputFile);
			out = new ByteArrayOutputStream();

			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(processedHtml);
			renderer.layout();
			renderer.createPDF(out, false);
//			renderer.createPDF(os);
			renderer.finishPDF();
			System.out.println("PDF created successfully");

			return new ByteArrayInputStream(out.toByteArray());
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					/* ignore */ }
			}
		}
	}
}