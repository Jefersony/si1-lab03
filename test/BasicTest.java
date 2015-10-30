import org.junit.*;

import java.util.*;

import play.test.*;
import models.*;
 
public class BasicTest extends UnitTest {
    
    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        Fixtures.deleteAll();
    }
 
    @Test
    public void criaEBuscaUsuario() {
        // Cria um novo usuario e o salva
        new User("bob@gmail.com", "secret", "Bob").save();

        // Busca o usuario Bob
        User bob = User.find("byEmail", "bob@gmail.com").first();

        assertNotNull(bob);
        assertEquals("Bob", bob.fullname);
    }
    
    @Test
    public void tentaConectarUsuario() {
        // Cria um novo usuario e o salva
        new User("bob@gmail.com", "secret", "Bob").save();

        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }
    
    @Test
    public void criaAnuncio() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Cria um novo anuncio
        new Post(bob, "My first post", "Hello world").save();

        // Testa se o anuncio foi criado
        assertEquals(1, Post.count());

        // Busca todos os anuncios de bob 
        List<Post> bobPosts = Post.find("byAuthor", bob).fetch();

        assertEquals(1, bobPosts.size());
        Post firstPost = bobPosts.get(0);
        assertNotNull(firstPost);
        assertEquals(bob, firstPost.author);
        assertEquals("My first post", firstPost.title);
        assertEquals("Hello world", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }
    
    @Test
    public void postaComentarios() {
    	// Novo usuario
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Novo anuncio
        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        // Posta comentarios
        new Comment(bobPost, "Jeff", "Nice post").save();
        new Comment(bobPost, "Tom", "I knew that !").save();

        // Busca todos os comentarios
        List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();

        assertEquals(2, bobPostComments.size());

        Comment firstComment = bobPostComments.get(0);
        assertNotNull(firstComment);
        assertEquals("Jeff", firstComment.author);
        assertEquals("Nice post", firstComment.content);
        assertNotNull(firstComment.postedAt);

        Comment secondComment = bobPostComments.get(1);
        assertNotNull(secondComment);
        assertEquals("Tom", secondComment.author);
        assertEquals("I knew that !", secondComment.content);
        assertNotNull(secondComment.postedAt);
    }
    
    @Test
    public void verificaContadoresDosModelos() {
        // Novo usuario
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Novo anuncio
        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        // Posta comentarios
        bobPost.addComment("Jeff", "Nice post");
        bobPost.addComment("Tom", "I knew that !");

        // Conta objetos
        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());

        // Busca o primeiro anuncio de Bob
        bobPost = Post.find("byAuthor", bob).first();
        assertNotNull(bobPost);

        // Navega pelos comentarios
        assertEquals(2, bobPost.comments.size());
        assertEquals("Jeff", bobPost.comments.get(0).author);

        // Deleta o anuncio
        bobPost.delete();

        // Checa se os comentarios foram deletados
        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testeCompleto() {
        Fixtures.load("data.yml");

        // Conta objetos
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());

        // Conecta usuarios
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("jeff@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));

        // Busca todos os anuncios de Bob
        List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, bobPosts.size());

        // Busca todos os comentarios relacionados aos anuncios de Bob
        List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, bobComments.size());

        // Busca o anuncio mais recente
        Post frontPost = Post.find("order by postedAt desc").first();
        assertNotNull(frontPost);
        assertEquals("About the model layer", frontPost.title);

        assertEquals(2, frontPost.comments.size());

        // Posta um novo comentario
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());
    }
    
    @Test
    public void testaTags() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Novo anuncio
        Post bobPost = new Post(bob, "My first post", "Hello world").save();
        Post anotherBobPost = new Post(bob, "My second post post", "Hello world").save();
        
        assertEquals(0, Post.findTaggedWith("Red").size());
        
        // Adiciona tags
        bobPost.tagItWith("Red").tagItWith("Blue").save();
        anotherBobPost.tagItWith("Red").tagItWith("Green").save();
        
        // Verifica tags
        assertEquals(2, Post.findTaggedWith("Red").size());        
        assertEquals(1, Post.findTaggedWith("Blue").size());
        assertEquals(1, Post.findTaggedWith("Green").size());
        
        assertEquals(1, Post.findTaggedWith("Red", "Blue").size());   
        assertEquals(1, Post.findTaggedWith("Red", "Green").size());   
        assertEquals(0, Post.findTaggedWith("Red", "Green", "Blue").size());  
        assertEquals(0, Post.findTaggedWith("Green", "Blue").size());    
        
        List<Map> cloud = Tag.getCloud();
        Collections.sort(cloud, new Comparator<Map>() {
            public int compare(Map m1, Map m2) {
                return m1.get("tag").toString().compareTo(m2.get("tag").toString());
            }
        });
        assertEquals("[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]", cloud.toString());
        
    }
 
}