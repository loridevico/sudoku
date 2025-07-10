package br.com.dio.model;

public class Space {

    private Integer actual; // valor atual inserido
    private final int expected; //valor correto para esta célula
    private final boolean fixed; // Verdadeiro se é um número fixed

    public Space(final int expected, final boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        if (fixed){ //Se é um número fixo, o valor atual é o esperado
            actual = expected;
        } else {
            actual = null; //Espaços não fixos começam vazios
        }
    }

    public Integer getActual() {
        return actual;
    }

    // Define o valor atual para célula espaço
    public void setActual(final Integer actual) {
        if (fixed) return;
        this.actual = actual;
    }

    // Limpa o espaço, definindo o valor atual como null para célula espaço
    public void clearSpace(){
        if (fixed) return;
        setActual(null);
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }
}