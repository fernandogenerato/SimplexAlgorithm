package simplex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class simplex extends javax.swing.JFrame {

    public simplex() {
        initComponents();
    }
    ArrayList al = new ArrayList();
    static double resultado = 0;

    private Double[][] tabela;
    private int tabelaLinhaTam;
    private int tabelaColunaTam;
    private int qtdVariaveis;
    public String[] nomeRestricao = {
        "Primeira Restrição: ",
        "Segunda Restrição: ", "Terceira Restrição: ",
        "Quarta Restrição: ", "quinta Restrição: ",
        "Sexta Restrição: "};
//............................ CONSTRUTOR DA CLASSE .......................................................

    public simplex(int linha, int coluna, ArrayList<Restricao> restricoes, int qtdVariaveis, ArrayList<Double> lucros) {
        this.tabela = new Double[linha][coluna];
        this.tabelaLinhaTam = linha;
        this.tabelaColunaTam = coluna;
        this.qtdVariaveis = qtdVariaveis;

        InserirValoresIniciaisEmTabela(restricoes, lucros);
        if (verificaNegativos()) {
            calcula();
        } else {
            this.listarTabela();
        }
    }
//...................... M�todo que verifica se ainda existe valores negativos na linha de Z ...........................

    private boolean verificaNegativos() {
        int cont = 0;
        for (int c = 0; c < this.tabelaColunaTam; c++) {
            if (this.tabela[0][c] < 0) {
                cont++;
            }
        }
        if (cont > 0) {
            return true;
        } else {
            return false;
        }
    }
//...................... M�todo que calcula as novas linhas ...........................................................

    private void calcula() {
        int posicaoMenorValor, linhaQueSai;
        posicaoMenorValor = pegaPosicaoMenorValorNegativo();//Pega a posi��o na matriz que tem o maior valor negativo absoluto
        linhaQueSai = pegaLinhaQueSai(posicaoMenorValor);//Pega a linha que sai para gerar novas linhas
        calcularNLP(posicaoMenorValor, linhaQueSai);
        calcularNovasLinhas(posicaoMenorValor, linhaQueSai);
        if (verificaNegativos()) {
            calcula();
        } else {
            this.listarTabela();
        }
    }
//...................... Calcula Novas Linhas ...........................................................................

    private void calcularNovasLinhas(int posicaoMenorValor, int linhaQueSai) {
        Double[] auxNlp = new Double[this.tabelaColunaTam];

        for (int l = 0; l < this.tabelaLinhaTam; l++) {
            if (l != linhaQueSai) {
                for (int c = 0; c < this.tabelaColunaTam; c++) {
                    auxNlp[c] = this.tabela[linhaQueSai][c] * (this.tabela[l][posicaoMenorValor] * (-1));
                }
                for (int c = 0; c < this.tabelaColunaTam; c++) {
                    this.tabela[l][c] = this.tabela[l][c] + auxNlp[c];
                }
            }
        }
    }
//...................... Calcula NLP(Nova Linha Piv�) ...................................................................

    private void calcularNLP(int posicaoMenorValor, int linhaQueSai) {
        System.out.println(this.tabela[linhaQueSai][posicaoMenorValor]);
        Double valor = this.tabela[linhaQueSai][posicaoMenorValor];
        for (int c = 0; c < this.tabelaColunaTam; c++) {
            this.tabela[linhaQueSai][c] = this.tabela[linhaQueSai][c] / valor;
        }
    }
//...................... Pegar linha que sai .............................................................................

    private int pegaLinhaQueSai(int linhaDaDivisao) {
        int linhaQueSai = 1;
        Double[] resultadosDivisao = new Double[this.tabelaLinhaTam - 1];
        for (int l = 1; l < this.tabelaLinhaTam; l++) {
            resultadosDivisao[l - 1] = (this.tabela[l][this.tabelaColunaTam - 1] / this.tabela[l][linhaDaDivisao]);
        }
        Double valor = resultadosDivisao[0];
        for (int i = 0; i < resultadosDivisao.length; i++) {
            if (resultadosDivisao[i] >= 0 && resultadosDivisao[i] < valor) {
                valor = resultadosDivisao[i];
                linhaQueSai = i + 1;
            }
        }
        return linhaQueSai;
    }
//...................... Mostra o resultado dos calculos .............................................................

    public void mostraResultados() {
        DecimalFormat df = new DecimalFormat("0.###");
        String saida;
        System.out.println("\n[ Variáveis Básicas ]\n");
        for (int c = 1; c < this.tabelaColunaTam - 1; c++) {
            for (int l = 1; l < this.tabelaLinhaTam; l++) {
                if (this.tabela[l][c] > 0 && this.tabela[l][c] == 1 && this.tabela[l][c] <= 1) {
                    saida = df.format(this.tabela[l][this.tabelaColunaTam - 1]);
                    System.out.println("* X" + c + " = " + saida);
                }
            }
        }
        System.out.println("\n[ Variáveis não Básicas(que devem ser igual a zero)]\n");
        for (int c = 1; c < this.tabelaColunaTam - 1; c++) {
            for (int l = 1; l < this.tabelaLinhaTam; l++) {
                if (this.tabela[l][c] > 1 || this.tabela[l][c] < 0) {
                    System.out.println("* X" + c);
                    break;
                }
            }
        }
        System.out.println("\n* [ O valor de Z ] *\n");
        System.out.println("* Z = " + this.tabela[0][this.tabelaColunaTam - 1]);
        resultado = this.tabela[0][this.tabelaColunaTam - 1];
    }
//...................... Método que retorna menor valor da matriz .....................................................

    private int pegaPosicaoMenorValorNegativo() {
        int posicao = 0;
        Double valor = 0.0;
        for (int c = 0; c < this.tabelaColunaTam; c++) {
            if (this.tabela[0][c] < 0 && this.tabela[0][c] < valor) {
                posicao = c;
                valor = this.tabela[0][c];
            }
        }
        return posicao;
    }
//...................... Método que possibilita inserir valores na tabela ..............................................    

    private void InserirValoresIniciaisEmTabela(ArrayList<Restricao> restricoes, ArrayList<Double> lucros) {
        //Preenche com zeros a tabela
        for (int l = 0; l < this.tabelaLinhaTam; l++) {
            for (int c = 0; c < this.tabelaColunaTam; c++) {
                this.tabela[l][c] = 0.0;
            }
        }
        //Z sempre vai ser igual a 1 no come�o
        this.tabela[0][0] = 1.0;

        //Coloca os valores de lucro de cada vari�vel
        for (int c = 1; c < this.qtdVariaveis + 1; c++) {
            this.tabela[0][c] = (lucros.get(c - 1)) * (-1);
        }
        //Coloca os valores das restrições e seus limites
        for (int l = 1; l < this.tabelaLinhaTam; l++) {
            for (int c = 1; c <= this.qtdVariaveis; c++) {
                this.tabela[l][c] = restricoes.get(l - 1).getLista(c - 1);
            }
            this.tabela[l][this.tabelaColunaTam - 1] = restricoes.get(l - 1).getLimite();
        }
        //Coloca o valor das variáveis de folga na tabela
        int valor = this.qtdVariaveis + 1;
        for (int i = 1; i < this.tabelaLinhaTam; i++) {
            this.tabela[i][valor] = 1.0;
            valor++;
        }
    }
//...................... Apenas lista a tabela ............................................................

    public void listarTabela() {
        System.out.println("\n\n* [ Listagem da Tabela ] *\n");
        DecimalFormat df = new DecimalFormat("0.###");
        String saida;
        for (int l = 0; l < this.tabelaLinhaTam; l++) {
            for (int c = 0; c < this.tabelaColunaTam; c++) {
                saida = df.format(this.tabela[l][c]);
                System.out.print("   " + saida);
                al.add(saida);
            }
            System.out.println();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        quantidadeVariaveis = new javax.swing.JSpinner();
        quantidadeRestricoes = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        label = new javax.swing.JLabel();
        melhorSolucaoMax = new javax.swing.JTextField();
        Mostrar = new javax.swing.JButton();

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIMPLEX");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImages(null);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/simplex/haapywayLogoPrincipal.jpg"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel6.setText("SIMPLEX");

        jLabel4.setText("Quantidade de Variáveis :");

        jLabel5.setText("Quantidade de Restrições :");

        jButton1.setText("Inserir Dados");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        label.setText("Resultado da Maximização (Z) :");

        melhorSolucaoMax.setText(" ");

        Mostrar.setText("Calcular");
        Mostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MostrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Mostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(quantidadeRestricoes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                            .addComponent(quantidadeVariaveis, javax.swing.GroupLayout.Alignment.LEADING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(melhorSolucaoMax)
                .addGap(56, 56, 56))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(quantidadeVariaveis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(quantidadeRestricoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Mostrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label)
                    .addComponent(melhorSolucaoMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Pega a quantidade de variaveis que serao utilizadas na tabela
        int qtdVar = (int) quantidadeVariaveis.getValue();
        int qtdRestricoes = (int) quantidadeRestricoes.getValue();
        System.out.println("Quantidade de Variaveis :" + qtdVar + "\n" + "Quantidade e Restrições :" + qtdRestricoes);

        Double valor;
        ArrayList<Restricao> restricoes = new ArrayList<Restricao>();
        ArrayList<Double> lucros = new ArrayList<Double>();

        //Inserir os lucros das variaveis em uma lista da linha que tem o Z para que possa ser enviado para tabela
        System.out.println("\n* INSERIR LUCRO POR CADA VARIÁVEL *\n");
        for (int i = 0; i < qtdVar; i++) {
            valor = Double.parseDouble(JOptionPane.showInputDialog(null, "Digite o valor do lucro para variavel X" + (i + 1) + "da função objetivo:"));
            lucros.add(valor);
            System.out.println("Lucro variavel :" + (i + 1) + " = " + valor);
        }
        // Inserir as restricoees em uma lista para que possa passar para um metodo que preencha na tabela
        System.out.println("\n  * INSERIR AS RESTRICOES *\n");
        Restricao r;

        int ptr = 0;

        for (int i = 0; i < qtdRestricoes; i++) {
            System.out.println("=> Restrição " + (i + 1) + " : \n");
            r = new Restricao();//Criei uma classe que serve como estrutura de dados para armazenar as restri��es
            for (int v = 0; v < qtdVar; v++) {
                valor = Double.parseDouble(JOptionPane.showInputDialog(null, "Digite a variavel X" + (v + 1) + " da " + nomeRestricao[ptr]));
                r.addVariavel(valor);
                System.out.println("valor variavel X" + (v + 1) + ":" + valor);

            }
            valor = Double.parseDouble(JOptionPane.showInputDialog(null, "Digite o limite da " + nomeRestricao[ptr] + "\" <= \""));
            r.setLimite(valor);
            restricoes.add(r);
            System.out.println("limite da variavel <=" + valor);
            ptr++;
        }
        simplex m = new simplex(qtdRestricoes + 1, qtdVar + qtdRestricoes + 2, restricoes, qtdVar, lucros);
        m.mostraResultados();


    }//GEN-LAST:event_jButton1ActionPerformed

    private void MostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MostrarActionPerformed
        melhorSolucaoMax.setText("" + resultado);
        String format = String.format("%.2f", resultado);
        melhorSolucaoMax.setText(format);


    }//GEN-LAST:event_MostrarActionPerformed

    public static void main(String args[]) {
        System.out.println("\n  * PROGRAMA DE MAXIMIZAÇÃO SIMPLEX   *\n");

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    //javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
                    //    javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                    //   javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                    javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(simplex.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(simplex.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(simplex.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(simplex.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new simplex().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Mostrar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel label;
    private javax.swing.JTextField melhorSolucaoMax;
    private javax.swing.JSpinner quantidadeRestricoes;
    private javax.swing.JSpinner quantidadeVariaveis;
    // End of variables declaration//GEN-END:variables
}
