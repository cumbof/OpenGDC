/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class XMLNode {
    
    private boolean root = false;
    private String label = "";
    private HashMap<String, String> attributes = new HashMap<>();
    private ArrayList<XMLNode> childs = new ArrayList<>();
    private XMLNode parent = null;
    
    public XMLNode() { }
    
    public void setRoot() {
        this.root = true;
    }
    
    public boolean isRoot() {
        return this.root;
    }
    
    public void setLabel(String nodename) {
        this.label = nodename;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
    
    public HashMap<String, String> getAttributes() {
        return this.attributes;
    }
    
    public void addChild(XMLNode node) {
        this.childs.add(node);
    }
    
    public ArrayList<XMLNode> getChilds() {
        return this.childs;
    }
    
    public boolean hasChilds() {
        return this.childs.size() > 0;
    }
    
    public void setParent(XMLNode node) {
        this.parent = node;
    }
    
    public XMLNode getParent() {
        return this.parent;
    }
    
}
