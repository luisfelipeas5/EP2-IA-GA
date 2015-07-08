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
	JamaUtils (http://www.seas.upenn.edu/~eeaton/software.html)
	JChart2D (http://www.java2s.com/Code/Java/Chart/JChart2DDynamicChart.htm)

Parâmetros via linha de comando:
	- taxa de crossover
	- taxa de mutação
	- operador de seleção
		- operador=0, Seleção via Roleta
		- operador=1, Seleção via Torneio
	- nome do arquivo de entrada com as cidades
		- cada linha deve conter o: id da cidade, coordenada x e coordenada y, separado por espaços
Parâmetros adicionais:
	Parâmetros Adicionais via código:
		Numero de individuos da populacao inicial: tamanho_populacao_inicial;
		Numero de canditados para crossover: numero_candidatos_crossover;
		Numero de melhores individuos que permaneceram como candidatos (subpopulacao): quantidade_subpopulacao;
		Valor da diversidade em que deve-se para o crossover: diversidade_minima;
		Numero maximo de geracoes: numero_geracao_maximo;
		Tipo de crossover: tipo_crossover:
			- tipo_crossover=0 : crossover OX;
			- tipo_crossover=1 : crossover baseado em posicao;
			- tipo_crossover=2 : crossover baseado em ordem;
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
		Numero de threads adicionais alem da Thread Main para realizar o crossover e a mutacao: numero_threads

Algoritmo com Multi-Thread x sem Multi-Thread:
	Fizemos duas implementacoes do AlgoritmoGenetico.java:
	Sem Multi-Thread: AlgoritmoGenetico.java
	Com Multi-Thread: AlgoritmoGeneticoThread.java
		-No momento do crossover ou da mutacao, esse algoritmo separa a populacao n partes iguais para realizar determinada
			operacao, sendo n o numero de threads que foi passado como parametro.
	Por exemplo:
		Se numero_threads=0,
			A classe Caixeiro ira usar o algoritmo da classe AlgoritmoGenetico para obter o melhor caminho,
			utilizando a propia thread main para realizar o crossover sobre a populacao inteira, assim como a mutacao.
		Se numero_threads=1,
			A classe Caixeiro ira usar o algortimo da classe AlgoritmoGeneticoThreads para obter o melhor caminho,
			utilizando uma outra thread, que nao é a thread main, para realizar o crossover sobre a populacao inteira,
			assim como a mutacao. Enquanto isso a thread main fica aguardando a thread terminar
		Se numero_threads=2,
			A classe Caixeiro ira usar o algortimo da classe AlgoritmoGeneticoThreads para obter o melhor caminho,
			utilizando duas threads a mais. Cada Uma das threads operara sobre (1/n) da populacao, sendo n=2, entao,
			cada thread operara sobre 1/2 da populacao para realizar o crossover e a mutacao.
			Enquanto isso a thread main fica aguardando as threads terminar.	
