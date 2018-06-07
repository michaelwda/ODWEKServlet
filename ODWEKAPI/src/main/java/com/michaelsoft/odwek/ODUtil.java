package com.michaelsoft.odwek;

import java.util.*;
import com.ibm.edms.od.*;

public class ODUtil
{
    ODServer odServer;
    Boolean connected;
    public ODUtil()
    {

    }
    
    public synchronized Boolean isConnected()
    {
    	if(odServer==null)
    		return false;
    	if(odServer.isServerTimedOut())
    		return false;
    	if(!connected)
    		return false;
    	if(!odServer.isInitialized())
    		return false;
    	
    	return true;
    }

    public synchronized void connect(String ip, String username, String password)
    {
        try
        {
            ODConfig odConfig = new ODConfig();
            odServer = new ODServer(odConfig );
            odServer.initialize(  "ODUtil.java" );
            odServer.setPort(1450);
            odServer.logon( ip, username, password);
        }
        catch ( ODException e )
        {
        	connected=false;
            System.out.println( "ODException: " + e );
            System.out.println( "   id = " + e.getErrorId( ) );
            System.out.println( "  msg = " + e.getErrorMsg( ) );
            e.printStackTrace( );
            return;
        }
        catch ( Exception e2 )
        {
        	connected=false;
            System.out.println( "exception: " + e2 );
            e2.printStackTrace( );
            return;
        }
        connected=true;
      
    }

    public synchronized ArrayList<ODQueryResult> queryFolder(String folderName, String criteria, String value)
    {
        ArrayList<ODQueryResult> results=new ArrayList<ODQueryResult>();
        try
        {
            ODFolder odFolder = odServer.openFolder(folderName);
            odFolder.setMaxHits(200);
            
            ODCriteria odCrit = odFolder.getCriteria(criteria);
            if(odCrit==null)
                return results;
            odCrit.setOperator(ODConstant.OPEqual);
            odCrit.setSearchValue(value);
            
            Vector<ODHit> hits = odFolder.search();
            String header="";
            if ( hits != null && hits.size() > 0 )
            {
                String[] display_crit = odFolder.getDisplayOrder();
                for(int j = 0; j < display_crit.length; j++ )
                {
                    header = header + display_crit[j];
                    if(j!=display_crit.length-1)
                        header+=",";
                }

                for (int j = 0; j < hits.size( ); j++ )
                {
                    ODQueryResult item=new ODQueryResult();
                    item.Headers=header;
                    ODHit odHit = (ODHit)hits.elementAt( j );
                    item.Data = "";
                    for (int k = 0; k < display_crit.length; k++ )
                    {
                        String hit_value = odHit.getDisplayValue( display_crit[k] );
                        String useable_value = ( hit_value.equals( "" ) ) ? "" : hit_value;
                        useable_value=useable_value.trim();
                        if(useable_value.contains(","))
                        {
                            useable_value = "\"" + useable_value + "\"";
                        }
                        item.Data +=useable_value;
                        if(k!=display_crit.length-1)
                            item.Data+=",";
                    }
                    item.DocType=getDocTypeString(odHit.getDocType());
                    item.MimeType=odHit.getMimeType();
                    item.DocId=odHit.getDocId();
                    item.DocId=item.DocId.replaceAll("\u0001", "|");
                    results.add(item);
                }
            }
        }
        catch ( ODException e )
        {
        
        	connected=false;
            System.out.println( "ODException: " + e );
            System.out.println( "   id = " + e.getErrorId( ) );
            System.out.println( "  msg = " + e.getErrorMsg( ) );
            e.printStackTrace( );
        }
        catch ( Exception e2 )
        {
        	connected=false;
            System.out.println( "exception: " + e2 );
            e2.printStackTrace( );
        }

        return results;
    }
     
    public synchronized byte[] getDocument(String folderName, String docId)
    {
    	byte[] data_from_folder = new byte[0];
    	
    	docId=docId.replaceAll("\\|", "\u0001");
    	try
        {
	    	ODFolder odFolder = odServer.openFolder(folderName);
			odFolder.setMaxHits(200);
			
			TcCallback callback = new TcCallback();
			odFolder.retrieve(new String[]{docId},callback);
			data_from_folder = callback.getData();
			if ( data_from_folder == null )
			{
				data_from_folder = new byte[0];
				System.out.println( "Callback function not called during ODFolder.retrieve" );
			}
			else
			{
				
				System.out.println( "  ODFolder.retrieve=" + data_from_folder.length );
			}
			//----------
	        // Cleanup
	        //----------
			odFolder.close();
        }
    	catch ( ODException e )
        {
        
        	connected=false;
            System.out.println( "ODException: " + e );
            System.out.println( "   id = " + e.getErrorId( ) );
            System.out.println( "  msg = " + e.getErrorMsg( ) );
            e.printStackTrace( );
        }
        catch ( Exception e2 )
        {
        	connected=false;
            System.out.println( "exception: " + e2 );
            e2.printStackTrace( );
        }
    	return data_from_folder;
    }
    public void disconnect()
    {
    	connected=false;
        try{
            if(odServer!=null)
            {
                odServer.logoff();
                odServer.terminate();
                odServer=null;
            }
        }
        catch ( ODException e )
        {
            System.out.println( "ODException: " + e );
            System.out.println( "   id = " + e.getErrorId( ) );
            System.out.println( "  msg = " + e.getErrorMsg( ) );
            e.printStackTrace( );
        }
        catch ( Exception e2 )
        {
            System.out.println( "exception: " + e2 );
            e2.printStackTrace( );
        }
    }



















    String getOperatorName( int oper )
    {
        String str;

        switch( oper )
        {
        case ODConstant.OPEqual:
            str = "Equals";
            break;
        case ODConstant.OPNotEqual:
            str = "Not Equal";
            break;
        case ODConstant.OPLessThan:
            str = "Less Than";
            break;
        case ODConstant.OPLessThanEqual:
            str = "Less Than or Equal";
            break;
        case ODConstant.OPGreaterThan:
            str = "Greater Than";
            break;
        case ODConstant.OPGreaterThanEqual:
            str = "Greather Than or Equal";
            break;
        case ODConstant.OPIn:
            str = "In";
            break;
        case ODConstant.OPNotIn:
            str = "Not In";
            break;
        case ODConstant.OPLike:
            str = "Like";
            break;
        case ODConstant.OPNotLike:
            str = "Not Like";
            break;
        case ODConstant.OPBetween:
            str = "Between";
            break;
        case ODConstant.OPNotBetween:
            str = "Not Between";
            break;
        default:
            str = "Operator unknown";
            break;
        }

        return str;
    }

    String getDocTypeString( char type )
    {
        String str;

        switch( type )
        {
        case ODConstant.FileTypeAFP:
            str = "AFP";
            break;
        case ODConstant.FileTypeBMP:
            str = "BMP";
            break;
        case ODConstant.FileTypeEMAIL:
            str = "EMAIL";
            break;
        case ODConstant.FileTypeGIF:
            str = "GIF";
            break;
        case ODConstant.FileTypeJFIF:
            str = "JFIF";
            break;
        case ODConstant.FileTypeLINE:
            str = "LINE";
            break;
        case ODConstant.FileTypeMETA:
            str = "META";
            break;
        case ODConstant.FileTypeNONE:
            str = "NONE";
            break;
        case ODConstant.FileTypePCX:
            str = "PCX";
            break;
        case ODConstant.FileTypePDF:
            str = "PDF";
            break;
        case ODConstant.FileTypePNG:
            str = "PNG";
            break;
        case  ODConstant.FileTypeSCS:
            str = "SCS";
            break;
        case ODConstant.FileTypeTIFF:
            str = "TIFF";
            break;
        case ODConstant.FileTypeUSRDEF:
            str = "USRDEF";
            break;
        default:
            str = "*** Invalid Doc Type ***";
            break;
        }

        return str;
    }

    String getLocationString( int loc )
    {
        String str;

        switch( loc )
        {
        case ODConstant.DocLocationCache:
            str = "Cache";
            break;
        case ODConstant.DocLocationArchive:
            str = "Archive";
            break;
        case ODConstant.DocLocationExternal:
            str = "External";
            break;
        case ODConstant.DocLocationUnknown:
            str = "Unknown";
            break;
        default:
            str = "*** Invalid Doc Location ***";
            break;
        }

        return str;
    }



}