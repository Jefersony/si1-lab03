package controllers;
 
import play.*;
import play.mvc.*;
import play.data.validation.*;

import java.util.*;

import models.*;
 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
 
    public static void index() {
        List<Post> posts = Post.find("author.email", Security.connected()).fetch();
        render(posts);
    }
    
    public static void form(Long id) {
        if(id != null) {
            Post post = Post.findById(id);
            render(post);
        }
        render();
    }
    
    public static void save(Long id, String title, String content, String tags,
    		String cidade, String bairro, String strInstrumentos, 
    		String strEstilosQueGosta, String strEstilosQueNaoGosta, String procuraBanda) {
        Post post;
        if(id == null) {
            // Create post
            User author = User.find("byEmail", Security.connected()).first();
            post = new Post(author, title, content, 
            		        cidade, bairro, strInstrumentos, strEstilosQueGosta, strEstilosQueNaoGosta, Boolean.valueOf(procuraBanda));
        } else {
            // Retrieve post
            post = Post.findById(id);
            post.title = title;
            post.content = content;
            post.tags.clear();
            post.cidade = cidade;
            post.bairro = bairro;
            post.strInstrumentos = strInstrumentos;
            post.strEstilosQueGosta = strEstilosQueGosta;
            post.strEstilosQueNaoGosta = strEstilosQueNaoGosta;
            post.procuraBanda = Boolean.valueOf(procuraBanda);
            //System.out.println(post.procuraBanda + " - s:" + procuraBanda);
        }
        // Set tags list
        for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                post.tags.add(Tag.findOrCreateByName(tag));
            }
        }
        // Validate
        validation.valid(post);
        if(validation.hasErrors()) {
            render("@form", post);
        }
        // Save
        post.save();
        index();
    }
    
}
