package br.com.dio.model;

import java.util.Collection;
import java.util.List;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces; // grade 9x9 de objetos Space

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        // Verifica se NENHUM espaço não fixo tem um valor. Se for o caso, NÃO FOI INICIADO.
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        // Se algum espaço ainda é nulo (vazio) e não fixo, o jogo está INCOMPLETO. Caso contrário, está COMPLETO.
        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    // Verifica se o tabuleiro contém erros
    public boolean hasErrors(){
        if(getStatus() == NON_STARTED){
            return false; // Se o jogo não foi iniciado (sem input do usuário), não há erros
        }

        // Verifica se espaço preenchido pelo usuário não corresponde ao (expected)
        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    // Altera o valor de uma célula
    public boolean changeValue(final int col, final int row, final int value){
        var space = spaces.get(row).get(col); // Acessa o Space na [linha][coluna]
        if (space.isFixed()){
            return false; // Não pode alterar valores fixos
        }

        space.setActual(value); // Define o valor inserido pelo usuário
        return true;
    }

    // Limpa o valor de uma célula
    public boolean clearValue(final int col, final int row){
        var space = spaces.get(row).get(col); // Acessa o Space na [linha][coluna]
        if (space.isFixed()){
            return false; // Não pode limpar valores fixos
        }

        space.clearSpace(); // Limpa o valor
        return true;
    }

    // Reseta o jogo, limpando os valores não fixos
    public void reset(){
        spaces.forEach(rowList -> rowList.forEach(space -> {
            if (!space.isFixed()) {
                space.clearSpace();
            }
        }));
    }

    // Verifica se o jogo está finalizado
    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }
}