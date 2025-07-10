package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        System.out.println("DEBUG: Número de argumentos recebidos: " + args.length);
        if (args.length == 0) {
            System.out.println("DEBUG: Nenhum argumento de linha de comando foi recebido.");
            System.out.println("DEBUG: Certifique-se de configurar os 'Program arguments' no IntelliJ.");
        } else {
            System.out.println("DEBUG: Argumentos recebidos:");
            for (String arg : args) {
                System.out.println("DEBUG:    " + arg);
            }
        }
        // Processa os argumentos de linha de comando para configurar o tabuleiro inicial.
        // Cada argumento é uma string no formato "col,row;expected,fixed".
        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0], // A chave do mapa é "col,row"
                        v -> v.split(";")[1]  // O valor do mapa é "expected,fixed"
                ));

        var option = -1;
        while (true){ // Loop principal do jogo para o menu
            System.out.println("\nSelecione uma das opções a seguir:");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            try {
                option = scanner.nextInt(); // Lê a opção do usuário
                scanner.nextLine(); // Consome a quebra de linha pendente
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida para a opção. Por favor, digite um número.");
                scanner.nextLine(); // Limpa o buffer
                continue; // Continua para a próxima iteração do loop para exibir o menu novamente
            }

            switch (option){
                case 1 -> startGame(positions); // Inicia o tabuleiro com as posições iniciais
                case 2 -> inputNumber();       // Permite ao usuário inserir um número
                case 3 -> removeNumber();      // Permite ao usuário remover um número
                case 4 -> showCurrentGame();   // Exibe o tabuleiro atual
                case 5 -> showGameStatus();    // Mostra o status do jogo (incompleto, completo, com erros)
                case 6 -> clearGame();         // Limpa os números inseridos pelo usuário
                case 7 -> finishGame();        // Tenta finalizar o jogo (verifica solução)
                case 8 -> {
                    System.out.println("Saindo do jogo. Até mais!");
                    scanner.close(); // Fecha o scanner antes de sair
                    System.exit(0); // Sai da aplicação
                }
                default -> System.out.println("Opção inválida, selecione uma das opções do menu.");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("O jogo já foi iniciado.");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) { // Percorre as linhas
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) { // Percorre as colunas
                // Obtém a configuração da posição (ex: "0,0": "4,false")
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                // Extrai o valor esperado e se é fixo
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed); // Cria o objeto Space
                spaces.get(i).add(currentSpace); // Adiciona à lista de espaços (linha a linha)
            }
        }

        board = new Board(spaces); // Cria o objeto Board
        System.out.println("O jogo está pronto para começar!");
    }

    private static void inputNumber() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido (0-8):");
        var col = runUntilGetValidNumber(0, 8); // Valida a entrada para a coluna
        System.out.println("Informe a linha em que o número será inserido (0-8):");
        var row = runUntilGetValidNumber(0, 8); // Valida a entrada para a linha
        System.out.printf("Informe o número que vai entrar na posição [%d,%d] (1-9):%n", col, row);
        var value = runUntilGetValidNumber(1, 9); // Valida a entrada para o valor

        // Tenta alterar o valor no tabuleiro
        if (!board.changeValue(col, row, value)){
            System.out.printf("A posição [%d,%d] tem um valor fixo e não pode ser alterada.%n", col, row);
        } else {
            System.out.println("Número inserido com sucesso!");
        }
    }

    private static void removeNumber() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna em que o número será removido (0-8):");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o número será removido (0-8):");
        var row = runUntilGetValidNumber(0, 8);

        // Tenta limpar o valor no tabuleiro
        if (!board.clearValue(col, row)){
            System.out.printf("A posição [%d,%d] tem um valor fixo e não pode ser removida.%n", col, row);
        } else {
            System.out.println("Número removido com sucesso!");
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        var spacesList = board.getSpaces(); // Obtém a lista de espaços (lista de linhas)
        // Preenche o array para o template de exibição do tabuleiro
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                args[argPos++] = " " + ((isNull(spacesList.get(row).get(col).getActual())) ? " " : spacesList.get(row).get(col).getActual());
            }
        }
        System.out.println("\nSeu jogo se encontra da seguinte forma:");
        System.out.printf((BOARD_TEMPLATE) + "%n", args); // %n para nova linha
    }

    private static void showGameStatus() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status: %s%n", board.getStatus().getLabel());
        if(board.hasErrors()){
            System.out.println("O jogo contém erros.");
        } else {
            System.out.println("O jogo não contém erros.");
        }
        showCurrentGame(); // Exibe o tabuleiro para o usuário ver o status
    }

    private static void clearGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso? (sim/não)");
        var confirm = scanner.nextLine();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")){
            System.out.println("Informe 'sim' ou 'não':");
            confirm = scanner.nextLine();
        }

        if(confirm.equalsIgnoreCase("sim")){
            board.reset(); //Chama o reset no Board
            System.out.println("Limpado com sucesso!");
        } else {
            System.out.println("Limpeza cancelada.");
        }
    }

    private static void finishGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        if (board.gameIsFinished()){ //Verifica se completo e sem erros
            System.out.println("Parabéns! Você concluiu o jogo!");
            showCurrentGame(); // Mostra o tabuleiro final
            board = null; // Reinicia o jogo após a conclusão
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros. Verifique seu tabuleiro e ajuste-o.");
            showCurrentGame(); // Mostra o tabuleiro com os erros
        } else {
            System.out.println("Você ainda precisa preencher algum espaço para finalizar o jogo.");
            showCurrentGame(); // Mostra o tabuleiro com os espaços vazios
        }
    }

    // Verificar numero valido
    private static int runUntilGetValidNumber(final int min, final int max){
        var current = -1; // Valor inicial inválido
        while (true){ // Loop até uma entrada válida
            try {
                current = scanner.nextInt();
                scanner.nextLine();
                if (current >= min && current <= max){
                    return current;
                } else {
                    System.out.printf("Número fora do intervalo. Informe um número entre %d e %d:%n", min, max);
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
            }
        }
    }
}