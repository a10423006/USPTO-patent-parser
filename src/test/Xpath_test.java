package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Xpath_test {
	 public static void main(String args[]){
		 try {
			 //excel
		      WritableWorkbook workbook = Workbook.createWorkbook(new File("E:\\test_en_test.xls"));
		      // 添加一個工作表  
		      WritableSheet sheet = workbook.createSheet("test", 0);
		      sheet.addCell(new Label(0, 0, "Title"));
		      sheet.addCell(new Label(1, 0, "Abstract"));
		      sheet.addCell(new Label(2, 0, "IPC"));
		      //sheet.addCell(new Label(2, 0, "CPC"));
		      sheet.addCell(new Label(3, 0, "Description"));     
			 //讀取整個資料夾
			 File folder = new File("C:\\Users\\patent\\eclipse-workspace\\test\\src\\test\\xml");
			 String[] list = folder.list();
			 
			 //讀資料夾中所有檔案的檔名
			 for (int i = 0; i < list.length; i++) {
				String filename = list[i];
				System.out.println("File: " + filename);
				 
				//建立file
				File file = new File("C:\\Users\\patent\\eclipse-workspace\\test\\src\\test\\xml\\" + list[i]);
				
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(reader);
				
				//寫入新檔案
				File writefile = new File("C:\\Users\\patent\\eclipse-workspace\\test\\src\\test\\xml\\copy_" + list[i]);
				writefile.createNewFile(); // 建立新檔案
				BufferedWriter out = new BufferedWriter(new FileWriter(writefile));
				
				String content = "";
				//寫入
				out.write("<root-patents>\n");
				while((content = br.readLine()) != null){
					if(!content.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") && !content.contains("<!DOCTYPE")) {
						out.write(content + "\n");
					}
				}
				out.write("\n</root-patents>");
				out.flush();
				out.close();
		        
		        file = new File(writefile.getPath());
		        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();  
		        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();  
		        Document document = documentBuilder.parse(file); 
		        document.getDocumentElement().normalize();  
		  
		        XPath xPath = XPathFactory.newInstance().newXPath();  
				          
				//root
				String exprsion = "//us-patent-application";
				  
				NodeList nodes = (NodeList) xPath.compile(exprsion).evaluate(document, XPathConstants.NODESET);
				  
				for (int j = 0; j < nodes.getLength(); j++) {
			      Node item = nodes.item(j);    
			
			      Element element = (Element) item;  
			      System.out.println("<---"+ (j+1) +"--->");
			        
			      //title(標籤 invention-title)
			      System.out.println("[Title of the invention]");
			      String title = "";
			      if(element.getElementsByTagName("invention-title").item(0) != null) {
			    	  title = element.getElementsByTagName("invention-title").item(0).getTextContent();
				      System.out.println("title:" + title);
				      System.out.println("");
			      }
			      
			      //ipc
			      NodeList ipc_node = null;
			      String ipc = "";
			      if(element.getElementsByTagName("classifications-ipcr").item(0) != null) {
				      ipc_node = element.getElementsByTagName("classification-ipcr");
				      Node ipc_item = ipc_node.item(0);
				      Element ipc_element = (Element) ipc_item;
				      String ipc_section = ipc_element.getElementsByTagName("section").item(0).getTextContent();
				      String ipc_class = ipc_element.getElementsByTagName("class").item(0).getTextContent();
				      String ipc_subclass = ipc_element.getElementsByTagName("subclass").item(0).getTextContent();
				      String ipc_group = ipc_element.getElementsByTagName("main-group").item(0).getTextContent();
				      String ipc_subgroup = ipc_element.getElementsByTagName("subgroup").item(0).getTextContent();
				      ipc = ipc_section + ipc_class + ipc_subclass + ipc_group + "/" + ipc_subgroup;
				      System.out.println("IPC:" + ipc);
				      System.out.println("");
			      }
			      
			      //cpc
			    /* NodeList cpc_node = null;
			      String cpc = "";
			      if(element.getElementsByTagName("classifications-cpc").item(0) != null) {
			    	  cpc_node = element.getElementsByTagName("classifications-cpc");
				      Node cpc_item = cpc_node.item(0);
				      Element cpc_element = (Element) cpc_item;
				      String cpc_section = cpc_element.getElementsByTagName("section").item(0).getTextContent();
				      String cpc_class = cpc_element.getElementsByTagName("class").item(0).getTextContent();
				      String cpc_subclass = cpc_element.getElementsByTagName("subclass").item(0).getTextContent();
				      String cpc_group = cpc_element.getElementsByTagName("main-group").item(0).getTextContent();
				      String cpc_subgroup = cpc_element.getElementsByTagName("subgroup").item(0).getTextContent();
				      cpc = cpc_section + cpc_class + cpc_subclass + cpc_group + "/" + cpc_subgroup;
				      System.out.println("CPC:" + cpc);
				      System.out.println("");
			      }*/
			      
			      //abstract
			      System.out.println("[Abstract]");
			      String ab = element.getElementsByTagName("abstract").item(0).getTextContent();
			      ab = ab.replaceAll("\t|\r|\n", "");	//刪除換行
			      ab = ab.replaceAll("\\s+", " ");	//刪除兩個以上的空格
			      System.out.println("abstract:" + ab);
			      System.out.println("");
			      
			      //description(標籤  description)
			      System.out.println("[Description]");
			      NodeList description_node = element.getElementsByTagName("description");
			      Node description_item = description_node.item(0);
			      Element description_element = (Element) description_item;
			      
			      String description = element.getElementsByTagName("description").item(0).getTextContent();
			      String des = description;
			      String drawings = "";
			      String tables = "";
			      
			      //description_drawings(圖片描述)
			      if(description_element.getElementsByTagName("description-of-drawings").item(0) != null) {
			    	  drawings = description_element.getElementsByTagName("description-of-drawings").item(0).getTextContent();
			    	  des = (String) description.subSequence(0, description.indexOf(drawings));
			      }
			      
			      //tables(表格)
			      if(description_element.getElementsByTagName("tables").item(0) != null) {
			    	  tables = description_element.getElementsByTagName("tables").item(0).getTextContent();
			    	  des = (String) description.subSequence(0, description.indexOf(tables));
			      }
			      
			      des = des.replaceAll("\t|\r|\n", "");	//刪除換行
			      des = des.replaceAll("\\s+", " ");	//刪除兩個以上的空格
			      System.out.println("description:" + des);
			      System.out.println("");
			      
			      //excel 寫入
			      Label title_la = new Label(0, j+1, title);
			      Label abstract_la = new Label(1, j+1, ab);
			      Label ipc_la = new Label(2, j+1, ipc);
			      Label description_la = new Label(3, j+1, des);
			      
			      sheet.addCell(title_la);
			      sheet.addCell(ipc_la);
			      sheet.addCell(abstract_la);
			      sheet.addCell(description_la);
			      
			      
			      
				}
				System.out.println("<------------>");
				System.out.println("");
			 }
			 
			 workbook.write();
			 workbook.close();
	      } catch (Exception e) {  
	          e.printStackTrace();
	      } 
	 }
}
