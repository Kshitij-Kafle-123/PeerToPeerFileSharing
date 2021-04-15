package compression;

import java.io.*;
import file.FileIOWrite;



public class Compress implements Signature{
	
	
	private String fileName,outputFilename;
	private HuffmanNode root = null;
	private long[] freq  = new long[MAXCHARS];
	private String[] hCodes = new String[MAXCHARS];
	private int distinctChars = 0;
	private long fileLen=0,outputFilelen;
	private FileInputStream fin;
	private BufferedInputStream in;	
	private String ourSummary;

	
	void resetFrequency(){
		for(int i=0;i<MAXCHARS;i++)
		freq[i] = 0;
		
		distinctChars = 0;
		fileLen=0;
		ourSummary ="";
		}
	
	public Compress(){
		loadFile("","");
		}
	public Compress(String txt){
		System.out.println(txt);
		loadFile(txt);
		}
	public Compress(String txt,String txt2){
		loadFile(txt,txt2);
		}
		
	public void loadFile(String txt){
		fileName = txt;
		outputFilename = txt + strExtension;
		System.out.println(outputFilename);
		resetFrequency();
	
		}
	public void loadFile(String txt,String txt2){
		fileName = txt;
		outputFilename = txt2;
		System.out.println(fileName+" "+outputFilename);
		resetFrequency();
		}
	public String getOutputFileName() {
		return outputFilename;
	}
	public boolean encodeFile() throws Exception{
		
		if(fileName.length() == 0) return false;
		try{
		fin = new FileInputStream(fileName);
		in = new BufferedInputStream(fin);
		
		
		}catch(Exception e){ throw e; }
		
		//Frequency Analysis
  		try
		{
			fileLen = in.available();
			System.out.println(fileLen);
			if(fileLen == 0) throw new Exception("File is Empty!");
			ourSummary += ("Original File Size : "+ fileLen + "\n");
			System.out.println(ourSummary);
			long i=0;

			in.mark((int)fileLen);
			distinctChars = 0;
			
			while (i < fileLen)
			{		
				int ch = in.read();	
				//System.out.println(ch);
				i++;
				if(freq[ch] == 0) distinctChars++;
				freq[ch]++;
				
			}
			in.reset();			
		}
		catch(IOException e)
		{
			throw e;
			//return false;
		}
  		ourSummary += ("Distinct Chars " + distinctChars + "\n");
		/*
		System.out.println("distinct Chars " + distinctChars);
		 //debug
		for(int i=0;i<MAXCHARS;i++){
			if(freq[i] > 0)
			System.out.println(i + ")" + (char)i + " : " + freq[i]);
		}*/
		
		
		CustomPriorityQueue  cpq = new CustomPriorityQueue (distinctChars+1);
		
		int count  = 0 ;
		for(int i=0;i<MAXCHARS;i++){
			if(freq[i] > 0){
				count ++;
		//	System.out.println("ch = " + (char)i + " : freq = " + freq[i]);
				HuffmanNode np = new HuffmanNode(freq[i],(char)i,null,null);
			//	System.out.println(np);
				cpq.Enqueue(np);
				}
		}
		
		//cpq.displayQ();
		
		HuffmanNode low1,low2;
		
		while(cpq.totalNodes() > 1){
			low1 = cpq.Dequeue();
		//	System.out.println(low1);
			low2 = cpq.Dequeue();
			if(low1 == null || low2 == null) { throw new Exception("Queue Error!"); }
			HuffmanNode intermediate = new HuffmanNode((low1.freq+low2.freq),(char)0,low1,low2);
			if(intermediate == null) { throw new Exception("Not Enough Memory!"); }
			cpq.Enqueue(intermediate);

		}
		
		//cpq.displayQ();
		//root = new HuffmanNode();
		root = cpq.Dequeue();
		buildHuffmanCodes(root,"");
		
		for(int i=0;i<MAXCHARS;i++) hCodes[i] = "";
		getHuffmanCodes(root);
		
		
		
		//debug		
		/*for(int i=0;i<MAXCHARS;i++){
		if(hCodes[i] != ""){ 
		System.out.println(i + " : " + hCodes[i]);
		}
		}*/
		
		
		FileIOWrite hFile = new FileIOWrite(outputFilename);
		
		hFile.putString(huffSignature);
		String buf;
		buf = leftPadder(Long.toString(fileLen,2),32); //fileLen
		hFile.putBits(buf);
		buf = leftPadder(Integer.toString(distinctChars-1,2),8); //No of Encoded Chars
		hFile.putBits(buf);
		
		for(int i=0;i<MAXCHARS;i++){
			if(hCodes[i].length() != 0){
				buf = leftPadder(Integer.toString(i,2),8);
				hFile.putBits(buf);
				buf = leftPadder(Integer.toString(hCodes[i].length(),2),5);
				hFile.putBits(buf);
				hFile.putBits(hCodes[i]);
				}
			}
		
		long lcount = 0;
		while(lcount < fileLen){
			int ch = in.read();
			hFile.putBits(hCodes[(int)ch]);
			lcount++;
		}
		hFile.closeFile();
		outputFilelen =  new File(outputFilename).length();
		float cratio = (float)(((outputFilelen)*100)/(float)fileLen);
		ourSummary += ("Compressed File Size : " + outputFilelen + "\n");
		ourSummary += ("Compression Ratio : " + cratio + "%" + "\n");
		return true;
		
		}
	
	void buildHuffmanCodes(HuffmanNode parentNode,String parentCode){

		parentNode.huffCode = parentCode;
		if(parentNode.left != null)
			buildHuffmanCodes(parentNode.left,parentCode + "0");
		
		if(parentNode.right != null)
			buildHuffmanCodes(parentNode.right,parentCode + "1");
		}
	
	void getHuffmanCodes(HuffmanNode parentNode){
		
		if(parentNode == null) return;
		
		int asciiCode = (int)parentNode.ch;
		if(parentNode.left == null || parentNode.right == null)
		hCodes[asciiCode] = parentNode.huffCode;
		
		if(parentNode.left != null ) getHuffmanCodes(parentNode.left);
		if(parentNode.right != null ) getHuffmanCodes(parentNode.right);
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
		return ourSummary;
		}
	
		
		  public static void main(String[] args) throws Exception { Compress c= new
		  Compress("C:\\Users\\Kshitij\\Desktop\\s.txt",
		  "C:\\Users\\Kshitij\\Desktop\\d.txt"); c.encodeFile(); }
		 
}


