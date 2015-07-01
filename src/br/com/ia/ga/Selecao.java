package br.com.ia.ga;
import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

/*
 * Utiliza um dos metodos de selecao para escolher
 * dentre a populacao passada como parametro quais sao os individuos
 * que sao candidatos a participar da nova populacao de acordo.
 */
public class Selecao {

	public static Matrix seleciona_candidatos(Matrix populacao,Matrix fitness, int operador_selecao,
											int quantidade_nova_populacao) {
		Matrix populacao_nova=new Matrix(0, populacao.getColumnDimension());
		
		if(operador_selecao==0) {
			//System.out.print("\t\t\tSelecionando individuos via Roleta...");
			Matrix populacao_roleta = roleta(populacao, fitness,quantidade_nova_populacao);
			populacao_nova=JamaUtils.rowAppend(populacao_nova, populacao_roleta);
			//System.out.println("selecionados!");
		}else if(operador_selecao==1) {
			Matrix populacao_torneio= torneio(populacao, fitness, quantidade_nova_populacao);
			populacao_nova=JamaUtils.rowAppend(populacao_nova, populacao_torneio);
		}
		return populacao_nova;
	}
	
	/*
	 * Retorna uma selecao da populacao passada como parametro de acordo com os criterios do torneio:
	 * - Escolher dois cromossomos aleatoriamente
	 * - Gerar um numero aleatorio r entre 0 e 1
	 * - Se r for menor que k o melhor dos individuos eh escolhido
	 * 		- k eh passado como parametro < TODO
	 */
	private static Matrix torneio(Matrix populacao, Matrix fitness,int quantidade_nova_populacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		double k=0.6;
		
		Random random=new Random();
		for (int numero_cromossomos_selecionados = 0; numero_cromossomos_selecionados < quantidade_nova_populacao; numero_cromossomos_selecionados++) {
			int indice_cromossomo_X=random.nextInt(populacao.getRowDimension());
			int indice_cromossomo_Y=random.nextInt(populacao.getRowDimension());
			
			Matrix cromossomo_X= JamaUtils.getrow(populacao, indice_cromossomo_X);
			double fitness_X = fitness.get(indice_cromossomo_X, 0);
			Matrix cromossomo_Y= JamaUtils.getrow(populacao, indice_cromossomo_Y);
			double fitness_Y = fitness.get(indice_cromossomo_Y, 0);
			
			double r=random.nextDouble();
			
			if(r < k) {
				if( fitness_X>fitness_Y ) {
					nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_X);
				}else {
					nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_Y);
				}
			}else {
				if( fitness_X>fitness_Y ) {
					nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_Y);
				}else {
					nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_X);
				}
			}
		}
		
		return nova_populacao;
	}

	/*Normaliza os valores do fitness de cada um dos cromossomos
	 * para montar a roleta, assim cada cromossomo tem uma chance proporcional
	 * de ser escolhido para a nova populacao
	 */
	private static Matrix roleta(Matrix populacao, Matrix fitness, int quantidade_roleta_gira) {
		Matrix populacao_nova=new Matrix(0, populacao.getColumnDimension());
		
		//Matriz de indices dos cromossomos! Indices vao ser ordenados conforme o fitness
		Matrix indices_cromossomos=new Matrix(populacao.getRowDimension(), 1);
		for (int indice = 0; indice < indices_cromossomos.getRowDimension(); indice++) {
			indices_cromossomos.set(indice, 0, indice);
		}
		
		//Ordena fitness juntamente com os indices correspondentes
		Matrix fitness_indices_ordenados=Fitness.ordena_fitness(fitness, indices_cromossomos);
		indices_cromossomos=JamaUtils.getcol(fitness_indices_ordenados, 1);
		
		//Calcula fitness acumulados
		Matrix fitness_ordenado=JamaUtils.getcol(fitness_indices_ordenados, 0);
		//Matriz de fitness acumulados a cada fitness de cidade acrescentada
		Matrix fitness_acumulado=Fitness.calcula_fitness_acumulado(fitness_ordenado);
		
		//Maior fitness acumulados, ou seja, o ultimo elemento da matriz de fitness acumulados
		double fitness_acumulado_maximo=fitness_acumulado.get(fitness_acumulado.getRowDimension()-1, 0);
		
		//Rodar roleta n vezes, n eh definido por parametro da funcao
		Random random=new Random();
		for (int indice_cromossomo = 0; indice_cromossomo < quantidade_roleta_gira; indice_cromossomo++) {
			double roleta_aleatorio=random.nextDouble();//aleatorio de 0 a 1
			roleta_aleatorio=(fitness_acumulado_maximo)*roleta_aleatorio;//intervalado de 0 a maximo fitness acumulado
			int indice_cromossomo_escolhido=0;
			for (int indice_fitness = 0; indice_fitness < fitness_acumulado.getRowDimension(); indice_fitness++) {
				double fitness_iterado=fitness_acumulado.get(indice_fitness, 0);
				if( fitness_iterado>roleta_aleatorio ) {
					indice_cromossomo_escolhido=(int)( indices_cromossomos.get(indice_fitness, 0) );
					Matrix cromosso_escolhido=JamaUtils.getrow(populacao, indice_cromossomo_escolhido);
					populacao_nova=JamaUtils.rowAppend(populacao_nova, cromosso_escolhido);
					break;
				}
			}
		}
		return populacao_nova;
	}
	
	public static Matrix seleciona_melhores_individuos(Matrix populacao,Matrix fitness, int quantidade_subpopulacao) {
		Matrix melhores_individuos=new Matrix(0, populacao.getColumnDimension());
		
		//Matriz de indices dos cromossomos! Indices vao ser ordenados conforme o fitness
		Matrix indices_cromossomos=new Matrix(populacao.getRowDimension(), 1);
		for (int indice = 0; indice < indices_cromossomos.getRowDimension(); indice++) {
			indices_cromossomos.set(indice, 0, indice);
		}
		
		//Ordena fitness juntamente com os indices correspondentes
		Matrix fitness_indices_ordenados=Fitness.ordena_fitness(fitness, indices_cromossomos);
		//Matriz de indice de cromossomo ordenada conforme o fitness
		indices_cromossomos=JamaUtils.getcol(fitness_indices_ordenados, 1);
		
		for (int indice_cromossomo = 0; indice_cromossomo < quantidade_subpopulacao; indice_cromossomo++) {
			int indice_cromossomo_escolhido = (int)indices_cromossomos.get(indice_cromossomo, 0);
			Matrix cromossomo_escolhido = JamaUtils.getrow(populacao, indice_cromossomo_escolhido);
			melhores_individuos = JamaUtils.rowAppend(melhores_individuos, cromossomo_escolhido);
		}
		
		return melhores_individuos;
	}

}
