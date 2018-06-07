package com.michaelsoft.odwek;
import com.ibm.edms.od.*;

	public class TcCallback extends ODCallback
	{
	  byte[ ] data_from_folder;
	  boolean init = true;

	  TcCallback()
	  {
	  }

	  public void HitHandleCallback( int hit, int off, int len )
	  {
	  }

	  public boolean HitCallback( String docid, char type, String[ ] values )
					   throws Exception
	  {
		return true;
	  }

	  public boolean DataCallback( byte[ ] data )
	  {
		byte[ ] temp;
		int j, k;

		//----------
		// If first data block received, initialize container; otherwise,
		// append new data to that previously received.
		//----------
		if ( init )
		{
		  data_from_folder = data;
		  init = false;
		}
		else
		{
		  temp = new byte[ data_from_folder.length + data.length ];
		  for ( j = 0; j < data_from_folder.length; j++ )
			temp[j] = data_from_folder[j];
		  k = data_from_folder.length;
		  for ( j = 0; j < data.length; j++ )
			temp[k++] = data[j];
		  data_from_folder = temp;
		}

		return true;
	  }

	  public byte[ ] getData( )
	  {
		return data_from_folder;
	  }
	}