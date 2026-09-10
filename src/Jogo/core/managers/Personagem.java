package Jogo.core.managers;

import Jogo.core.entities.enemies.Enemy;

import java.util.List;

public abstract class Personagem {
    protected final String nome;
    public int maxHp;
    public int hp;
    protected double danoAtaque;
    protected double velocidadeMovimento;
    protected double velocidadeAtaque;
    protected double ouro;

    protected Personagem(String nome, int vidaMax, double danoAtaque,
                         double velocidadeMovimento, double velocidadeAtaque) {
        this.nome = nome;
        this.maxHp = vidaMax;
        this.hp = vidaMax;
        this.danoAtaque = danoAtaque;
        this.velocidadeMovimento = velocidadeMovimento;
        this.velocidadeAtaque = velocidadeAtaque;
    }

    public void receberDano(double dano) {
        hp = Math.max(0, hp - (int) Math.ceil(dano));
    }

    public void aoMatarInimigo(boolean ehBoss, double recompensaBase) {
        ouro += recompensaBase;
    }

    public abstract void atualizarPassiva(List<Enemy> inimigos);

    public String getNome() { return nome; }
    public int getVidaAtual() { return hp; }
    public int getVidaMax() { return maxHp; }
    public double getDanoAtaque() { return danoAtaque; }
    public double getVelocidadeAtaque() { return velocidadeAtaque; }
    public double getOuro() { return ouro; }
    public boolean estaVivo() { return hp > 0; }
}