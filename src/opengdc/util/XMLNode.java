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
