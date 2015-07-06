package com.microsoft.projectoxforddemo.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by yulw on 7/4/2015.
 */
public class PersonUtils {
    private static String PersonInfoFile = "person.xml";
    private static Person m_lastResolve = null;

    public static void setPerson(String name, String group) {

    }

    public static void newUser(String personName, String personGroup) {
        String path = Environment.getExternalStorageDirectory() + ImageUtils.getConfig().getDataDiraName();
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();

        File personInfoFile = new File(path, PersonInfoFile);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("ProjectOxfordConfigure");
            doc.appendChild(root);
            //Add a person filed
            Element newPerson = doc.createElement("Person");
            Attr personAttrName = doc.createAttribute("Name");
            Attr personAttrGroup = doc.createAttribute("Group");
            personAttrName.setValue(personName);
            personAttrGroup.setValue(personGroup);
            newPerson.setAttributeNode(personAttrName);
            newPerson.setAttributeNode(personAttrGroup);
            root.appendChild(newPerson);

            StreamResult result = new StreamResult(personInfoFile);

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            StreamResult consoleResult =
                    new StreamResult(System.out);
            transformer.transform(source, consoleResult);

            //save altogether with a capture
            ImageUtils.saveLastCapture(personName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNewPerson() {
        return resolveInfo() == null;
    }

    static Person resolveInfo() {
        String path = Environment.getExternalStorageDirectory() + ImageUtils.getConfig().getDataDiraName() + "/" + PersonInfoFile;
        File info = new File(path);
        if (!info.isFile()) {
            return null;
        }
        List<Person> personsArr = new ArrayList<Person>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(info);
            Element root = doc.getDocumentElement();
            NodeList persons = root.getElementsByTagName("Person");
            if (persons == null) {
                Log.d("resolve info ", "Person Not Found");
                return null;
            }
            for (int i = 0; i < persons.getLength(); i++) {
                Node item = persons.item(i);
                if (item.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Person p = new Person();
                Element ele = (Element) item;
                if (!ele.hasAttribute("Name")) {
                    Log.d("resolve info ", "Name Not Found");
                    return null;
                }
                p.m_name = ele.getAttribute("Name");
                if (!ele.hasAttribute("Group")) {
                    Log.d("resolve info ", "Name Not Found");
                    return null;
                }
                p.m_group = ele.getAttribute("Group");
                personsArr.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (personsArr.size() == 0)
            return null;
        m_lastResolve = personsArr.get(0);
        if(m_lastResolve!=null)
            Log.d("Obtained Person Name:",m_lastResolve.m_name);
        else
            Log.d("Obtained Person Name:","NULL");
        return m_lastResolve;
    }

    public static void removeData() {
        String configPath= Environment.getExternalStorageDirectory() + ImageUtils.getConfig().getDataDiraName() + "/" + PersonInfoFile;
        File config=new File(configPath);
        if(config.isFile())
            config.delete();
        if(m_lastResolve==null)
            resolveInfo();
        String imagePath= Environment.getExternalStorageDirectory() + ImageUtils.getConfig().getDataDiraName() + "/" +m_lastResolve.m_name+".bmp";
        File image=new File(imagePath);
        if(image.isFile())
            image.delete();
    }
    public static Bitmap getMasterBitMap() {
        if(m_lastResolve==null)
            resolveInfo();
        Bitmap bitmap=null;
        bitmap=ImageUtils.loadImage(m_lastResolve.m_name);
        return bitmap;
    }
}
class Person extends com.microsoft.projectoxford.face.contract.Person
{
    public String m_name = "Test";
    public String m_group = "Microsoft";
    Person(String name, String group) {
        m_name = name;
        m_group = group;
    }
    Person() {

    }
}
