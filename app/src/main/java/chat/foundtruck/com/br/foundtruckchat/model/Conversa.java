package chat.foundtruck.com.br.foundtruckchat.model;

/**
 * Created by Gollum on 20/11/2017.
 */

public class Conversa {

    private String idUsuario;
    private String nome;
    private String mensagem;
    private String remetente;

    public Conversa(){

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
