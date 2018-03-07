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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import opengdc.Settings;
import opengdc.parser.BioParser;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author fabio
 */
public class MetadataHandler {
    
    public static final String __OPENGDCSEP__ = "__opengdcsep__";
    
    // for createMap function only
    private static int keyPrefixCounterForUniqueness = 0;
    // cast to HashMap<String, Object>
    private static Object createMap(Node source, boolean restartKeyPrefixCounter) {
        if (restartKeyPrefixCounter)
            keyPrefixCounterForUniqueness = 0;
        HashMap<String, Object> tmpMap = new HashMap<>();
        try {
            int childs = source.getChildNodes().getLength();        
            if (childs == 0) {
                if (source.getTextContent().trim().equals(""))
                    return "NA";
                return source.getTextContent();
            }
            else {
                HashMap<String, Object> dataTmp = new HashMap<>();
                for (int i=0; i<childs; i++) {
                    Node child = source.getChildNodes().item(i);
                    Object child_data = createMap(child, false);
                    if (child.getNodeName().toLowerCase().trim().contains("#text") && childs<=1) {
                        return (String)child_data;
                    }
                    else {
                        if (!child.getNodeName().toLowerCase().trim().contains("#text")) {
                            dataTmp.put(keyPrefixCounterForUniqueness + "_" + child.getNodeName(), child_data);
                            keyPrefixCounterForUniqueness++;
                        }
                    }
                }
                tmpMap.put(keyPrefixCounterForUniqueness + "_" + source.getNodeName(), dataTmp);
                keyPrefixCounterForUniqueness++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tmpMap;
    }
    
    /*private static void printMap(Object map, int level) {
        String level_tab = "";
        for (int i=0; i<level; i++)
            level_tab += "\t";
        
        HashMap<String, Object> hash_map = (HashMap<String, Object>)map;
        for (String k: hash_map.keySet()) {
            if (hash_map.get(k) instanceof HashMap) {
                HashMap<String, Object> values = (HashMap<String, Object>)hash_map.get(k);
                System.err.println(level_tab + k);
                if (!values.isEmpty())
                    printMap(values, level+1);
            }
            else if (hash_map.get(k) instanceof String) {
                String value = (String)hash_map.get(k);
                System.err.println(level_tab + k + "\t" + value);
            }
        }
    }*/
    
    public static HashMap<String, Object> getXMLMap(String file_path) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(file_path));
            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            NodeList roots = doc.getChildNodes();
            //System.err.println(roots.getLength());

            for (int i = 0; i < roots.getLength(); i++) {
                Node node = roots.item(i);
                result = (HashMap<String, Object>)createMap(node, true);
                break;
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (ParserConfigurationException | IOException | DOMException t) {
            t.printStackTrace();
        }

        return result;
    }
    
    public static HashMap<String, HashMap<String, String>> getTSVMap(String file_path, String indexBy) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        
        try {
            InputStream fstream = new FileInputStream(file_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            int line_count = 0;
            int index_position = 0;
            String[] header = null;
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    if (line_count == 0) { // header
                        header = line_split;
                        for (int i=0; i<header.length; i++) {
                            if (header[i].toLowerCase().trim().equals(indexBy)) {
                                index_position = i;
                                break;
                            }
                        }
                    }
                    else { // content
                        String key = line_split[index_position];
                        HashMap<String, String> values = new HashMap<>();
                        for (int i=0; i<line_split.length; i++) {
                            if (i != index_position)
                                values.put(header[i], line_split[i]);
                        }
                        result.put(key, values);
                    }
                    line_count++;
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // https://gist.github.com/madan712/3912272
    public static HashMap<String, HashMap<String, String>> getXLSXMap(String file_path, String indexBy) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        
        try {
            InputStream ExcelFileToRead = new FileInputStream(file_path);
            XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

            int sheets = wb.getNumberOfSheets();
            for (int sheet_index=0; sheet_index<sheets; sheet_index++) {
                XSSFSheet sheet = wb.getSheetAt(sheet_index);
                
                String sheet_name = sheet.getSheetName();
                if (!sheet_name.toLowerCase().trim().contains("criteria") &&
                    !sheet_name.toLowerCase().trim().contains("original")) { // skip sheets
                    XSSFRow row; 
                    XSSFCell cell;
                    Iterator rows = sheet.rowIterator();

                    //String criteria_str = "";
                    int header_rows = 1;
                    int current_row = 0;

                    ArrayList<String> header = new ArrayList<>();
                    int indexBy_position = 0;
                                        
                    while (rows.hasNext()) {
                        row = (XSSFRow)rows.next();
                                                
                        Iterator cells = row.cellIterator();
                        //String row_str = "";
                        HashMap<String, String> content_map = new HashMap<>();
                        String indexBy_value = "";

                        int cell_index = 0;
                        while (cells.hasNext()) {
                            cell = (XSSFCell)cells.next();
                            
                            String cellValue = "";
                            if (cell.getCellTypeEnum() == CellType.STRING)
                                cellValue = cell.getStringCellValue();
                            else if (cell.getCellTypeEnum() == CellType.NUMERIC)
                                cellValue = String.valueOf(cell.getNumericCellValue());
                            
                            if (!cellValue.trim().equals("")) {
                            
                                //String cellValue = cell.getRawValue();
                                //System.err.println(cellValue);

                                // cell is in merged region?
                                int cellIterations = 1;
                                List<CellRangeAddress> mergedRegionsInSheet = sheet.getMergedRegions();
                                if (mergedRegionsInSheet.size() > 0)
                                    header_rows = 2;
                                for (CellRangeAddress mergedRegion: mergedRegionsInSheet) {
                                    if (mergedRegion.isInRange(cell)) {
                                        cellIterations = mergedRegion.getNumberOfCells();
                                        //if (mergedRegion.containsRow(current_row-1))
                                            //cellIterations = cellIterations/2;
                                        cellIterations = cellIterations / (((mergedRegion.getLastRow()+1) - (mergedRegion.getFirstRow()+1)) + 1);
                                        break;
                                    }
                                }
                                
                                if (current_row < header_rows) { //header
                                    for (int cell_iter=0; cell_iter<cellIterations; cell_iter++) {
                                        String prefix_header = "";
                                        try { prefix_header = header.get(cell_index + cell_iter); }
                                        catch (Exception e) { /* first line - prefix_header does not yet exist */ };
                                        String suffix_header = cellValue;

                                        String header_str = "";
                                        if (prefix_header.trim().equals("") && !suffix_header.trim().equals(""))
                                            header_str = suffix_header;
                                        else if (!prefix_header.trim().equals("") && suffix_header.trim().equals(""))
                                            header_str = prefix_header;
                                        else if (prefix_header.trim().equals(suffix_header.trim()))
                                            header_str = prefix_header;
                                        else if (!prefix_header.trim().equals("") && !suffix_header.trim().equals(""))
                                            header_str = prefix_header+__OPENGDCSEP__+suffix_header;

                                        //System.err.println(header_str + "\t" + cellIterations);

                                        if (header_str.toLowerCase().trim().equals(indexBy))
                                            indexBy_position = cell_index + cell_iter;

                                        try {
                                            header.remove(cell_index + cell_iter);
                                            header.add(cell_index + cell_iter, header_str);
                                            /*if (current_row == 1)
                                                System.err.println(header_str);*/
                                        }
                                        catch (Exception e) {
                                            header.add(header_str);
                                        }

                                        //cell_index++;
                                        cell_index += cellIterations;
                                    }
                                }
                                else { // content
                                    content_map.put(header.get(cell_index), cellValue);
                                    if (cell_index == indexBy_position)
                                        indexBy_value = cellValue;
                                    cell_index++;
                                }
                            }
                            else
                                cell_index++;
                        }
                        
                        if (current_row >= header_rows) { // content
                            if (!indexBy_value.trim().equals(""))
                                result.put(indexBy_value, content_map);
                        }
                        //criteria_str += row_str;
                        current_row++;
                        //System.out.println();
                    }

                    /*if (!criteria_str.trim().equals("")) {
                        HashMap<String, String> criteria_sheet = new HashMap<>();
                        criteria_sheet.put("criteria", criteria_str);
                        result.put(sheet_name, criteria_sheet);
                    }*/
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static HashMap<String, String> extractAdminInfo(HashMap<String, Object> xml_map) {
        HashMap<String, String> admin_map = new HashMap<>();
        HashMap<String, String> dataMap = getDataMap(xml_map, null);
        for (String key: dataMap.keySet()){
            if (key.contains("admin"))
                admin_map.put(key, dataMap.get(key));
        }
        return admin_map;
    }
    
    public static HashMap<String, String> getDataMap(HashMap<String, Object> map, HashMap<String, String> dataMap) {
        if (dataMap == null)
            dataMap = new HashMap<>();
        for (String k: map.keySet()) {
            if (map.get(k) instanceof HashMap) {
                HashMap<String, Object> values = (HashMap<String, Object>)map.get(k);
                if (!values.isEmpty())
                    getDataMap(values, dataMap);
            }
            else if (map.get(k) instanceof String) {
                String value = (String)map.get(k);
                dataMap.put(k, value);
            }
        }
        return dataMap;
    }
    
    public static String findKey(HashMap<String, String> map, String searchCondition, String str) {
        String value = "";
        if (!map.isEmpty()) {
            for (String k: map.keySet()) {
                if (searchCondition.toLowerCase().trim().equals("endswith")) {
                    if (k.toLowerCase().trim().endsWith(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("startswith")) {
                    if (getAttributeFromKey(k).toLowerCase().trim().startsWith(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("contains")) {
                    if (k.toLowerCase().trim().contains(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("equals")) {
                    if (getAttributeFromKey(k).toLowerCase().trim().equals(str)) {
                        value = map.get(k);
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    private static ArrayList<XMLNode> aliquotNodes = new ArrayList<>();
    public static void searchForAliquots(XMLNode root) {
        if (root.getLabel().toLowerCase().trim().endsWith("bio:aliquot") && root.getAttributes().size()>0) {
            aliquotNodes.add(root);
            //System.err.println(root.getLabel() + "\t" + "attributes: " + root.getAttributes().size());
        }
        else {
            if (root.hasChilds()) {
                for (XMLNode child: root.getChilds())
                    searchForAliquots(child);
            }
        }
    }
    public static ArrayList<XMLNode> getAliquotNodes() {
        return aliquotNodes;
    }
    public static void emptyAliquotNodes() {
        aliquotNodes = new ArrayList<>();
    }
    
    public static XMLNode convertMapToIndexedTree(HashMap<String, Object> map, XMLNode root) {
        for (String k: map.keySet()) {
            if (map.get(k) instanceof HashMap) {
                XMLNode child = new XMLNode();
                child.setLabel(k);
                child.setParent(root);
                root.addChild(convertMapToIndexedTree((HashMap<String, Object>)map.get(k), child));
            }
            else if (map.get(k) instanceof String) {
                String value = (String)map.get(k);
                root.addAttribute(k, value);
            }
        }
        return root;
    }
    
    public static HashMap<String, String> extractParentMetadata(XMLNode node, HashMap<String, String> meta) {
        if (meta == null)
            meta = new HashMap<>();
        meta.putAll(node.getAttributes());
        
        if (node.getParent() == null)
            return meta;
        return extractParentMetadata(node.getParent(), meta);
    }
    
    public static String getAttributeFromKey(String key) {
        String[] key_split = key.split("_");
        return key.substring(key_split[0].length()+1, key.length());
    }
    
    // the attributes in this methods are all required 
    public static HashMap<String, HashMap<String, Object>> getAdditionalManuallyCuratedAttributes(String program, String disease, String dataType, String format, String aliquot_uuid, HashMap<String, String> biospecimen_attributes, HashMap<String, String> clinical_attributes, HashMap<String, String> manually_curated, String suffix_id) {
        String attributes_prefix = "manually_curated";
        String category_separator = "__";
        
        /******* tissue_status *******/
        // retrieve 'manually_curated__tissue_status' from 'biospecimen__bio__sample_type_id'
        HashMap<String, HashMap<String, Object>> additional_attributes = new HashMap<>();
        String tissue_id = "";
        for (String bio_attr: biospecimen_attributes.keySet()) {
            if (bio_attr.trim().toLowerCase().contains("sample_type_id")) {
                tissue_id = biospecimen_attributes.get(bio_attr);
                break;
            }
        }
        HashMap<String, Object> values = new HashMap<>();
        String tissue_status = "";
        if (!tissue_id.trim().equals(""))
            tissue_status = BioParser.getTissueStatus(tissue_id);
        values.put("value", tissue_status);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"tissue_status", values);
        
        /******* exp_data_bed_url *******/
        values = new HashMap<>();
        String expDataType = "";
        for (String man_attr: manually_curated.keySet()) {
            if (man_attr.trim().toLowerCase().contains("data_type")) {
                expDataType = manually_curated.get(man_attr);
                break;
            }
        }
        if (!expDataType.trim().equals("")) {
            if (GDCData.getGDCData2FTPFolderName().containsKey(expDataType.trim().toLowerCase())) {
                String opengdc_data_folder_name = GDCData.getGDCData2FTPFolderName().get(expDataType.trim().toLowerCase());
                expDataType = Settings.getOpenGDCFTPRepoProgram(program, false)+disease.trim().toLowerCase()+"/"+opengdc_data_folder_name+"/"+aliquot_uuid.trim().toLowerCase()+"-"+suffix_id+"."+Settings.getOpenGDCFTPConvertedDataFormat();
            }
            else expDataType = "";
        }
        values.put("value", expDataType);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"exp_data_"+Settings.getOpenGDCFTPConvertedDataFormat()+"_url", values);
        
        /******* opengdc_id *******/
        values = new HashMap<>();
        String opengdcId = "";
        opengdcId = aliquot_uuid.trim().toLowerCase()+"-"+suffix_id;
        values.put("value", opengdcId);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"opengdc_id", values);

        /******* data_format *******/
        values = new HashMap<>();
        String data_format = "";
        if (!expDataType.trim().equals(""))
            data_format = Settings.getOpenGDCFTPConvertedDataFormat().toUpperCase();
        values.put("value", data_format);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"data_format", values);
        
        /******* exp_metadata_url *******/
        values = new HashMap<>();
        values.put("value", Settings.getOpenGDCFTPRepoProgram(program, false)+disease.trim().toLowerCase()+"/"+GDCData.getGDCData2FTPFolderName().get(dataType.trim().toLowerCase())+"/"+aliquot_uuid.trim().toLowerCase()+"-"+suffix_id+"."+Settings.getOpenGDCFTPConvertedDataFormat()+"."+format);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"exp_metadata_url", values);
                
        return additional_attributes;
    }
    
    public static HashMap<String, HashMap<String, Boolean>> getAdditionalAttributes() {
        HashMap<String, HashMap<String, Boolean>> additionalAttributes = new HashMap<>();
        // <'attribute:string', 'required:boolean'>
        HashMap<String, Boolean> attributes = new HashMap<>();
        attributes.put("data_category", true);
        attributes.put("data_format", true);
        attributes.put("data_type", true);
        attributes.put("experimental_strategy", true);
        attributes.put("file_id", true);
        attributes.put("file_name", true);
        attributes.put("file_size", true);
        attributes.put("platform", true);
        attributes.put("analysis.analysis_id", true);
        attributes.put("analysis.workflow_link", true);
        attributes.put("analysis.workflow_type", true);
        attributes.put("cases.case_id", true);
        attributes.put("cases.disease_type", true);
        attributes.put("cases.primary_site", true);
        attributes.put("cases.demographic.year_of_birth", true);
        attributes.put("cases.project.program.program_id", true);
        attributes.put("cases.project.program.name", true);
                
        // other gdc attributes
        attributes.put("cases.submitter_id", true);
        //attributes.put("cases.samples.tumor_descriptor", false);
        //attributes.put("cases.samples.tissue_type", false);
        //attributes.put("cases.samples.sample_type", false);
        //attributes.put("cases.samples.submitter_id", false);
        //attributes.put("cases.samples.sample_id", false);
        //attributes.put("cases.samples.portions.analytes.aliquots.aliquot_id", false);
        //attributes.put("cases.samples.portions.analytes.aliquots.submitter_id", false);
        
        additionalAttributes.put("manually_curated", attributes);
        return additionalAttributes;
    }
    
    public static ArrayList<String> getAggregatedAdditionalAttributes() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("file_id");
        attributes.add("file_name");
        attributes.add("file_size");
        attributes.add("analysis.analysis_id");
        attributes.add("analysis.workflow_type");

        return attributes;
    }
    
    public static ArrayList<HashMap<String, String>> aggregateSameDataTypeInfo(ArrayList<HashMap<String, String>> files_info, ArrayList<String> aggregatedAdditionalAttributes) {
        HashMap<String, ArrayList<HashMap<String, String>>> aggregated = new HashMap<>();
        String platform_tmp = "";
        for (HashMap<String, String> file_info: files_info) {
            if (file_info != null) {
                if (file_info.containsKey("data_type")) {
                    String data_type = file_info.get("data_type");
                    ArrayList<HashMap<String, String>> values = new ArrayList<>();
                    if (aggregated.containsKey(data_type))
                        values = aggregated.get(data_type);
                    values.add(file_info);
                    aggregated.put(data_type, values);
                    if (data_type.trim().toLowerCase().equals("aligned reads")) {
                        if (file_info.containsKey("platform"))
                            platform_tmp = file_info.get("platform");
                    }
                }
            }
        }

        ArrayList<HashMap<String,String>> compressedMap = new ArrayList<>();
        for(String key: aggregated.keySet()){
            HashMap<String, String> tmp = new HashMap<>();
            ArrayList<HashMap<String, String>> mapList = aggregated.get(key);
            for (HashMap<String, String> map: mapList){
                for (String attribute: map.keySet()){
                    if (!tmp.containsKey(attribute))
                        tmp.put(attribute, map.get(attribute));
                    else {
                        if (aggregatedAdditionalAttributes.contains(attribute)) {
                            String value = tmp.get(attribute);
                            value = value + "," + map.get(attribute);
                            tmp.put(attribute, value);
                        }
                    }
                }
            }

            // platform control
            // if platform does not exist or is empty
            // set the same platform of the Aligned Reads
            if (!tmp.containsKey("platform"))
                tmp.put("platform", platform_tmp);
            else {
                if (tmp.get("platform").trim().equals(""))
                    tmp.put("platform", platform_tmp);
            }
            
            // populate compressedMap
            compressedMap.add(tmp);
        }

        return compressedMap;
    }
    
    /*public static void main(String[] args) {
        System.err.println("Biospecimen sample");
        String biospecimen_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-biospecimen/nationwidechildrens.org_biospecimen.TCGA-OR-A5J1.xml";
        HashMap<String, Object> xml_biospecimen_data = getXMLMap(biospecimen_xml_path);
        HashMap<String, String> biospecimen_data = getDataMap(xml_biospecimen_data, null);
        System.err.println("XML Data size: " + xml_biospecimen_data.size());
        System.err.println("Data size: " + biospecimen_data.size()+"\n");
        printMap(biospecimen_data, 0);
        
        System.err.println("\n#################################################\n");
        
        System.err.println("Clinical sample");
        String clinical_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-clinical/nationwidechildrens.org_clinical.TCGA-OR-A5J1.xml";
        HashMap<String, Object> xml_clinical_data = getXMLMap(clinical_xml_path);
        HashMap<String, String> clinical_data = getDataMap(xml_clinical_data, null);
        System.err.println("XML Data size: " + xml_clinical_data.size());
        System.err.println("Data size: " + clinical_data.size()+"\n");
        printMap(clinical_data, 0);
    }*/
    
}
