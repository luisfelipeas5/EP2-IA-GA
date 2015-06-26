# EP2-IA-GA
Segundo exercício programa da disciplina de Inteligência Artificial
Disciplina ministrada por: Clodoaldo Aparecido de Moraes Lima
Assunto: Algoritmo Genético

Alunos:
  André Mello 7137440
  Antonio Mateus 8516031
  Luís Felipe de Almeida da Silva 8516775
  Marcelo Kazuya Kajiwara 8516903

Pacotes necessários:
  Jama (http://math.nist.gov/javanumerics/jama/#Package)
  JamaUtil (http://www.seas.upenn.edu/~eeaton/software.html)

Parâmetros adicionais:
	Parâmetros Adicionais via código:
		Numero de individuos da populacao inicial: tamanho_populacao_inicial;
		Numero de canditados para crossover: numero_candidatos_crossover;
		Numero de melhores individuos que permaneceram como candidatos (subpopulacao): quantidade_subpopulacao;
		Valor da diversidade em que deve-se para o crossover: diversidade_minima;
		Numero maximo de geracoes: numero_geracao_maximo;
		Tipo de crossover: tipo_crossover:
			- tipo_crossover=0 : crossover OX;
		Controle do crossover se os pais vao ou nao na populacao pos-crossover: incluir_pais_nova_populacao;
			- incluir_pais_nova_populacao=true : pais vao continuar na populacao pos-crossover;
			- incluir_pais_nova_populacao=false : pais nao vao continuar na populacao pos-crossover;
		Tipo da mutacao: tipo_mutacao:
			- tipo_mutacao=0 : mutacao simples
			- tipo_mutacao=1 : mutacao alternativa
			- tipo_mutacao=2 : variacao da mutacao inversivel
		Quantidade de cromossomos que nao sofrerão mutação: quantidade_cromossomos_nao_mutantes;
		Tamanho da populacao aleatoria no final de cada geracao: tamanho_populacao_aleatoria;
		Intervalo de tempo para acrescentar populacao aleatoria: geracao_populacao_aleatoria;

Algoritmo com Multi-Thread x sem Multi-Thread:
	Fizemos duas implementacoes do AlgoritmoGenetico.java:
	Sem Multi-Thread: AlgoritmoGenetico.java
	Com Multi-Thread: AlgoritmoGeneticoThread.java
		- recebe um parâmetro a mais: o número de Threads para executar o crossover e a mutacão: numero_threads
	