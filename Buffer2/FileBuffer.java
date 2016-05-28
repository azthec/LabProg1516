package trabalho2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;


/**
 * Incompleto, so para fase 2 do Trabalho Pratico
 * 
 * 
 * @author azthec
 *
 */
public class FileBuffer extends Buffer {
	private Path savePath;
	// null= não definido
	private boolean modified;
	// true= modificado; false= inalterado


	FileBuffer() { //para ter construtor
		super();
	}

	// gravar
	public void save(Path saveLoc) throws IOException {
		savePath = saveLoc;
		save();
	}
	
	public void save() throws IOException { 
		File file = new File(savePath.toString());
		
		if(modified) {
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			PrintWriter writer = null;
	        try {
	            writer = new PrintWriter(savePath.toString(), "UTF-8");
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        
	        for(StringBuilder string : lineList) {
	        	writer.println(string.toString());
	        }
	        writer.close();
			
			modified = false;
		}

	}

	public void saveAs(Path path)
			throws IOException { 
		savePath = path;
		save();
	}

	// abrir
	public void open(Path path)
			throws IOException, LineInputException { 
		savePath = path;
		File file = new File(savePath.toString());

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			String line;
			try (
					InputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
					BufferedReader br = new BufferedReader(isr);
					) {
				while ((line = br.readLine()) != null) {
					line = line.trim();
					insertString(line);
					breakLine();
				}
				br.close(); //loses the stream according to javadoc for BufferedReader and InputStreamReader as well as FileReader
			}
		}
		savePath = file.toPath();
	}

	@Override
	public void insert(char c) throws LineInputException {
		super.insert(c);
		modified = true; // marcar modificação
	}
	// análogo para outros modificadores

	@Override
	public void insertString(String tmp) throws LineInputException {
			//throws InvalidContentException {
		//try {
		super.insertString(tmp);
		//} catch (InvalidContentException e) {
		//  e.printStackTrace();
		//}
		modified=true;
	}

	@Override
	public void breakLine() {
		super.breakLine();
		modified = true;
	}

}
