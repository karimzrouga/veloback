package com.itgate.ecommerce.controllers;

import com.itgate.ecommerce.models.*;
import com.itgate.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
//hedhi marbouta bel front end w tzid fichier kaml esmou util mta3 local storage
@CrossOrigin("*")
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategorieVeloRepository categorieVeloRepository;
    @Autowired
    private ArbitreRepository arbitreRepository;
    @Autowired
    private EquipeFederaRepository equipeFederaRepository;

    @Autowired
    private CoureurRepository coureurRepository;


    @GetMapping("/all")
    public List<Competition> getallCompetition(){
        return competitionRepository.findAll();

    }
  /*  @PostMapping("/save/{id_categorie}")
    public Competition saveCompetition(@RequestBody Competition co,@RequestParam List<Long> ida,@RequestParam List<Long> idc,  @PathVariable Long id_categorie){
        for (int i=0;i<ida.size();i++){
            Arbitre a =arbitreRepository.findById(ida.get(i)).get();
           co.addarbitre(a);
            System.out.println(a);
        }

        for (int i=0;i<idc.size();i++){
            Coureur c =coureurRepository.findById(idc.get(i)).get();
            co.addcoureur(c);
            System.out.println(c);
        }


        CategorieVelo cv = categorieVeloRepository.findById(id_categorie).get();
        co.setCategorieVelo(cv);

        return competitionRepository.save(co);
    }
*/


    @PostMapping("/savenew/{id_categorie}")
    public Competition saveCompetitionfinal(@RequestBody Competition co,  @PathVariable Long id_categorie){
       /* for (int i=0;i<ida.size();i++){
            Arbitre a =arbitreRepository.findById(ida.get(i)).get();
            co.addarbitre(a);
            System.out.println(a);
        }*/

       // co.setList(idc);
       // ,@RequestParam ArrayList<Long> idc
      //  for (int i=0;i<idc.size();i++){
           // Coureur c =coureurRepository.findById(idc.get(i)).get();
         //   ArrayList<Long> myList = new ArrayList<>(Arrays.asList(idc.get(i)));
          //  co.setList(myList);
           // System.out.println(c);
      //  }


        CategorieVelo cv = categorieVeloRepository.findById(id_categorie).get();
        co.setCategorieVelo(cv);


co.setConfirmation("en cours");
        return competitionRepository.save(co);
    }

    @PutMapping("/affecterarbitre/{id}")
    public Competition affecterarbitreCompetition(@PathVariable Long id ,@RequestParam List<Long> ida){
        Competition co1=competitionRepository.findById(id).get();
        if(co1!= null){
            co1.setId(id);
            for (int i=0;i<ida.size();i++){
                Arbitre a =arbitreRepository.findById(ida.get(i)).get();
                co1.addarbitre(a);
                System.out.println(a);
            }
            return competitionRepository.saveAndFlush(co1);
        }
        else{
            throw  new RuntimeException("FAIL!!");
        }
    }
    @PutMapping("/affectercoureur/{id}")
    public Competition affectercoureurCompetition(@PathVariable Long id ,@RequestParam ArrayList<Long> idc){
        Competition co1=competitionRepository.findById(id).get();
        if(co1!= null){
            co1.setId(id);
            co1.setList(idc);
            return competitionRepository.saveAndFlush(co1);
        }
        else{
            throw  new RuntimeException("FAIL!!");
        }
    }

    @PutMapping("/confirmer/{id}")
    public Competition confirmer(@PathVariable Long id){
        Competition co1=competitionRepository.findById(id).get();
        if(co1!= null){
            co1.setId(id);
      co1.setConfirmation("confirmée");
            return competitionRepository.saveAndFlush(co1);
        }
        else{
            throw  new RuntimeException("FAIL!!");
        }
    }

    @GetMapping("/getone/{id}")
    public Competition getone(@PathVariable Long id){

        return competitionRepository.findById(id).get();
    }
    @PutMapping("/update/{id}/{id_categorie}")
    public Competition updateCompetition(@PathVariable Long id,@RequestBody Competition co,@PathVariable Long id_categorie){
        Competition co1=competitionRepository.findById(id).get();
        if(co1!= null){
            co.setId(id);
            co.setArbitres(co1.getArbitres());
            co.setList(co1.getList());

            co.setConfirmation(co1.getConfirmation());
            CategorieVelo cv = categorieVeloRepository.findById(id_categorie).get();
            co.setCategorieVelo(cv);
            return competitionRepository.saveAndFlush(co);
        }
        else{
            throw  new RuntimeException("FAIL!!");
        }
    }
    @DeleteMapping("/delete/{id}")
    public HashMap<String ,String > deleteCompetition(@PathVariable Long id){
        HashMap<String ,String> message=new HashMap<>();
        try{
            competitionRepository.deleteById(id);
            message.put("etat","competition deleted");

        }catch (Exception e){
            message.put("etat","competition  not deleted");
        }
        return message;
    }





}
