package controllers;

import play.*;
import play.mvc.*;
import play.utils.HTML;
import play.data.validation.*;
import play.libs.*;
import play.cache.*;

import java.util.*;

import models.*;
 
public class Application extends Controller {
    
    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }
 
    public static void index() {
        Post frontPost = Post.find("order by postedAt desc").first();
        List<Post> olderPosts = Post.find("order by postedAt desc").from(1).fetch(10);
        render(frontPost, olderPosts);
    }
    
    public static void show(Long id) {
        Post post = Post.findById(id);
        String randomID = Codec.UUID();
        render(post, randomID);
    }
    
    public static void postComment(
        Long postId, 
        @Required(message="Author is required") String author, 
        @Required(message="A message is required") String content, 
        @Required(message="Please type the code") String code, 
        String randomID) 
    {
        Post post = Post.findById(postId);
        if(!Play.id.equals("test")) {
            validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
        }
        if(validation.hasErrors()) {
            render("Application/show.html", post, randomID);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
        show(postId);
    }
    
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "30mn");
        renderBinary(captcha);
    }
    
    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }
    
    public static void newUser() {
        render("Application/newUser.html");   
    }
    
    public static void addNewUser(Long id, 
           @Required(message="Email necessario") String email, 
           @Required(message="Senha necessaria") String password, 
           @Required(message="Nome necessario") String fullname,
           @Required(message="Digite o password novamente") String segundoPassword) {
        User user = new User(email, password, fullname);
        if( !(password.equals(segundoPassword))){
            //flash.success("O segundo password nao esta igual ao primeiro%s", ".");
            flash.error("O segundo password nao esta igual ao primeiro.");
            render("Application/newUser.html", user);
            return;
        }
        if(validation.hasErrors()) {
            render("Application/newUser.html", user);
        }
        user.save();
        flash.success("Obrigado por se cadastrar, %s.", fullname);
        render("Application/newUser.html");
        
    }
 
}
