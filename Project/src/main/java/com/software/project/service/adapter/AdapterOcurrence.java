package com.software.project.service.adapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;




import java.util.UUID;

import org.hibernate.ejb.criteria.expression.function.TrimFunction;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;








import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.software.project.entities.Ocorrencia;
import com.software.project.entities.User;
import com.software.project.entities.adapter.Attributes;
import com.software.project.entities.adapter.Entity;
import com.software.project.entities.adapter.Metadata;

public class AdapterOcurrence {
	
	public static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
    public static Entity toEntity(Ocorrencia o) {
    	Entity e = new Entity();
    	List<Attributes> aList = new ArrayList<Attributes>();
    	aList.add(new Attributes("title", "String", o.getTitle(), null));
    	
    	List<Metadata> metadatas = new ArrayList<Metadata>();
		metadatas.add(new Metadata("location", "String", "WGS84"));
		String  gpsCoords = o.getLat()+", "+o.getLng();
		aList.add(new Attributes("GPSCoord","coords",gpsCoords,metadatas));
		
    	/*aList.add(new Attributes("lat", "String", o.getLat()));
    	aList.add(new Attributes("lng", "String", o.getLng()));*/
    	aList.add(new Attributes("endereco", "String", o.getEndereco(), null));
    	aList.add(new Attributes("dataOcorrencia", "String", df.format(o.getDataOcorrencia()), null)); 
    	aList.add(new Attributes("userId", "String", String.valueOf(o.getUser().getId()), null));   
    	
    	e.setId(String.valueOf(o.getIdOcorrencia()));
    	e.setType("Ocurrence");
    	e.setAttributes(aList);

		return e;
		
	}
    
    
    public static Entity parseEntity(String s) {
    	Entity e = new Entity(); 
    	Gson gson = new Gson();
    	List<Attributes> lAtt = new ArrayList<Attributes>();
    	try {
    		JSONParser jsonParser = new JSONParser();
    		JSONObject jsonObject = (JSONObject) jsonParser.parse(s);
    		JSONObject structure = (JSONObject) jsonObject.get("contextElement");
    		Type listType = new TypeToken<ArrayList<Attributes>>() {}.getType();
            lAtt =  gson.fromJson(structure.get("attributes").toString(), listType);
     
            e.setId(structure.get("id").toString());
            e.setType(structure.get("type").toString());
            e.setAttributes(lAtt);
            
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
		return e;

    }
    
    public static Ocorrencia toOcurrence(Entity e) throws ParseException {
    	Ocorrencia o = new Ocorrencia();
    	o.setIdOcorrencia(Long.parseLong(e.getId()));
    	for (Attributes att : e.getAttributes()) {
    		switch (att.getName()) {
    		case "title":
				o.setTitle(att.getValue());
				break;
			case "GPSCoord":
				String[] tokensVal = att.getValue().split(",");
				o.setLat(tokensVal[0].trim());
				o.setLng(tokensVal[1].trim());
				break;
			case "endereco":
				o.setEndereco(att.getValue());
				break;
			case "dataOcorrencia":
				Date date = null;
				date = df.parse(att.getValue());
				o.setDataOcorrencia(date);
				break;	
			case "userId":
				User u = new User();
				u.setId(Long.parseLong(att.getValue()));
				o.setUser(u);
				break;	
			}
			
		}
		return o;
		
	}
    
    public static List<Entity> parseListEntity(String s) throws Exception {
		List<Entity> listEntity = new ArrayList<Entity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(s.trim());
		JSONArray lang = (JSONArray) jsonObject.get("contextResponses");
		if(lang != null){
			Iterator i = lang.iterator();
			// take each value from the json array separately
		    while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				if(innerObj != null)
				listEntity.add(AdapterOcurrence.parseEntity(innerObj.toString()));
			}
		}
		return listEntity;

    }
    
    public static long generateUniqueId() {      
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;        
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Long.valueOf(str);
    }
}
