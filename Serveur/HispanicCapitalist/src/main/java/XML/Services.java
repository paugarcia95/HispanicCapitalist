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
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author killian
 */
public class Services {

    public World readWorldFromXml() {
        World monde = null;
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u;
            u = cont.createUnmarshaller();
            InputStream input = getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            monde = (World) u.unmarshal(input);

        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        return monde;
    }

    public World readWorldFromXml(String pseudo) {
        World monde = null;
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u;
            u = cont.createUnmarshaller();
            InputStream input = getClass().getClassLoader().getResourceAsStream(pseudo + "WorldHispanic.xml");
            monde = (World) u.unmarshal(input);

        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        return monde;
    }

    public void saveWordlToXml(World world) {
        try {
            // InputStream input= getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            JAXBContext cont;
            cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            m.marshal(world, new File("WorldHispanic.xml"));
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveWordlToXml(String pseudo, World world) {
        try {
            // InputStream input= getClass().getClassLoader().getResourceAsStream("WorldHispanic.xml");
            JAXBContext cont;
            cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            m.marshal(world, new File(pseudo + "WorldHispanic.xml"));
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
            world.setMoney(argent - product.getCout() * qtchange);
            product.setQuantite(qtchange);
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
                monde.setScore(monde.getScore() + produit.getRevenu() * nb);
                produit.setTimeleft((dif * 1000) - (nb * produit.getVitesse()));
            } else if (produit.getTimeleft() != 0 && produit.getTimeleft() < dif) {
                monde.setScore(monde.getScore() + produit.getRevenu());
            } else {
                produit.setTimeleft(produit.getTimeleft() - dif);
            }
        }
        monde.setLastupdate(System.currentTimeMillis());
    }

    private void verificationUnlocks(World monde, ProductType produit) {
        List<PallierType> lpa = produit.getPalliers().getPallier();
        for (int i = 0; i < lpa.size(); i++) {
            if (produit.getQuantite() >= lpa.get(i).getSeuil()) {
                lpa.get(i).setUnlocked(true);
            }
        }
        List<ProductType> lp = monde.getProducts().getProduct();
        int allnb = 0;
        for (int j = 0; j < lp.size(); j++) {
            if (j == 0) {
                allnb = lp.get(j).getQuantite();
            }
            if (lp.get(j).getQuantite() < allnb) {
                allnb = lp.get(j).getQuantite();
            }
        }
        List<PallierType> lau = monde.getAllunlocks().getPallier();
        for(int k=0;k<lau.size();k++){
            if(lau.get(k).getSeuil()<=allnb){
                lau.get(k).setUnlocked(true);
            }
        }
    }
}