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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Completo!
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
	
	FileBuffer(String tmp) {
		super(tmp);
	}

	/**
	 * Set file save path for buffer
	 * @param p
	 */
	public void setSavePath(Path p)
	{
		this.savePath = p;
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

	// abrir
	public void open(Path path) throws IOException, LineInputException {
		savePath=path;
		BufferedReader reader = null;
		String current_line = null;

		reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

		while((current_line = reader.readLine()) != null)
		{
			this.insertString(current_line);
			this.breakLine();
		}
		reader.close();
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
