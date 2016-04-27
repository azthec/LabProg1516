package test2;

import java.io.*;
import java.util.LinkedList;

class Flight{
	String country1;
	String country2;
	String horario_entrada;
	String horario_chegada;
	String code_flight;
	LinkedList<String> dias;

	Flight(){

	}


	Flight(String c1, String c2, String h_e, String h_c,String c_f, LinkedList<String> dias){
		country1 = c1;
		country2 = c2;
		h_e = horario_entrada;
		h_c = horario_chegada;
		c_f = code_flight;
		dias = new LinkedList<String>();
	}


	public LinkedList<String> getDates(LinkedList<String> dias, String dia){
		while(!dias.isEmpty()) {
			if (dia.equals("alldays"))
				dias.add("alldays");
			if(dia.equals("mon"))
				dias.add("mon");
			if(dia.equals("tu"))
				dias.add("tu");
			if(dia.equals("we"))
				dias.add("we");
			if(dia.equals("th"))
				dias.add("th");
			if(dia.equals("fr"))
				dias.add("fr");
			if(dia.equals("sa"))
				dias.add("sa");
			if(dia.equals("su"))
				dias.add("su");
		}
		return dias;
	}

	public void getDataBase(LinkedList<Flights> voos) throws IOException {

		PrintWriter writer = null;
		File file = new File("flight.txt");
		if(!file.exists()) {
			try {
				PrintWriter writercreate = new PrintWriter("flight.txt", "UTF-8");
				writercreate.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		while (line != null)                 // read the score file line by line
		{
			try {
				//actual code goes here
				
				/*
				 * agora aqui tens de por cada linha do ficheiro inserir um objecto novo na linked list
				 * dividir a linhaa em tokens usando a virgula como tokenizer (nao sei como vais fazer isso pra varios dias que isso tem de ser outra lista)
				 * e colocar o que les em cada um dos atributos do novo objecto
				 * 
				 * repetir
				 */
				
				
			} catch (NumberFormatException e1) {

			}
			line = reader.readLine();
		}
		reader.close();




	}
}

public class Flights{
	static LinkedList<Flights> voos = new LinkedList<Flights>();
	public static void readInput(){

		getDataBase(voos);
	}
	public static void main(String[] args){
		readInput();
	}
}
