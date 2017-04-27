package opengdc.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLReader {

	public static String getEntrezFromHugo(String hugoXmlFile) {
		String entrez = "";
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			File file = new File(hugoXmlFile);
			Document doc = docBuilder.parse(file);

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList docNodes = doc.getElementsByTagName("doc");

				Node docNode = docNodes.item(0);
				//System.out.println("Node Length: "+docNodes.getLength());

				if (docNodes.getLength() > 0) {
					for (int i = 0; i < docNode.getChildNodes().getLength(); i++) {
						Node child = docNode.getChildNodes().item(i);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element elem = (Element) child;
							//System.err.println("-> " + elem.getNodeName());
							//System.out.println("NAME: " + elem.getNodeName() + "\tVALUE: " + elem.getTextContent() + "\tATT_NAME: " + elem.getAttributes().getNamedItem("entrez_id").toString());

							if (elem.getAttributes().item(0).toString().equals("name=\"entrez_id\"")) {
								//System.out.println("ATT: " + elem.getAttributes().item(0));
								entrez = elem.getTextContent();
								//System.out.println("ENTREZ: " + entrez);
								//  file.delete();

								break;
							}   
						}
					}

				
			} else {
				entrez = "";
			}


		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return entrez;
	}
	
	public static boolean isXMLLike(String inXMLStr) {

        boolean retBool = false;
        Pattern pattern;
        Matcher matcher;

        // REGULAR EXPRESSION TO SEE IF IT AT LEAST STARTS AND ENDS
        // WITH THE SAME ELEMENT
        final String XML_PATTERN_STR = "<(\\S+?)(.*?)>(.*?)</\\1>";



        // IF WE HAVE A STRING
        if (inXMLStr != null && inXMLStr.trim().length() > 0) {

            // IF WE EVEN RESEMBLE XML
            if (inXMLStr.trim().startsWith("<")) {

                pattern = Pattern.compile(XML_PATTERN_STR,
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

                // RETURN TRUE IF IT HAS PASSED BOTH TESTS
                matcher = pattern.matcher(inXMLStr);
                retBool = matcher.matches();
            }
        // ELSE WE ARE FALSE
        }

        return retBool;
    }

}
