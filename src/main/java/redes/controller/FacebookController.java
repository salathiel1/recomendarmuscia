package redes.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PostData;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FacebookController {

	private Facebook facebook;
    private ConnectionRepository connectionRepository;

    @Autowired
    public FacebookController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;//new FacebookTemplate("EAACEdEose0cBAMJ6ZBNFeiiJD2GlC3vUyZCyC16wdPqxVQtZBOYjt0Kt9iVTJSPInatE1CXDP1YNZCTOY0aYHINIiFIFT9HnoALruuZBFclGGwqGE2Has7NIIpTW1XyD3zConwZATSRAxCQaxLkVHrNQ6wIpMHZCF1RwFS94QGOgSvg9XcfwIIFlk5DNVLqSZBMZAtVzuNmvZBNwZDZD");
        this.connectionRepository = connectionRepository;
    }

    @GetMapping(path = "/infoFacebook")
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null)
            return "redirect:/connect/facebook";

        String [] fields = { "id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work", "music"};
        User userProfile = facebook.fetchObject("me", User.class, fields);
        model.addAttribute("perfil", userProfile);
        PagedList<Page> minhasPaginasMusicas = facebook.likeOperations().getMusic();
        model.addAttribute("mpm", minhasPaginasMusicas);
        PagedList<Reference> amigos = facebook.friendOperations().getFriends();
        Page paginaRecomendada = recomendarMusica(minhasPaginasMusicas, amigos);
        if(paginaRecomendada != null)
        	model.addAttribute("pr", paginaRecomendada);
        else
        	model.addAttribute("pr", new Page());
        
        return "resultado";
    }
    
    @GetMapping(path = "/postar")
    public String postar(@RequestParam(value="nomepagina", required=true) String nomepagina) {
    	//facebook.feedOperations().post("", "https://recmus.herokuapp.com/compartilhar/"+nomepagina);
    	facebook.feedOperations().post(new PostData("me").link("https://recmus.herokuapp.com/compartilhar/"+nomepagina, 
    			"https://i.imgur.com/VumUITz.jpg", 
    			"Pagina de musica recomendada: " + nomepagina, 
    			"Projeto da disciplina de redes sociais", 
    			"O site https://recmus.herokuapp.com me recomendou a pagina de musica: " + nomepagina));
    	return "post";
    }
    
    @GetMapping(path = "/compartilhar/{nomepagina}")
    public ModelAndView paginaPost(@PathVariable String nomepagina) {
    	ModelAndView model = new ModelAndView("compartilhar");
		model.addObject("nomepagina", nomepagina);
		return model;
    }
    
    private Page recomendarMusica(PagedList<Page> uPagMus, PagedList<Reference> amigos){
    	PagedList<Page> acpms = musicasAmigoComum(uPagMus, amigos);
    	ArrayList<Page> paginasRec = new ArrayList<>();
    	if(acpms != null && acpms.size() > 0){
    		for(Page acp : acpms)
    			if(!uPagMus.contains(acp)) paginasRec.add(acp);
    		return paginasRec.get(randInt(0, paginasRec.size()-1));
    	}
    	return null;
    }
    
    private PagedList<Page> musicasAmigoComum(PagedList<Page> uPagMus, PagedList<Reference> amigos){
    	PagedList<Page> acpms = null;
    	if(amigos.size() > 0){
        	int qtComunMelhor = -1;
        	for(int i = 0; i < amigos.size(); i++){
        		String aid = amigos.get(i).getId();
        		PagedList<Page> apms = facebook.likeOperations().getMusic(aid);
        		int qtMusComun = qtdMusicasComum(uPagMus, apms);
        		if(qtMusComun != apms.size() && qtMusComun >= qtComunMelhor){ 
        			if(qtMusComun == qtComunMelhor){
        				if(Math.random() > 0.5){
        					acpms = apms;
        					qtComunMelhor = qtMusComun;
        				}
        			}else{
        				acpms = apms;
            			qtComunMelhor = qtMusComun;	
        			}
        		}
        	}
        }
    	return acpms;
    }
    
    private int qtdMusicasComum(PagedList<Page> list1, PagedList<Page> list2){
    	int qtd = 0;
    	for(Page p1 : list1){
			for(Page p2 : list2){
				if(p1.getId().equals(p2.getId())) qtd++;
			}
		}
    	return qtd;
    }
    
    private int randInt(int min, int max){
    	return min + (int)(Math.random() * ((max - min) + 1));
    }
    
}
