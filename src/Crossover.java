import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;


public class Crossover {

	public static Matrix aplica_crossover(Matrix populacao, double taxa_crossover, 
										int tipo_crossover, boolean incluir_pais_nova_populacao) {
		//TODO passar para o crossover uma populacao ordenada pelo fitness
		if(tipo_crossover==0) {
			return crossover_ox(populacao, taxa_crossover, incluir_pais_nova_populacao);
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
	private static Matrix crossover_ox(Matrix populacao, double taxa_crossover, boolean incluir_pais_nova_populacao) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		
		Random random=new Random();
		//TODO acrescentar Threads!
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension()-1; indice_cromossomo+=2) {
			//Pais
			Matrix cromossomo_P1=JamaUtils.getrow(populacao, indice_cromossomo);
			Matrix cromossomo_P2=JamaUtils.getrow(populacao, indice_cromossomo+1);
			//Incluir pais na nova populacao
			//Filhos
			Matrix cromossomo_F1=new Matrix(1,cromossomo_P1.getColumnDimension());
			Matrix cromossomo_F2=new Matrix(1,cromossomo_P2.getColumnDimension());
			
			//Mapa com as cidades ja incluidas em cada uma dos cromossomos 
			Map<Double, Boolean> cidades_incluidas_F1=new HashMap<Double, Boolean>();
			Map<Double, Boolean> cidades_incluidas_F2=new HashMap<Double, Boolean>();
			
			int corte_posicao_inicial= Math.abs(random.nextInt())%cromossomo_P1.getColumnDimension();
			int corte_posicao_final= corte_posicao_inicial+ (int)(cromossomo_P1.getColumnDimension()*taxa_crossover);
			corte_posicao_inicial=corte_posicao_inicial%cromossomo_P1.getColumnDimension();
			corte_posicao_final=corte_posicao_final%cromossomo_P1.getColumnDimension();
			
			//itera sobre as cidades na zona de corte
			for (int i = 0; i < Math.abs(corte_posicao_inicial-corte_posicao_final)+1; i++) {
				int indice_cidade_incluida=corte_posicao_inicial+i;
				indice_cidade_incluida=indice_cidade_incluida%cromossomo_P1.getColumnDimension();
				//armazena as cidades que deve ser herdadas de cada um dos pais
				double cidade_herdada_P1=cromossomo_P1.get(0, indice_cidade_incluida);
				double cidade_herdada_P2=cromossomo_P2.get(0, indice_cidade_incluida);
				//inclui nos filhos as cidades herdadas nos pais
				cromossomo_F1.set(0,indice_cidade_incluida, cidade_herdada_P1);
				cromossomo_F2.set(0,indice_cidade_incluida, cidade_herdada_P2);
				//inclui no mapa quais cidades estao em cada filho
				cidades_incluidas_F1.put(cidade_herdada_P1, true);
				cidades_incluidas_F2.put(cidade_herdada_P2, true);
			}
			if(incluir_pais_nova_populacao) {
				nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P1);
				nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P2);
			}
			//Completar cromossomo filho F1
			int numero_cidades=Math.abs(corte_posicao_final-corte_posicao_inicial)+1;
			int indice_incluir_cidade=(corte_posicao_final+1)%cromossomo_F1.getColumnDimension();
			for(int indice_posicao=corte_posicao_final+1; numero_cidades<cromossomo_F1.getColumnDimension(); indice_posicao++) {
				indice_posicao=(indice_posicao)%cromossomo_P2.getColumnDimension();
				double cidade_herdada_P2= cromossomo_P2.get(0, indice_posicao); 
				
				//TODO vetor de cidades incluidas dinamicamente
				boolean cidade_jah_incluida=false;
				if (cidades_incluidas_F1.containsKey(cidade_herdada_P2)) {
					cidade_jah_incluida=true;
				}
				if (!cidade_jah_incluida) {
					cromossomo_F1.set(0, indice_incluir_cidade, cidade_herdada_P2);
					indice_incluir_cidade++;
					indice_incluir_cidade=indice_incluir_cidade%cromossomo_F1.getColumnDimension();
					numero_cidades++;
				}
				//System.out.println("F1");
				//cromossomo_F1.print(0, 0);
			}
			//Adiciona F1 a nova populacao
			nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F1);
			
			//Completar cromossomo filho F2
			numero_cidades=Math.abs(corte_posicao_final-corte_posicao_inicial)+1;
			indice_incluir_cidade=(corte_posicao_final+1)%cromossomo_F2.getColumnDimension();
			for(int indice_posicao=corte_posicao_final+1; numero_cidades<cromossomo_F2.getColumnDimension(); indice_posicao++) {
				indice_posicao=(indice_posicao)%cromossomo_P1.getColumnDimension();
				double cidade_herdada_P1= cromossomo_P1.get(0, indice_posicao); 
				
				boolean cidade_jah_incluida=false;
				if ( cidades_incluidas_F2.containsKey(cidade_herdada_P1) ) {
					cidade_jah_incluida=true;
				}
				if (!cidade_jah_incluida) {
					cromossomo_F2.set(0, indice_incluir_cidade, cidade_herdada_P1);
					indice_incluir_cidade++;
					indice_incluir_cidade=indice_incluir_cidade%cromossomo_F2.getColumnDimension();
					numero_cidades++;
				}
				//cromossomo_F2.print(0, 0);
			}
			//System.out.println("F2 incluido");
			//cromossomo_F2.print(0, 0);
			//Adiciona F2 a nova populacao
			nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F2);
		}
		return nova_populacao;
	}

}
