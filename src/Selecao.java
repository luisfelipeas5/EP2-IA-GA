import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

/*
 * Utiliza um dos metodos de selecao para escolher
 * dentre a populacao passada como parametro quais sao os individuos
 * que sao candidatos a participar da nova populacao de acordo.
 */
public class Selecao {

	public static Matrix seleciona_candidatos(Matrix populacao,Matrix fitness, int operador_selecao) {
		if(operador_selecao==0) {
			return roleta(populacao, fitness);
		}
		return null;
	}
	
	/*Normaliza os valores do fitness de cada um dos cromossomos
	 * para montar a roleta, assim cada cromossomo tem uma chance proporcional
	 * de ser escolhido para a nova populacao
	 */
	private static Matrix roleta(Matrix populacao, Matrix fitness) {
		Matrix populacao_nova=new Matrix(0, populacao.getColumnDimension());
		double fitness_maximo=JamaUtils.getMax(fitness);
		double fitness_minimo=JamaUtils.getMin(fitness);
		Random random=new Random();
		
		//Rodar roleta para cada cromossomo da populacao
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension(); indice_cromossomo++) {
			double roleta_aleatorio=random.nextDouble();
			double roleta_cromossomo= ( fitness.get(indice_cromossomo, 0) - fitness_minimo )/ (fitness_maximo-fitness_minimo);
			
			if( roleta_aleatorio < roleta_cromossomo ) {
				Matrix cromossomo = JamaUtils.getrow(populacao, indice_cromossomo);
				populacao_nova = JamaUtils.rowAppend(populacao_nova, cromossomo);
			}
		}
		return populacao_nova;
	}

}
