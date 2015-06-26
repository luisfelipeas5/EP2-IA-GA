package br.com.ia;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import Jama.Matrix;

public class Leitor_Arquivo_Entrada {

	public static Matrix lee_arquivo(String nome_arquivo) {
		Scanner scanner=null;
		try {
			scanner=new Scanner(new FileReader(nome_arquivo));
		} catch (FileNotFoundException e) {
			System.out.println("Nao foi possivel abrir o arquivo "+nome_arquivo);
			e.printStackTrace();
		}
		
		//Lista de tamanho dinamico para armazenar as coordenadas. Como nao sabemos o tamanho do arquivo
		List< double[]> coordenadas_lista=new LinkedList<double[]>();
		//Enquanto o arquivo nao acabar, ou seja, enquanto houver linhas para serem lidas
		while(scanner.hasNext()) {
			//Linha lida do arquivo sem espacos em branco no comeco e no final(trim())
			String linha=scanner.nextLine().trim();
			String[] linha_dividida = linha.split(" +"); //Linha dividida por espacos(multiplos espacos)
			double coordenada_x=Double.parseDouble(linha_dividida[1]); //Coordenada x na segunda posicao
			double coordenada_y=Double.parseDouble(linha_dividida[2]); //Coordenada y na terceira posicao
			//Adicionando a lista de coordenada as novas coordenadas da linha lida
			coordenadas_lista.add(new double[] {coordenada_x, coordenada_y}); 
		}
		
		Matrix coordenadas=new Matrix(coordenadas_lista.size(), 2);//Matrix que contera as coordenadas lidas
		Iterator<double[]> iterator_coordenadas_lista = coordenadas_lista.iterator();//iterador sobre a lista criada
		//Transferir todas as coordenadas da lista para a matrix
		//Enquanto houver coordenadas na lista, ou seja, enquanto houver posicoes para iterar
		for(int i=0; iterator_coordenadas_lista.hasNext(); i++) {
			double[] coordenada = iterator_coordenadas_lista.next();
			//armazena coordenada na matriz onde estao todas as outras coordenadas
			coordenadas.set(i, 0, coordenada[0]); //coordenada x
			coordenadas.set(i, 1, coordenada[1]); //coordenada y
		}
		
		scanner.close();
		return coordenadas;
	}

}
