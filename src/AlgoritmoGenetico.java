import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

public class AlgoritmoGenetico {
	public static void main(String[] args) {
		/*
		 *PARÂMETROS:
		 *	- Taxa de crossover
		 *	- Taxa de mutação
		 *	- Operador de seleção
		 */
		double taxa_crossover=Double.parseDouble(args[0]);
		double taxa_mutacao=Double.parseDouble(args[1]);
		int operador_selecao=Integer.parseInt(args[2]);
		int tamanho_populacao_inicial=10;
		double diversidade_minima=0.001;
		int numero_geracao_maximo=1000;
		String nome_arquivo=args[3];
		
		System.out.println("Parametros iniciais:");
		System.out.println("\tTaxa de crossover="+taxa_crossover);
		System.out.println("\tTaxa de mutacao="+taxa_mutacao);
		System.out.println("\tOperador de selecao="+operador_selecao);
		System.out.println("\tTamanho da Populacao inicial="+tamanho_populacao_inicial);
		System.out.println("\tDiversidade minima="+diversidade_minima);
		System.out.println("\tNumero maximo de geracoes="+numero_geracao_maximo);
		System.out.println();
		//matriz de cada uma das cidades que irao compor o cromossomo
		Matrix cidades=Leitor_Arquivo_Entrada.lee_arquivo(nome_arquivo);
		
		//Cada linha da matriz representa um cromossomo; cada elemento uma cidade
		Matrix populacao=gera_populacao_inicial(cidades.getRowDimension(), tamanho_populacao_inicial);
		//System.out.println("Populacao inicial:");
		//populacao.print(10, 0);
		
		//Funcao de fitness calculada para saber a diversidade inicial
		Matrix fitness_inicial=fitness(populacao, cidades);
		//System.out.println("Fitness inicial:");
		//fitness_inicial.print(fitness_inicial.getRowDimension(), 4);
		
		//calcula-se a diversidade da populacao inicial
		double diversidade=avalia_diversidade(fitness_inicial);
		System.out.println("Diversidade inicial="+diversidade);
		for(int geracao_atual=0; 
				geracao_atual<numero_geracao_maximo && diversidade<diversidade_minima;
				geracao_atual++) {
			Matrix fitness_populacao=fitness(populacao, cidades);
			Matrix nova_populacao=selecao(populacao,fitness_populacao);
			aplica_crossover(nova_populacao);
			aplica_mutacao(nova_populacao);
			diversidade=avalia_diversidade(nova_populacao);
		}
		
	}
	
	/*
	 * Diversidade calculada a partir da distancia media entre
	 * os valores passados em uma matriz de fitness
	 */
	private static double avalia_diversidade(Matrix fitness) {
		double diversidade=0;
		
		//Melhor fitness
		double melhor_fitness=JamaUtils.getMax(fitness);
		//Comparar os fitness e calcular a distancia com o maior fitness
		for (int indice_fitness = 0; indice_fitness < fitness.getRowDimension(); indice_fitness++) {
			diversidade+= (melhor_fitness- fitness.get(indice_fitness, 0));
		}
		diversidade=diversidade/fitness.getRowDimension();
		return diversidade;
	}

	private static Matrix fitness(Matrix populacao, Matrix cidades) {
		Matrix fitness = new Matrix(populacao.getRowDimension(), 1);
		//calcular o fitness de cada um dos cromossomos da populacao
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			//Cromossomo do calculo do fitness
			Matrix cromossomo=populacao.getMatrix(indice_cromossomo, indice_cromossomo, 0, populacao.getColumnDimension()-1);
			
			double distancia_total=0;
			//Somar as distancias entre as cidades
			for (int indice_cidade_cromossomo = 0; indice_cidade_cromossomo < cromossomo.getColumnDimension()-1; indice_cidade_cromossomo++) {
				int indice_cidade=(int)cromossomo.get(0, indice_cidade_cromossomo);
				int indice_cidade_proxima=(int)cromossomo.get(0, indice_cidade_cromossomo+1);
				
				Matrix cidade_atual=cidades.getMatrix(indice_cidade, indice_cidade, 0, cidades.getColumnDimension()-1);
				Matrix cidade_proxima=cidades.getMatrix(indice_cidade_proxima, indice_cidade_proxima, 0, cidades.getColumnDimension()-1);
				
				double distancia_da_proxima_cidade= distancia_euclidiana(cidade_atual, cidade_proxima);
				distancia_total+=distancia_da_proxima_cidade;
			}
			//Somar distancia da ultima cidade para a primeira
			int indice_cidade_ultima=(int)cromossomo.get(0, cromossomo.getColumnDimension()-1);
			int indice_cidade_primeira=(int)cromossomo.get(0, 0);
			
			Matrix cidade_ultima=cidades.getMatrix(indice_cidade_ultima, indice_cidade_ultima, 0, cidades.getColumnDimension()-1);
			Matrix cidade_primeira=cidades.getMatrix(indice_cidade_primeira, indice_cidade_primeira, 0, cidades.getColumnDimension()-1);
			
			double distancia_da_primeira_para_ultima= distancia_euclidiana(cidade_ultima, cidade_primeira);
			distancia_total+=distancia_da_primeira_para_ultima;
			
			//Adicionar fitness desse cromossomo na matriz de fitness
			//Problema de maximizacao, portanto: quanto maior distancia percorrida, menor o fitness
			double fitness_cromossomo= 100000/distancia_total;
			fitness.set(indice_cromossomo, 0, fitness_cromossomo);
		}
		
		return fitness;
	}
	
	/*
	 * As matrizes que representam a cidade devem ser matrizes linhas com o mesmo numero de colunas.
	 * O calculo da distancia euclidiana entre duas cidades eh feito:
	 * Dada as cidades P=(p1,p2,...pn) e Q=(q1,q2,...qn),
	 * d= ( (p1-q1)^2 + (p2-q2)^2 +...+ (pn-qn)^2 )^(1/(n)), sendo
	 * n=numero de dimensoes
	 */
	private static double distancia_euclidiana(Matrix cidade_atual,	Matrix cidade_proxima) {
		double distancia_euclidiana=0;
		int numero_dimensoes=cidade_atual.getColumnDimension();
		for (int dimensao = 0; dimensao < numero_dimensoes; dimensao++) {
			double p=cidade_atual.get(0, dimensao);
			double q=cidade_proxima.get(0, dimensao);
			
			distancia_euclidiana+= (p-q)*(p-q);
		}
		distancia_euclidiana=Math.pow(distancia_euclidiana, 1.0/numero_dimensoes );
		
		return distancia_euclidiana;
	}

	private static Matrix gera_populacao_inicial(int quant_cidades, int tamanho_populacao_inicial) {
		Matrix populacao=new Matrix( tamanho_populacao_inicial, quant_cidades);
		
		Random random=new Random();
		for(int indice_cromossomo=0; indice_cromossomo<populacao.getRowDimension(); indice_cromossomo++) {
			//Se a cidade i for incluida no cromossomo, cidade_incluida_cromossomo[i]=1, se não igual a 0;
			int[] cidade_incluida_cromossomo=new int[quant_cidades];
			int quantidade_cidades_incluidas=0;
			
			for(int quant_cidades_incluidas=0; quant_cidades_incluidas<quant_cidades; quant_cidades_incluidas++) {
				int cidade=Math.abs(random.nextInt())%quant_cidades;//escolhe randomicamente uma cidade para incluir
				/*
				 * se essa cidade ja for incluida,
				 * busca sequencial para achar a proxima cidade que nao foi escolhida ainda
				 */
				while(cidade_incluida_cromossomo[cidade]==1 ) {
					cidade++;
				}
				populacao.set(indice_cromossomo, quantidade_cidades_incluidas, cidade);
				quantidade_cidades_incluidas++;
			}
		}
		return populacao;
	}
}
