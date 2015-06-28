package br.com.ia.ga;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.umbc.cs.maple.utils.JamaUtils;
import Jama.Matrix;

public class Crossover {

	public static Matrix aplica_crossover(Matrix populacao, double taxa_crossover, 
										int tipo_crossover, boolean pais_sobrevivem) {
		if(tipo_crossover==0) {
			return crossover_ox(populacao, taxa_crossover, pais_sobrevivem);
		}else if(tipo_crossover==1) {
			return crossover_posicao(populacao, taxa_crossover, pais_sobrevivem);
		}else if(tipo_crossover==2) {
			return crossover_ordem(populacao, taxa_crossover, pais_sobrevivem);
		}
		
		return null;
	}
	
	/*
	 * Gera uma nova populacao a partir do crossover baseado em ordem:
	 * -Iterar sobre todos os cromossomos, dois a dois:
	 * 		- escolhe dois cromossomos em sequencia da ordem que esta no cromossomo, pai P1 e pai P2
	 * 		- seleciona n cidades do pai P1, n=(taxa_crossover)*(tamanho do cromossomo)
	 * 		- filho F1 recebe as n cidades, mas na ordem que está no pai P2, nas mesmas posicoes que antes estavam no P1
	 * 		- itera sobre os genes de P1 e sobre as posicoes vazias de F1:
	 * 			- se o gene de F1 esta vazio,
	 * 				- colocar gene de P1 em F1
	 * 		- fazer a mesma coisa para F2, trocando os pais
	 * 		- inserir na populacao nova
	 * 
	 */
	private static Matrix crossover_ordem(Matrix populacao,double taxa_crossover,boolean pais_sobrevivem) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		for (int indice_cromossomo = 0; indice_cromossomo < populacao.getRowDimension()-1; indice_cromossomo+=2) {
			Matrix cromossomo_P1=JamaUtils.getrow(populacao, indice_cromossomo);
			Matrix cromossomo_P2=JamaUtils.getrow(populacao, indice_cromossomo+1);
			
			Matrix cromossomo_F1=new Matrix(1, populacao.getColumnDimension());
			Matrix cromossomo_F2=new Matrix(1, populacao.getColumnDimension());
			
			Map<Double, Boolean> cidade_incluidas_F1=new HashMap<Double, Boolean>();
			Map<Double, Boolean> cidade_incluidas_F2=new HashMap<Double, Boolean>();
			List<Integer> posicoes=new LinkedList<Integer>();
			
			int numero_cidades_incluidas=(int)(cromossomo_P1.getColumnDimension()*taxa_crossover);
			Random random = new Random();
			
			//Selecionar os genes de P1 e P2
			for (int gene_selecionado = 0; gene_selecionado < numero_cidades_incluidas; gene_selecionado++) {
				int indice_gene=random.nextInt(cromossomo_P1.getColumnDimension());
				double gene_P1 = cromossomo_P1.get(0, indice_gene);
				double gene_P2 = cromossomo_P2.get(0, indice_gene);
				
				while( cidade_incluidas_F1.get(gene_P1)!=null ) {
					indice_gene++;
					indice_gene=indice_gene%cromossomo_F1.getColumnDimension();
					gene_P1=cromossomo_P1.get(0, indice_gene);
					gene_P2=cromossomo_P2.get(0, indice_gene);
				}
				cidade_incluidas_F1.put(gene_P1, true);
				cidade_incluidas_F2.put(gene_P2, true);
				posicoes.add(indice_gene);
			}
			
			
			Collections.sort(posicoes);
			Iterator<Integer> iterator_posicoes_F1=posicoes.iterator();
			//Detectar a ordem e incluir os genes nos filho F1
			for (int indice_gene = 0; indice_gene < cromossomo_P1.getColumnDimension(); indice_gene++) {
				double gene_P1=cromossomo_P1.get(0, indice_gene);
				double gene_P2=cromossomo_P2.get(0, indice_gene);
				
				if(cidade_incluidas_F1.get(gene_P2)!=null) {
					int indice_posicao_F1=iterator_posicoes_F1.next();
					cromossomo_F1.set(0, indice_posicao_F1, gene_P2);
				}
				if(cromossomo_F1.get(0, indice_gene )==0) {
					cromossomo_F1.set(0,indice_gene, gene_P1);
				}
			}
			
			Iterator<Integer> iterator_posicoes_F2=posicoes.iterator();
			//Detectar a ordem e incluir os genes nos filho F2
			for (int indice_gene = 0; indice_gene < cromossomo_P2.getColumnDimension(); indice_gene++) {
				double gene_P1=cromossomo_P1.get(0, indice_gene);
				double gene_P2=cromossomo_P2.get(0, indice_gene);
				
				if(cidade_incluidas_F2.get(gene_P1)!=null) {
					int indice_posicao_F2=iterator_posicoes_F2.next();
					cromossomo_F2.set(0, indice_posicao_F2, gene_P1);
				}
				if(cromossomo_F2.get(0, indice_gene )==0) {
					cromossomo_F2.set(0,indice_gene, gene_P2);
				}
			}
			if(pais_sobrevivem) {
				nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P1);
				nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P2);
			}
			nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F1);
			nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F2);
			
		}
		return nova_populacao;
	}

	/*
	 * Gera uma nova populacao a partit do crossover baseado em posicao:
	 * - Itera sobre todos os cromossomos, dois a dois:
	 * 		- escolhe dois cromossomos em sequencia da ordem que esta no cromossomo, pai P1 e pai P2
	 * 		- seleciona randomicamente n indices de posicao, n=(taxa_crossover)*(tamanho do cromossomo)
	 * 		- filho F1 recebe nesses indices escolhidos, os genes de P2
	 * 		- itera sobre os genes de P1 e sobre as posicoes vazias de F1, ate que nao ha posicoes vazias:
	 * 			- se o gene nao esta em F1,
	 * 				- colocar em F1
	 * 				- avancar posicao vazia de F1
	 * 			- proximo gene do P1
	 * 		- fazer a mesma coisa para F2, trocando os pais
	 * 		- inserir na populacao nova
	 */
	private static Matrix crossover_posicao(Matrix populacao,	double taxa_crossover, boolean pais_sobrevivem) {
		Matrix nova_populacao=new Matrix(0, populacao.getColumnDimension());
		Random random=new Random();
		for (int indice_cromossomo_pai = 0; indice_cromossomo_pai < populacao.getRowDimension()-1; indice_cromossomo_pai+=2) {
			Matrix cromossomo_P1=JamaUtils.getrow(populacao, indice_cromossomo_pai);
			Matrix cromossomo_P2=JamaUtils.getrow(populacao, indice_cromossomo_pai+1);
			
			Matrix cromossomo_F1=new Matrix(1,cromossomo_P1.getColumnDimension());
			Matrix cromossomo_F2=new Matrix(1,cromossomo_P2.getColumnDimension());
			
			Map<Double, Boolean> cidades_incluidas_F1=new HashMap<Double, Boolean>();
			Map<Double, Boolean> cidades_incluidas_F2=new HashMap<Double, Boolean>();
			
			int numero_posicoes_selecionadas=(int)(cromossomo_P1.getColumnDimension()*taxa_crossover);
			for (int numero_genes_incluidos = 0; numero_genes_incluidos < numero_posicoes_selecionadas; numero_genes_incluidos++) {
				int indice_posicao = random.nextInt(cromossomo_P1.getColumnDimension());
				while(cromossomo_F1.get(0, indice_posicao)!=0) {
					indice_posicao++;
					indice_posicao=indice_posicao%cromossomo_F1.getColumnDimension();
				}
				double gene_P1=cromossomo_P1.get(0, indice_posicao);
				double gene_P2=cromossomo_P2.get(0, indice_posicao);
				cromossomo_F1.set(0, indice_posicao, gene_P2);
				cromossomo_F2.set(0, indice_posicao, gene_P1);
				
				cidades_incluidas_F1.put(gene_P2, true);
				cidades_incluidas_F2.put(gene_P1, true);
			}
			
			int numero_posicoes_preenchidas=numero_posicoes_selecionadas;
			int indice_gene_vazio=0;
			for (int indice_posicao_P1 = 0; numero_posicoes_preenchidas!=cromossomo_F1.getColumnDimension(); indice_posicao_P1++) {
				double gene = cromossomo_P1.get(0, indice_posicao_P1);
				if(cidades_incluidas_F1.get(gene)==null) {
					while( cromossomo_F1.get(0, indice_gene_vazio)!=0 ) {
						indice_gene_vazio+=1;
					}
					cromossomo_F1.set(0, indice_gene_vazio, gene);
					indice_gene_vazio++;
					numero_posicoes_preenchidas++;
				}
			}
			
			numero_posicoes_preenchidas=numero_posicoes_selecionadas;
			indice_gene_vazio=0;
			for (int indice_posicao_P2 = 0; numero_posicoes_preenchidas!=cromossomo_F2.getColumnDimension(); indice_posicao_P2++) {
				double gene = cromossomo_P2.get(0, indice_posicao_P2);
				if(cidades_incluidas_F2.get(gene)==null) {
					while( cromossomo_F2.get(0, indice_gene_vazio)!=0 ) {
						indice_gene_vazio+=1;
					}
					cromossomo_F2.set(0, indice_gene_vazio, gene);
					indice_gene_vazio++;
					numero_posicoes_preenchidas++;
				}
			}
			 if(pais_sobrevivem) {
				 nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P1);
				 nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_P2);
			 }
			 nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F1);
			 nova_populacao=JamaUtils.rowAppend(nova_populacao, cromossomo_F2);
		}
		return nova_populacao;
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
			
			int corte_posicao_inicial= (int)(Math.abs(random.nextInt())%cromossomo_P1.getColumnDimension());
			int corte_posicao_final= corte_posicao_inicial+ (int)(cromossomo_P1.getColumnDimension()*taxa_crossover);
			corte_posicao_inicial=corte_posicao_inicial%cromossomo_P1.getColumnDimension();
			corte_posicao_final=(int)(corte_posicao_final%cromossomo_P1.getColumnDimension());
			
			//itera sobre as cidades na zona de corte
			for (int i = 0; i < (int)(cromossomo_P1.getColumnDimension()*taxa_crossover)+1; i++) {
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
			int numero_cidades=(int)(cromossomo_P2.getColumnDimension()*taxa_crossover+1);
			int indice_incluir_cidade=(corte_posicao_final+1)%cromossomo_F1.getColumnDimension();
			for(int indice_posicao=corte_posicao_final+1; numero_cidades<cromossomo_F1.getColumnDimension(); indice_posicao++) {
				indice_posicao=(indice_posicao)%cromossomo_P2.getColumnDimension();
				double cidade_herdada_P2= cromossomo_P2.get(0, indice_posicao); 
				
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
			numero_cidades=(int)(cromossomo_P1.getColumnDimension()*taxa_crossover+1);
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
