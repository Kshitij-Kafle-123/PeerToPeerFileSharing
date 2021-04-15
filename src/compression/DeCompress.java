package compression;


import java.io.*;
import file.FileIO;


public class DeCompress implements Signature{

	
	private String fileName,outputFilename;
	private String[] hCodes = new String[MAXCHARS];
	private int distinctChars = 0;
	private long fileLen=0,outputFilelen;
	
	private FileOutputStream outf;
	private String gSummary;

	
	public DeCompress(){
		loadFile("","");
		}
	public DeCompress(String txt){
		System.out.println("client side bata");
		loadFile(txt);
		}
	public DeCompress(String txt,String txt2){
		loadFile(txt,txt2);
		}
		
	public void loadFile(String txt){
		System.out.println(txt);
		fileName = txt;
		outputFilename = stripExtension(txt,strExtension);
		System.out.println(outputFilename);
		gSummary = "";
		}
	public void loadFile(String txt,String txt2){
		//System.out.println("hi");
		fileName = txt;
		outputFilename = txt2;
		gSummary = "";
		}
	String stripExtension(String ff,String ext){

	//	ff = ff.toLowerCase();
		if(ff.endsWith(ext.toLowerCase())){
			return ff.substring(0,ff.length()-ext.length());
			}
		//return "_" + ff;
		return ff+ext.toLowerCase();
		}
		
	public boolean decodeFile() throws Exception{
		System.out.println("decode this file");
		
		if(fileName.length() == 0) return false;
		
		for(int i=0;i<MAXCHARS;i++) hCodes[i] = "";
		long i;
		FileIO fin = new FileIO(fileName);
		fileLen = fin.available();

		String buf;
		buf = fin.getBytes(huffSignature.length());
		//System.out.println(buf);
		if(!buf.equals(huffSignature)) return false;
		outputFilelen = Long.parseLong(fin.getBits(32),2);
		distinctChars = Integer.parseInt(fin.getBits(8),2) + 1;
		gSummary  += ("Compressed File Size : "+ fileLen + "\n");
		gSummary  += ("Original   File Size : "+ outputFilelen + "\n");
		gSummary  += ("Distinct   Chars     : "+ distinctChars + "\n");
		//System.out.println(gSummary);
		for(i=0;i<distinctChars;i++){
		
			int ch = Integer.parseInt(fin.getBits(8),2);
			int len = Integer.parseInt(leftPadder(fin.getBits(5),8),2);
			hCodes[ch] = fin.getBits(len);
			//System.out.println((char)ch + " : "  + hCodes[ch]);
			}
		
		try{
		
		outf = new FileOutputStream(outputFilename);
		i = 0;
		int k;
		int ch;
		
		while(i < outputFilelen){	
				buf = "";
				for(k=0;k<32;k++){
					buf += fin.getBit();
					ch  = findCodeword(buf);
					//System.out.println(ch);
						if(ch > -1){
							outf.write((char)ch);
							buf = "";
							i++;
							break;
						}
					}
				if(k >=32 ) throw new Exception("Corrupted File!");
				
			}
		
		outf.close();
		return true;
		
		}catch(Exception e){
			throw e;
		}
 

		}
		
	int findCodeword(String cw){
		int ret = -1;
		for(int i=0;i<MAXCHARS;i++){
			if(hCodes[i] != "" && cw.equals(hCodes[i])){
				ret = i;
				break;
			}
			}
			return ret;
		}

	
	String leftPadder(String txt,int n){
		while(txt.length() < n )
			txt =  "0" + txt;
		return txt;
		}
	
	String rightPadder(String txt,int n){
		while(txt.length() < n )
			txt += "0";
		return txt;
		}
		
	public String getSummary(){
		return gSummary;
		}
	
		
		  public static void main(String[] args) throws Exception { DeCompress de=new
		  DeCompress("C:\\Users\\Kshitij\\Desktop\\d.txt"); 
		  de.decodeFile(); }
		 
}



