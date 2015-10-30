package models;
 
import java.util.*;
import javax.persistence.*;

import play.data.binding.*;
import play.data.validation.*;
import play.db.jpa.Model;

// Analogamente: Anuncio
@Entity
public class Post extends Model {
	@Required
	public String cidade;
	@Required
	public String bairro;
	public String strInstrumentos;
	public String strEstilosQueGosta;
	public String strEstilosQueNaoGosta;
	public boolean procuraBanda; // false = tocar ocasionalmente
	public boolean finalizado;
 
    @Required
    public String title;
    
    @Required @As("yyyy-MM-dd")
    public Date postedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content; // descricao
    
    @Required
    @ManyToOne
    public User author;
    
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<Comment> comments;
    
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;
    
    public Post(User author, String title, String content) { 
        this.comments = new ArrayList<Comment>();
        this.tags = new TreeSet();  
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
        finalizado = false;
    }
    
    public Post(User author, String title, String content, 
    		String cidade, String bairro, String strInstrumentos, 
    		String strEstilosQueGosta, String strEstilosQueNaoGosta, boolean procuraBanda) {
    	this(author, title, content);
    	this.cidade = cidade;
    	this.bairro = bairro;
    	this.strInstrumentos = strInstrumentos;
    	this.strEstilosQueGosta = strEstilosQueGosta;
    	this.strEstilosQueNaoGosta = strEstilosQueNaoGosta;
    	this.procuraBanda = procuraBanda;
    }
 // Construtor criado pra inserir anuncios ja finalizados na inicializacao
    public Post(User author, String title, String content, 
    		String cidade, String bairro, String strInstrumentos, 
    		String strEstilosQueGosta, String strEstilosQueNaoGosta, 
    		boolean procuraBanda, boolean finalizado){
    	this(author, title, content, cidade, bairro, strInstrumentos, strEstilosQueGosta, strEstilosQueNaoGosta, procuraBanda);
    	this.finalizado = finalizado; 
    }
    
    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content);
        this.comments.add(newComment);
        this.save();
        return this;
    }
    
    public Post previous() {
        return Post.find("postedAt < ?1 order by postedAt desc", postedAt).first();
    }

    public Post next() {
        return Post.find("postedAt > ?1 order by postedAt asc", postedAt).first();
    }
    
    public Post tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }
    
    public static List<Post> findTaggedWith(String tag) {
        return Post.find(
            "select distinct p from Post p join p.tags as t where t.name = ?",
            tag
        ).fetch();
    }
    
    public static List<Post> findTaggedWith(String... tags) {
        return Post.find(
            "select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
    
    public String toString() {
        return title;
    }
 
}
