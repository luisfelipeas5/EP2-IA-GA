import java.util.Arrays;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;


public class Fitness {
	
	public static Matrix fitness(Matrix populacao, Matrix distancias_entre_cidades) {
		Matrix fitness = new Matrix(populacao.getRowDimension(), 1);
		//calcular o fitness de cada um dos cromossomos da populacao
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			//Cromossomo do calculo do fitness
			Matrix cromossomo=JamaUtils.getrow(populacao, indice_cromossomo);
			double distancia_total=0;
			//Somar as distancias entre as cidades
			for (int indice_cidade_cromossomo = 0; indice_cidade_cromossomo < cromossomo.getColumnDimension(); indice_cidade_cromossomo++) {
				int indice_cidade=(((int)cromossomo.get(0, indice_cidade_cromossomo))-1);
				int indice_cidade_proxima=(((int)cromossomo.get(0, (indice_cidade_cromossomo+1)%cromossomo.getColumnDimension() ))-1);
				
				double distancia_da_proxima_cidade= distancias_entre_cidades.get(indice_cidade, indice_cidade_proxima);
				distancia_total+=distancia_da_proxima_cidade;
			}
			
			//Adicionar fitness desse cromossomo na matriz de fitness
			/*
			 * Problema de maximizacao de fitness e minimazacao da distancia,
			 * portanto: quanto maior distancia percorrida, menor o fitness
			 */
			double fitness_cromossomo= 1/distancia_total;
			
			fitness.set(indice_cromossomo, 0, fitness_cromossomo);
		}
		return fitness;
	}
	
	public static Matrix ordena_fitness(Matrix fitness, Matrix indices) {
		//TODO Otimizar fazendo um ordenador proprio
		
		double[][] fitness_array=fitness.transpose().getArray();
		Arrays.sort(fitness_array[0]);
		
		Matrix fitness_ordenado=null;
		fitness_ordenado=new Matrix(fitness_array);
		fitness_ordenado = fitness_ordenado.transpose();
		
		Matrix fitness_ordenado_decrescente=new Matrix(fitness_ordenado.getRowDimension(),1);
		int i=0;
		for (int indice_fitness = fitness_ordenado.getRowDimension()-1; indice_fitness>=0 ; indice_fitness--) {
			fitness_ordenado_decrescente.set(i, 0, fitness_ordenado.get(indice_fitness, 0));
			i++;
		}
		
		Matrix novos_indices=new Matrix(indices.getRowDimension(), indices.getColumnDimension());
		for (int indice_cromosso = 0; indice_cromosso < indices.getRowDimension(); indice_cromosso++) {
			double fit_original=fitness.get(indice_cromosso, 0);
			for (int indice_fitness_ordenado = 0; indice_fitness_ordenado < fitness_ordenado_decrescente.getRowDimension(); indice_fitness_ordenado++) {
				double fit_ordenado=fitness_ordenado_decrescente.get(indice_fitness_ordenado, 0);
				if( fit_ordenado==fit_original ) {
					novos_indices.set(indice_fitness_ordenado, 0, indice_cromosso);
					break;
				}
			}
		}
		
		Matrix fitness_indices_ordenados=new Matrix(fitness.getRowDimension(), 0);
		fitness_indices_ordenados=JamaUtils.columnAppend(fitness_indices_ordenados, fitness_ordenado_decrescente);
		fitness_indices_ordenados=JamaUtils.columnAppend(fitness_indices_ordenados, novos_indices);
		return fitness_indices_ordenados;
	}

	public static Matrix calcula_fitness_acumulado(Matrix fitness) {
		Matrix fitness_acumulado=new Matrix(fitness.getRowDimension(), 1);
		
		double fitness_soma=0;
		for (int indice_fitness = 0; indice_fitness < fitness.getRowDimension(); indice_fitness++) {
			fitness_soma+=fitness.get(indice_fitness, 0);
			fitness_acumulado.set(indice_fitness, 0, fitness_soma);
		}
		
		return fitness_acumulado;
	}
}
