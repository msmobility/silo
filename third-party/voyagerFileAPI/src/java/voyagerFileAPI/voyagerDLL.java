package java.voyagerFileAPI;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface voyagerDLL extends Library
{		
	voyagerDLL INSTANCE=(voyagerDLL) Native.loadLibrary("VoyagerFileAccess",voyagerDLL.class);
	
	Pointer MatReaderOpen(String filename, String errMsg, int errBuffLen);
	Pointer MatWriterOpen(String filename, String field, int ntype, int nZones, int nMatrices, byte[] precisions, String[] matrixNames, String errMsg, int errBuffLen);
	
	void MatReaderClose(Pointer state);	
	void MatWriterClose(Pointer state);	
	
	int MatReaderGetNumMats(Pointer state);	
	int MatReaderGetNumZones(Pointer state);	
	int MatReaderGetMatrixNames(Pointer state, String[] names);
	
	int MatReaderGetRow(Pointer state, int matNumber, int rowNumber, double[] buffer);	
	int MatWriterWriteRow(Pointer state, int matNumber, int rowNumber, double[] buffer);
	
}