package com.sid.demoms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@SpringBootApplication
public class DemoMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMsApplication.class, args);
    }
    @Bean
    CommandLineRunner start(ProduitRepository produitRepository, RepositoryRestConfiguration restConfiguration){
        return args -> {
            restConfiguration.exposeIdsFor(Produit.class); //Add id Produit dans l'affichage (json)
            produitRepository.save(new Produit(null, "Ord Hp 54", 6000, 5));
            produitRepository.save(new Produit(null, "Ord Hp 54", 2000, 12));
            produitRepository.save(new Produit(null, "Smart Phone", 400, 7));
            produitRepository.findAll().forEach(produit -> {
                System.out.println(produit.getName());
            });
        };
    }

}

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
class Produit{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private double quantity;
}

@RepositoryRestResource  //Spring Data Rest remplace RestController, et il contient tous les méthodes classiques (save, get, update, delete), et ainsi il intégre la pagination
interface ProduitRepository extends JpaRepository<Produit, Long>{
    @RestResource(path = "/byName")
    Page<Produit> findByNameContains(@Param("kw") String nom, Pageable pageable);
}

@Projection(name = "mobile", types = Produit.class)
interface ProduitProjection{
    String getName();
}
@Projection(name = "web", types = Produit.class)
interface ProduitProjection2{
    String getName();
    String getPrice();
}



//@RestController  si on a des opérations plus complex il faudrait créer un couche service, alors on doit créer RestController
class ProduitRestController{
    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping("/produits")
    public List<Produit> list(){
        return produitRepository.findAll();
    }
    @GetMapping("/produits/{id}")
    public Produit produit(@PathVariable Long id){
        return produitRepository.findById(id).get();
    }
    @PostMapping("/produits")
    public Produit save(@RequestBody Produit produit){
        return produitRepository.save(produit);
    }
    @PutMapping("/produits/{id}")
    public Produit update(@PathVariable Long id, @RequestBody Produit produit){
        produit.setId(id);
        return produitRepository.save(produit);
    }
    @DeleteMapping("/produits/{id}")
    public void delete(@PathVariable Long id){
        produitRepository.deleteById(id);
    }
}
