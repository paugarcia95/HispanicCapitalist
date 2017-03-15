/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XML;

import generated.World;
import java.io.File;
import java.io.InputStream;
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

}
