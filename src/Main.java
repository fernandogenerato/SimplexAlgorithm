import simplex.Restricao;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;



public class Main {
	private Double[][] tabela;
	private int tabelaLinhaTam;
	private int tabelaColunaTam;
	private int qtdVariaveis;
//............................ CONSTRUTOR DA CLASSE .......................................................
	public Main(int linha, int coluna,ArrayList<Restricao> restricoes, int qtdVariaveis,ArrayList<Double> lucros){
		this.tabela = new Double[linha][coluna];
		this.tabelaLinhaTam = linha;
		this.tabelaColunaTam = coluna;
		this.qtdVariaveis = qtdVariaveis;
		
		InserirValoresIniciaisEmTabela(restricoes,lucros);
		if(verificaNegativos())
			calcula();
		else
			this.listarTabela();
	}
//...................... M�todo que verifica se ainda existe valores negativos na linha de Z ...........................
	private boolean verificaNegativos(){
		int cont =0;
		for(int c = 0; c < this.tabelaColunaTam; c++){
			if(this.tabela[0][c] < 0){
				cont++;
			}
		}
		if(cont > 0)
			return true;
		else
			return false;
	}		
//...................... M�todo que calcula as novas linhas ...........................................................
	private void calcula(){
		int posicaoMenorValor,linhaQueSai;
		posicaoMenorValor = pegaPosicaoMenorValorNegativo();//Pega a posi��o na matriz que tem o maior valor negativo absoluto
		linhaQueSai = pegaLinhaQueSai(posicaoMenorValor);//Pega a linha que sai para gerar novas linhas
		calcularNLP(posicaoMenorValor,linhaQueSai);
		calcularNovasLinhas(posicaoMenorValor,linhaQueSai);
		if(verificaNegativos())
			calcula();
		else
			this.listarTabela();
	}
//...................... Calcula Novas Linhas ...........................................................................
	private void calcularNovasLinhas(int posicaoMenorValor,int linhaQueSai){
		Double[] auxNlp = new Double[this.tabelaColunaTam];
		
		for(int l = 0; l < this.tabelaLinhaTam; l++){
			if(l != linhaQueSai){
				for(int c = 0; c < this.tabelaColunaTam; c++){
					auxNlp[c] = this.tabela[linhaQueSai][c] * (this.tabela[l][posicaoMenorValor] * (-1));
				}
				for(int c = 0; c < this.tabelaColunaTam; c++){
					this.tabela[l][c] = this.tabela[l][c]+ auxNlp[c];
				}
			}
		}
	}
//...................... Calcula NLP(Nova Linha Piv�) ...................................................................
	private void calcularNLP(int posicaoMenorValor, int linhaQueSai){
		System.out.println(this.tabela[linhaQueSai][posicaoMenorValor]);
		Double valor = this.tabela[linhaQueSai][posicaoMenorValor];
		for(int c = 0 ; c< this.tabelaColunaTam; c++){
			this.tabela[linhaQueSai][c] = this.tabela[linhaQueSai][c]/valor; 
		}
	}
//...................... Pegar linha que sai .............................................................................
	private int pegaLinhaQueSai(int linhaDaDivisao){
		int linhaQueSai = 1;
		Double[] resultadosDivisao = new Double[this.tabelaLinhaTam - 1];
		for(int l = 1; l < this.tabelaLinhaTam;l++){
			resultadosDivisao[l-1] = (this.tabela[l][this.tabelaColunaTam-1]/this.tabela[l][linhaDaDivisao]);
		}
		Double valor = resultadosDivisao[0];
		for(int i = 0; i < resultadosDivisao.length; i++){
			if(resultadosDivisao[i] >= 0 && resultadosDivisao[i] < valor){
				valor = resultadosDivisao[i];
				linhaQueSai = i+1;
			}
		}
		return linhaQueSai;
	}
//...................... Mostra o resultado dos calculos .............................................................
	public void mostraResultados(){
		DecimalFormat df = new DecimalFormat("0.###");
		String saida;		
		System.out.println("\n[ Variáveis Básicas ]\n");
		for(int c = 1; c < this.tabelaColunaTam - 1; c++){
			for(int l = 1; l < this.tabelaLinhaTam;l++){
				if(this.tabela[l][c]>0 && this.tabela[l][c] == 1 && this.tabela[l][c] <= 1){
					saida = df.format(this.tabela[l][this.tabelaColunaTam-1]);
					System.out.println("* X"+c+" = "+saida);
				}
			}
		}
		System.out.println("\n[ Variáveis não Básicas(que devem ser igual a zero)]\n");
		for(int c = 1; c < this.tabelaColunaTam - 1; c++){
			for(int l = 1; l < this.tabelaLinhaTam;l++){
				if(this.tabela[l][c]>1 || this.tabela[l][c] < 0){
					System.out.println("* X"+c);
					break;
				}
			}
		}
		System.out.println("\n* [ O valor de Z ] *\n");
		System.out.println("* Z = "+this.tabela[0][this.tabelaColunaTam - 1]);
	}
//...................... Método que retorna menor valor da matriz .....................................................
	private int pegaPosicaoMenorValorNegativo(){
		int posicao = 0;
		Double valor =0.0;
		for(int c=0; c < this.tabelaColunaTam;c++){
			if(this.tabela[0][c] < 0 && this.tabela[0][c] < valor){
				posicao = c;
				valor = this.tabela[0][c];
			}
		}
		return posicao;
	}
//...................... Método que possibilita inserir valores na tabela ..............................................	
	private void InserirValoresIniciaisEmTabela(ArrayList<Restricao> restricoes, ArrayList<Double> lucros){
		//Preenche com zeros a tabela
		for(int l = 0; l < this.tabelaLinhaTam; l++){
			for(int c = 0 ; c < this.tabelaColunaTam; c++){
				this.tabela[l][c]=0.0;
			}
		}
		//Z sempre vai ser igual a 1 no come�o
		this.tabela[0][0]=1.0;
		
		//Coloca os valores de lucro de cada vari�vel
		for(int c = 1; c < this.qtdVariaveis+1;c++){
			this.tabela[0][c] = (lucros.get(c-1))*(-1);
		}
		//Coloca os valores das restrições e seus limites
		for(int l = 1; l < this.tabelaLinhaTam; l++){
			for(int c = 1; c <= this.qtdVariaveis;c++){
				this.tabela[l][c] = restricoes.get(l-1).getLista(c-1);
			}
			this.tabela[l][this.tabelaColunaTam - 1]= restricoes.get(l-1).getLimite();
		}
		//Coloca o valor das variáveis de folga na tabela
		int valor = this.qtdVariaveis+1;
		for(int i = 1; i < this.tabelaLinhaTam; i++){
			this.tabela[i][valor] = 1.0;
			valor++;
		}
	}
//...................... Apenas lista a tabela ............................................................
	public void listarTabela(){
		System.out.println("\n\n* [ Listagem da Tabela ] *\n");
		DecimalFormat df = new DecimalFormat("0.###");
		String saida;
		for(int l = 0; l< this.tabelaLinhaTam;l++){
			for(int c = 0; c < this.tabelaColunaTam;c++){
				saida = df.format(this.tabela[l][c]);
				System.out.print("   "+saida);
			}
			System.out.println();
		}
	}
//....................... MAIN ............................................................................	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		int qtdVariaveis;
		int qtdRestricoes;
		@SuppressWarnings("unused")
		Double valor;
		ArrayList<Restricao> restricoes = new ArrayList<Restricao>();
		ArrayList<Double> lucros = new ArrayList<Double>(); 
		//Pega a quantidade de vari�veis que ser�o utilizadas na tabela
		System.out.println("\n  * PROGRAMA DE MAXIMIZAÇÃO SIMPLEX	*\n");
		System.out.print("Digite a quantidade de variáveis(produtos ou unidades): ");
		qtdVariaveis = s.nextInt();
		//Pega a quantidade de restri��es que existe no problema
		System.out.print("Digite a quantidade de restrições: ");
		qtdRestricoes = s.nextInt();
		//Inserir os lucros das variaveis em uma lista da linha que tem o Z para que possa ser enviado para tabela
		System.out.println("\n* INSERIR LUCRO POR CADA VARIÁVEL *\n");
		for(int i = 0; i< qtdVariaveis; i++){
			System.out.print("Digite o Lucro "+(i+1)+" : ");
			lucros.add(valor = s.nextDouble());
		}
		// Inserir as restri��es em uma lista para que possa passar para um m�todo que preencha na tabela
		System.out.println("\n  * INSERIR AS RESTRI��ES	*\n");
		Restricao r;
		for(int i = 0; i < qtdRestricoes;i++){
			System.out.println("=> Restrição "+(i+1)+" : \n");
			r = new Restricao();//Criei uma classe que serve como estrutura de dados para armazenar as restri��es
			for(int v = 0; v < qtdVariaveis; v++){
				System.out.print("Digite nesta restrição a variavel X"+(v+1)+":");
				r.addVariavel( valor = s.nextDouble());
			}
			System.out.print("Digite o Limite desta restrição: ");
			r.setLimite(valor = s.nextDouble());
			restricoes.add(r);
		}
		Main m = new Main(qtdRestricoes+1, qtdVariaveis + qtdRestricoes + 2,restricoes,qtdVariaveis,lucros);
		m.mostraResultados();
	}
}