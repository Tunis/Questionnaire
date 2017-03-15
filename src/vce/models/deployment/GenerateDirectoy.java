package vce.models.deployment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
//import org.apache.commons.io.IOUtils;

public class GenerateDirectoy implements Runnable {
	
	//Construct
	//----------------------------------
	public GenerateDirectoy(){
		createPath("res/images/");
		createPath("pdf");
		copyFileToPath("res/");
	}
	
	//Création d'un dossiers (avec les sous-dossier) nécessaire à l'application
	//----------------------------------
	private void createPath(String path){
		File dir = new File (path);
		dir.setWritable(true);
		dir.mkdirs();
	}
	
	//Extrait les fichiers utiles de l'archive
	//----------------------------------
	private void copyFileToPath(String path){
		//Récupère le répertoire du jar
		String rootDirectory = GenerateDirectoy.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		try{
			//On récupère les entrées du jar pour les lister
			JarFile jarFile = new JarFile(rootDirectory);
			Enumeration<JarEntry> entries = jarFile.entries();
			
			while(entries.hasMoreElements()){
				JarEntry entry = entries.nextElement();

				//Si l'entrée correspond au dossier images/ alors on "extrait"
				if(entry.getName().startsWith("images/") && entry.getName().length() > 7){
					Files.copy(jarFile.getInputStream(entry), Paths.get(path + entry.getName()));
				}
			}
			
			jarFile.close();
			
		} catch(IOException e) {
			//TODO : Affichage de l'exception dans l'IHM ?
			System.err.println("" + e.getMessage());
		}
		
//		InputStream is = GenerateDirectoy.class.getResourceAsStream("/images/logo.png");
//		OutputStream os = new FileOutputStream(path);
//		IOUtils.copy(is, os);
	}

	@Override
	public void run() {
	}
}
