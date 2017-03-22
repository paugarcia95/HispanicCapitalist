/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XML;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author killian
 */
public class Services {

    Unmarshaller u;
    JAXBContext cont;

    public World readWorldFromXml(String pseudo) {
        World monde = null;
        try {
            cont = JAXBContext.newInstance(World.class);
            u = cont.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            monde = (World) u.unmarshal(new File(pseudo + "WorldHispanic.xml"));
        } catch (UnmarshalException e) {
            InputStream input = getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            try {
                monde = (World) u.unmarshal(input);
            } catch (JAXBException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        return monde;
    }

    public World readWorldFromXml() {
        World monde = null;
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            u = cont.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }

        InputStream input = getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
        try {
            monde = (World) u.unmarshal(input);
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }

        return monde;
    }

    public void saveWordlToXml(World world) {
        try {
            OutputStream output = null;
            try {
                output = new FileOutputStream("WorldHispanic.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            }
            // InputStream input= getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            JAXBContext cont;
            cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            m.marshal(world, output);
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveWordlToXml(String pseudo, World world) {
        try {
            OutputStream output = null;
            try {
                output = new FileOutputStream(pseudo + "WorldHispanic.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.INFO, null, "erreur" + pseudo);
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            }
            // InputStream input= getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            JAXBContext cont;
            cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            m.marshal(world, output);

            System.out.println(world.getProducts().getProduct().get(0).getQuantite());
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean updateProduct(String username, ProductType newproduct) {
        World world = getWorld(username);
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            return false;
        }
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            double argent = world.getMoney();
            world.setMoney(argent - (product.getCout()*Math.pow(product.getCroissance(),qtchange)));
            product.setQuantite(product.getQuantite() + qtchange);
            product.setCout(product.getCout()*Math.pow(product.getCroissance(),product.getQuantite()));
            verificationUnlocks(world, product);
        } else {
            product.setTimeleft(product.getVitesse());
            world.setLastupdate(System.currentTimeMillis());
        }
        saveWordlToXml(username, world);
        return true;
    }

    public Boolean updateManager(String username, PallierType newmanager) {
        World world = getWorld(username);
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        manager.setUnlocked(true);
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        product.setManagerUnlocked(true);
        world.setMoney(world.getMoney() - manager.getSeuil());
        saveWordlToXml(username, world);
        return true;
    }

    public boolean updateUpgrade(String username, PallierType pallier) {
        World world = getWorld(username);
        PallierType upgrade = findUpgradeByName(world, pallier);
        if (upgrade == null) {
            return false;
        }
        ProductType product = findProductById(world, upgrade.getIdcible());
        if (product == null) {
            return false;
        }
        String type = upgrade.getTyperatio().value();
        //si c est de type gain
        if (type.equals("gain")) {
            product.setRevenu(product.getRevenu() * upgrade.getRatio());
        } //si c est de type vitesse
        else if (type.equals("vitesse")) {
            product.setVitesse((int) (product.getVitesse() * upgrade.getRatio()));
            product.setTimeleft((long) (product.getTimeleft() / upgrade.getRatio()));
        }
        upgrade.setUnlocked(true);
        world.setMoney(world.getMoney() - upgrade.getSeuil());
        saveWordlToXml(username, world);
        return true;
    }

    public boolean updateAngelUpgrade(String username, PallierType pallier) {
        World world = getWorld(username);
        PallierType upgrade = findAngelUpgradeByName(world, pallier);
        if (upgrade == null) {
            return false;
        }
        String type = upgrade.getTyperatio().value();
        if (type.equals("ange")) {
            world.setAngelbonus((int) (world.getAngelbonus() + upgrade.getRatio()));
        } else {
            ProductType product = findProductById(world, upgrade.getIdcible());
            if (product == null) {
                return false;
            }
            //si c est de type gain
            if (type.equals("gain")) {
                product.setRevenu(product.getRevenu() * upgrade.getRatio());
            } //si c est de type vitesse
            else if (type.equals("vitesse")) {
                product.setVitesse((int) (product.getVitesse() * upgrade.getRatio()));
                product.setTimeleft((long) (product.getTimeleft() / upgrade.getRatio()));
            }
        }
        upgrade.setUnlocked(true);
        world.setActiveangels(world.getActiveangels() - upgrade.getSeuil());
        saveWordlToXml(username, world);
        return true;
    }

    private ProductType findProductById(World world, int id) {
        List<ProductType> lp = world.getProducts().getProduct();
        int size = lp.size();
        ProductType retour = null;
        for (int i = 0; i < size; i++) {
            if (lp.get(i).getId() == id) {
                retour = lp.get(i);
            }
        }
        return retour;
    }

    private PallierType findManagerByName(World world, String name) {
        List<PallierType> lp = world.getManagers().getPallier();
        int size = lp.size();
        PallierType retour = null;
        for (int i = 0; i < size; i++) {
            if (lp.get(i).getName().equals(name)) {
                retour = lp.get(i);
            }
        }
        return retour;
    }

    private PallierType findUpgradeByName(World world, PallierType pallier) {
        ProductType produit = findProductById(world, pallier.getIdcible());
        List<PallierType> lp = produit.getPalliers().getPallier();
        PallierType retour = null;
        for (int i = 0; i < lp.size(); i++) {
            if (lp.get(i).getName().equals(pallier.getName())) {
                retour = lp.get(i);
            }
        }
        return retour;
    }

    private PallierType findAngelUpgradeByName(World world, PallierType pallier) {
        List<PallierType> lp = world.getAllunlocks().getPallier();
        PallierType retour = null;
        for (int i = 0; i < lp.size(); i++) {
            if (lp.get(i).getName().equals(pallier.getName())) {
                retour = lp.get(i);
            }
        }
        return retour;
    }

    private World getWorld(String username) {
        World monde = readWorldFromXml(username);
        majScore(monde);
        saveWordlToXml(monde);
        return monde;
    }

    private void majScore(World monde) {
        long dif = monde.getLastupdate() - System.currentTimeMillis();
        List<ProductType> lp = monde.getProducts().getProduct();
        int size = lp.size();
        for (int i = 0; i > size; i++) {
            ProductType produit = lp.get(i);
            if (produit.isManagerUnlocked()) {
                int nb = (int) ((dif * 1000) % produit.getVitesse());
                monde.setScore(monde.getScore() + produit.getRevenu() * nb * (1 + monde.getActiveangels() * monde.getAngelbonus() / 100));
                monde.setMoney(monde.getMoney() + produit.getRevenu() * nb * (1 + monde.getActiveangels() * monde.getAngelbonus() / 100));
                produit.setTimeleft((dif * 1000) - (nb * produit.getVitesse()));
            } else if (produit.getTimeleft() != 0 && produit.getTimeleft() < dif) {
                monde.setScore(monde.getScore() + produit.getRevenu() * (1 + monde.getActiveangels() * monde.getAngelbonus() / 100));
                monde.setMoney(monde.getMoney() + produit.getRevenu() * (1 + monde.getActiveangels() * monde.getAngelbonus() / 100));
            } else {
                produit.setTimeleft(produit.getTimeleft() - dif * 1000);
            }
        }
        monde.setLastupdate(System.currentTimeMillis());
    }

    private void verificationUnlocks(World monde, ProductType produit) {
        List<PallierType> lpa = produit.getPalliers().getPallier();
        // on parcours la liste des unlocks pour le produit
        for (int i = 0; i < lpa.size(); i++) {
            // si on a assez pour unlock
            if (produit.getQuantite() >= lpa.get(i).getSeuil()) {
                // si ce n'etait aps deja unlock
                if (!lpa.get(i).isUnlocked()) {
                    String type = lpa.get(i).getTyperatio().value();
                    //si c est de type gain
                    if (type.equals("gain")) {
                        produit.setRevenu(produit.getRevenu() * lpa.get(i).getRatio());
                    } //si c est de type vitesse
                    else if (type.equals("vitesse")) {
                        produit.setVitesse((int) (produit.getVitesse() * lpa.get(i).getRatio()));
                        produit.setTimeleft((long) (produit.getTimeleft() / lpa.get(i).getRatio()));
                    }
                    // on unlock
                    lpa.get(i).setUnlocked(true);
                }
            }
        }
        List<ProductType> lp = monde.getProducts().getProduct();
        int allnb = 0;
        //on parcours nos produit pour savoir quel est notre seuil minimal
        for (int j = 0; j < lp.size(); j++) {
            if (j == 0) {
                allnb = lp.get(j).getQuantite();
            }
            if (lp.get(j).getQuantite() < allnb) {
                allnb = lp.get(j).getQuantite();
            }
        }
        // on parcours maintenant la liste des allunlocks
        List<PallierType> lau = monde.getAllunlocks().getPallier();
        for (int k = 0; k < lau.size(); k++) {
            // si on a assez pour unlock
            if (lau.get(k).getSeuil() <= allnb) {
                // si ce n etait pas deja unlock
                if (!lau.get(k).isUnlocked()) {
                    String type = lau.get(k).getTyperatio().value();
                    // si cest un gain
                    if (type.equals("gain")) {
                        for (int l = 0; l < lpa.size(); l++) {
                            lp.get(l).setRevenu(lp.get(l).getRevenu() * lau.get(k).getRatio());
                        }
                        produit.setRevenu(produit.getRevenu() * lau.get(k).getRatio());
                        // si c est une vitesse
                    } else if (type.equals("vitesse")) {
                        for (int l = 0; l < lpa.size(); l++) {
                            lp.get(l).setVitesse((int) (lp.get(l).getVitesse() * lau.get(k).getRatio()));
                            lp.get(l).setTimeleft((long) (lp.get(l).getTimeleft() / lau.get(k).getRatio()));
                        }
                    }
                    lau.get(k).setUnlocked(true);
                }
            }
        }
    }

    public void resetWorld(String username) {
        World monde = getWorld(username);
        double score = monde.getScore();
        double totala = monde.getTotalangels();
        double angel = monde.getActiveangels();
        monde = readWorldFromXml();
        monde.setScore(score);
        monde.setTotalangels(totala);
        monde.setActiveangels(angel);
        saveWordlToXml(username, monde);
    }
}
