package vce.models.data;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

public class ExportToPDF {
	private PDDocument document = null;
	private PDPage page = new PDPage(PDRectangle.A4);
	private PDFont font = PDType1Font.HELVETICA;
	private float fontSize = 12.0f;
	private PDPageContentStream contentStream = null;
	private float pageH = page.getMediaBox().getHeight();
	private float pageW = page.getMediaBox().getWidth();
	
	private String imgPath = null;
	
	public ExportToPDF(){
		imgPath = "res/images/fond-certificat.jpg";
	}
	
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public void createJustificatif(String qName, String uName, String uPrenom){
		PDPageContentStream contents = null;
		File pdfDocument = null;
		DateFormat dateTimeFormatter = SimpleDateFormat.getInstance();
		String text = null;
		float msgWidth = 0.0f;
		
		//On initialise un nouveau PDDocuments
		document = new PDDocument();
		
		try{
			pdfDocument = new File("pdf/Justificatif - " + qName + " - " + uName + " " + uPrenom + ".pdf");
			contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
			contents.beginText();
			contents.setFont(font, fontSize);
			
			addLogo(false);
			
			//On ajoute les différents éléments du justificatif
			//---------------------------------------------------------
			//Titre
			text = "Attestation de présence";
			msgWidth = font.getStringWidth(text);
			contents.newLineAtOffset((pageW - ((msgWidth*fontSize)/1000f))/2f, pageH - (10*pageH/100));
			contents.showText(text);
			
			//Retour à 0
			contents.newLineAtOffset(-((pageW - ((msgWidth*fontSize)/1000f))/2f), -(pageH - (10*pageH/100)));
			
			//Corps
			StringBuilder sb = new StringBuilder();
			sb.append("Le présent document atteste que : ");
			msgWidth = font.getStringWidth(sb.toString());
			contents.newLineAtOffset(10*pageW/100, pageH - (25*pageH/100));
			contents.showText(sb.toString());
			sb =  new StringBuilder();
			sb.append("Mr ")
					.append(uName)
					.append(" ")
					.append(uPrenom);
			msgWidth = font.getStringWidth(sb.toString());
			contents.newLineAtOffset(0, -fontSize*2);
			contents.showText(sb.toString());
			sb =  new StringBuilder();
			sb.append("à bien réalisé le test de ")
					.append(qName)
					.append(" en date du ")
					.append(dateTimeFormatter.format(Date.from(Instant.now())));
			msgWidth = font.getStringWidth(sb.toString());
			contents.newLineAtOffset(0, -fontSize*2);
			contents.showText(sb.toString());
			
			//Retour à 0
			contents.newLineAtOffset(-(10*pageW/100), 0);
			
			//Signature
			text = "Afip formations";
			msgWidth = font.getStringWidth(text);
			contents.newLineAtOffset(pageW - ((msgWidth*fontSize)/1000f + 50), -50);
			contents.showText(text);
			contents.endText();

			document.addPage(page);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				contents.close();
				document.save(pdfDocument);
				try {
					document.close();
					document = null;
				} catch (IOException e) {
					System.err.println("Impossible de fermer le document : " + e.getMessage());
				}
			} catch (IOException e) {
				System.err.println("Impossible de fermer le flux de contenu : " + e.getMessage());
			}
		}
	}
	
	public void createCertificate(String qName, String uName, String uPrenom, int score, int nbQuestion) {
		PDPageContentStream contents = null;
		File pdfDocument = null;
		
		//On initialise un nouveau PDDocuments
		document = new PDDocument();
		
		try{
			pdfDocument = new File("pdf/Certificat - " + qName + " - " + uName + " " + uPrenom + ".pdf");
			
			landscapeMode();
			addBackgroundImage();
			addLogo(true);

			contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
			contents.beginText();
			contents.setFont(font, fontSize);
			contents.newLineAtOffset(190, 150);
			DateFormat dateTimeFormatter = SimpleDateFormat.getInstance();
			contents.showText(dateTimeFormatter.format(Date.from(Instant.now())));
			contents.newLineAtOffset(390, 0);
			contents.showText("Afip formations");
			contents.newLineAtOffset(-340, 120);
			StringBuilder sb = new StringBuilder();
			sb.append("Mr ")
					.append(uName)
					.append(" ")
					.append(uPrenom)
					.append(" a passer le test ")
					.append(qName)
					.append(" avec la note de ")
					.append(score)
					.append("/")
					.append(nbQuestion)
					.append(".");
			contents.showText(sb.toString());
			contents.endText();

			document.addPage(page);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//On ferme le flux de contenu
			try {
				contents.close();
				document.save(pdfDocument);
				try {
					document.close();
					document = null;
				} catch (IOException e) {
					System.err.println("Impossible de fermer le document : " + e.getMessage());
				}
			} catch (IOException e) {
				System.err.println("Impossible de fermer le flux de contenu : " + e.getMessage());
			}
		}
	}
	
	public void closePDF(){
		try {
			document.close();
			document = null;
		} catch (IOException e) {
			System.err.println("Impossible de fermer le document : " + e.getMessage());
		}
	}
	
	private void landscapeMode() throws IOException{
		contentStream = null;
		
		PDRectangle pageSize = page.getMediaBox();
		float pageWidth = pageSize.getWidth();

		//Tourne Ã  90Â°
		page.setRotation(90);

		//Applique la rotation via une transformation par Matrice
		contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
		contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
		
		try {
			contentStream.close();
		} catch (IOException e) {
			System.err.println("Impossible de fermer le flux de contenu : " + e.getMessage());
		}
	}
	
	private void addBackgroundImage() throws IOException{
		contentStream = null;
		
		//RÃ©cupÃ¨re l'image
		PDImageXObject pdImage = PDImageXObject.createFromFile(imgPath, document);
		contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

		// reduce this value if the image is too large
		float scale = 0.61f;

		//Ajout l'image au pdf
		contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

		try {
			contentStream.close();
		} catch (IOException e) {
			System.err.println("Impossible de fermer le flux de contenu : " + e.getMessage());
		}
	}
	
	private void addLogo(boolean isLandscape) throws IOException{
		contentStream = null;
		
		//RÃ©cupÃ¨re l'image
		setImgPath("res/images/logo.png");
		
		PDImageXObject pdImage = PDImageXObject.createFromFile(imgPath, document);
		contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

		// reduce this value if the image is too large
		float scale = 0.5f;
		
		//Ajout l'image au pdf
		if(isLandscape){
			contentStream.drawImage(pdImage, 47, pageW-((pdImage.getHeight() * scale) + 42), pdImage.getWidth() * scale, pdImage.getHeight() * scale);
		} else {
			contentStream.drawImage(pdImage, 10, (pageH - pdImage.getHeight() * scale)-10, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
		}
		
		try {
			contentStream.close();
		} catch (IOException e) {
			System.err.println("Impossible de fermer le flux de contenu : " + e.getMessage());
		}
	}
}
