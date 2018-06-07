package com.michaelsoft.odwek;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ApiController {

	public static ODUtil util;
	public static final String SERVER = "10.1.1.1";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
 
	@GetMapping("/query")
    public ResponseEntity<?> query(@RequestParam(value="folderName") String folderName, @RequestParam(value="criteria") String criteria, @RequestParam(value="value") String value, HttpServletRequest request) {
		
		ArrayList<ODQueryResult> results=new ArrayList<>();
		
		if(util==null)
			util=new ODUtil();
		
		if(!util.isConnected())
		{
			util.connect(SERVER,USER,PASSWORD);
		}
		
		if(util.isConnected())
		{
			results=util.queryFolder(folderName, criteria, value);     	        
		}
        
		return ResponseEntity.ok(results.toArray());
 
    }
	
	
	@ResponseBody
    @RequestMapping(value="/document", method=RequestMethod.GET, produces={"text/plain", "application/*"} )
	public ResponseEntity<?> document(@RequestParam(value="folderName") String folderName, @RequestParam(value="docId") String docId, HttpServletRequest request) {
		
		byte[] result =new byte[0];
		if(util==null)
			util=new ODUtil();
		
		if(!util.isConnected())
		{
			util.connect(SERVER,USER,PASSWORD);
		}
		
		if(util.isConnected())
		{
			result = util.getDocument(folderName, docId);     	
			
		}
        
		return ResponseEntity.ok()
				 .contentLength(result.length)
				 .contentType(MediaType.APPLICATION_OCTET_STREAM)
				 .body(result);
 
    }
}
