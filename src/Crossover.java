import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;


public class Crossover {

	public static Matrix aplica_crossover(Matrix populacao, double taxa_crossover, int tipo_crossover) {
		if(tipo_crossover==0) {
			return crossover_xo(populacao, taxa_crossover);
		}
		return null;
	}
	
	/*	
	 * Gera uma nova populaca a partir do crossover OX (crossover cruzado):
	 * Escolhido dois cromossomos pais P1 e P2, e dois cromossomos filhos F1 e F2.
	 * Escolhe-se p1...pn posicoes [n=(taxa_crossover)%] de P1 para formar uma parte do F1.
	 * A partir da posicao p(n+1) o F1 comeca a receber posicoes de P2 caso nao faca parte do cromossomo.
	 * A mesma coisa acontece com F2.
	 */
	private static Matrix crossover_xo(Matrix populacao, double taxa_crossover) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		
		Random random=new Random();
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension()-1; indice_cromossomo+=2) {
			//Pais
			Matrix cromossomo_P1=JamaUtils.getrow(populacao, indice_cromossomo);
			Matrix cromossomo_P2=JamaUtils.getrow(populacao, indice_cromossomo+1);
			//Filhos
			Matrix cromossomo_F1=new Matrix(1,cromossomo_P1.getColumnDimension());
			Matrix cromossomo_F2=new Matrix(1,cromossomo_P2.getColumnDimension());
			
			int corte_posicao_inicial= Math.abs(random.nextInt())%cromossomo_P1.getColumnDimension();
			int corte_posicao_final=corte_posicao_inicial+ (int)(cromossomo_P1.getColumnDimension()*taxa_crossover);
			
			Matrix cromossomo_herdado_P1=cromossomo_P1.getMatrix(0, 0, corte_posicao_inicial, corte_posicao_inicial-1);
			cromossomo_F1.setMatrix(0, 0, corte_posicao_inicial, corte_posicao_inicial-1, cromossomo_herdado_P1);
			
			Matrix cromossomo_herdado_P2=cromossomo_P2.getMatrix(0, 0, corte_posicao_inicial, corte_posicao_inicial-1);
			cromossomo_F2.setMatrix(0, 0, corte_posicao_inicial, corte_posicao_inicial-1, cromossomo_herdado_P2);
			
			//Completar cromossomos filhos
			int indice_posicao = corte_posicao_final;
			while ( indice_posicao != corte_posicao_inicial) {
				//TODO
				indice_posicao=(indice_posicao+1)%cromossomo_P2.getColumnDimension();
			}
			
		}
		
		return nova_populacao;
	}

}
