package redes.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FacebookController {

	private Facebook facebook;
    private ConnectionRepository connectionRepository;

    @Autowired
    public FacebookController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = new FacebookTemplate("EAACEdEose0cBAEsZBb7RSrsOIn6gzsNEfcZAPUOaVSLTG9pOtZAv4SJZAXsUFZA0poyo0s3ZCZCIld92Y1dnh1eYnKShcecMQldH6KIXI8btfYZA6G6nohWcPGwbxZAjLO4IY4JZC4H8yYBkadheYcQ1OfHtf9btfNCOwk1q5sr9fnVOikpKQ4H1vD2bzksmiNJoIZD");
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
        model.addAttribute("pr", paginaRecomendada);
        
        return "resultado";
    }
    
    @GetMapping(path = "/postar")
    public String helloFacebook(@RequestParam(value="nomepagina", required=true) String nomepagina) {
    	facebook.feedOperations().updateStatus("[TESTE] O site https://recmus.herokuapp.com me recomendou a pagina de musica: " + nomepagina);
    	return "post";
    }
    
    private Page recomendarMusica(PagedList<Page> uPagMus, PagedList<Reference> amigos){
    	PagedList<Page> acpms = musicasAmigoComum(uPagMus, amigos);
    	ArrayList<Page> paginasRec = new ArrayList<>();
    	for(Page acp : acpms)
    		if(!uPagMus.contains(acp)) paginasRec.add(acp);
    	return paginasRec.get(randInt(0, paginasRec.size()-1));
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
