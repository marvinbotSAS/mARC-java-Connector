package mARC.Connector;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * class which contains the main methods to communicate to a mARC server.
 *
 */

@SuppressWarnings("unused")
public final class Connector{

    private boolean analyze; //  analyze is used to check if we have to store results (if its false this means that results are not stored).
	
    private String ip;  // IP is the address of the mARC server (KM).
    private String port; // Port is the port where mARC server is running.
    private SocketChannel sock; // sock is socket object to the mARC server
    private PrintWriter out;
    private BufferedReader in;
    private Boolean isConnected; // isConnected checks if the client is connected or not the Marvin's  server
    private Boolean isValid;      // isValid checks if the server IP is valid and reachable
    private Boolean isError;
    private Boolean isBlocking;
    private String socketErrorMsg;
    private int timeLimit; // Connection time out (in seconds) to the mARC server. 
    private String toSend ; // String to send to the server
    public String RawScript;
    private String toReceive; // String returned by the server.
    private String received;
    
    public boolean directExecute; // This value specifies which mode of script to used when submitting request to mARC server (false means line by line execution, true tells to the server to turn to the script mode.
                                   
    private final Lock lock;
    private int idx;
    private  ArrayDeque<String> kmSessions;	    	
    private  String kmScriptSession;	// value of the session id for the script
    public  MarcResult  result;     //   

    
    private int kmError ;     // Indicates if the call of a KM method returns or not an error. 
    private String executionErrorMsg ;  // String containing the error message 
    
    public String session_name;
    
    public String SessionId;      //  id of  session
    private int kmCurrentId;  //  id of the KM session for the last request.
    
    private String kmFunction;   // Method of KM to call
    private String kmParams;     // list of parameters of KM method.    
    static private String[] kmTypeLabel = new String[15];     
    private KmString kmstring = new KmString();
    
    private   ArrayList<String> params;  // Stack of commands and parameters of script. 
    private   ArrayList<String> localParams = new ArrayList<String>(); 
    private String name;
    
    private String ServerName;
    private String ServerBuild;
    private String ServerType;
    private String ServerVersion;
    private String ServerPort;
    
    public boolean FindASessionId(String id)
    {
        return this.kmSessions.contains(id);
    }
    public void AddASessionId(String id)
    {
        if (this.kmSessions.contains(id) )
        {
                    return;
        }
        this.kmSessions.add(id);
    }
    
    public void RemoveASessionId(String id)
    {
        if (!this.kmSessions.contains(id) )
        {
                    return;
        }
        
        this.kmSessions.remove(id);
    }
    
    public void push(String s)
    {
      localParams.add( new String(s) );
    }
    
    public boolean isAnalyze() {
        return analyze;
    }

    public void Lock()
    {
        lock.lock();
    }
    
    public void UnLock()
    {
        lock.unlock();
    }
    
     public String[] getDataByName(String name, int idx)
     {
         return result.getDataByName(name, idx);
     }
    
     public String[] getDataByLine(int row, int idx)
     {
         return result.getDataByLine(row, idx);
     }
     
    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public SocketChannel getSock() {
        return sock;
    }

    public void setSock(SocketChannel sock) {
        this.sock = sock;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public Boolean getIsBlocking() {
        return isBlocking;
    }

    public void setIsBlocking(Boolean isBlocking) {
        this.isBlocking = isBlocking;
    }

    public String getSocketErrorMsg() {
        return socketErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.socketErrorMsg = errorMsg;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getToSend() {
        return toSend;
    }

    public void setToSend(String toSend) {
        this.toSend = toSend;
    }

    public String getToReceive() {
        return toReceive;
    }

    public void setToReceive(String toReceive) {
        this.toReceive = toReceive;
    }


    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public ArrayDeque<String> getKmSessions() {
        return kmSessions;
    }


    public String getKmScriptSession() {
        return kmScriptSession;
    }

    public void setKmScriptSession(String kmScriptSession) {
        this.kmScriptSession = kmScriptSession;
    }

    public MarcResult getResult() {
        return result;
    }

    public void setResult(MarcResult result) {
        this.result = result;
    }

    public int getError() {
        return kmError;
    }

    public void setError(int kmError) {
        this.kmError = kmError;
    }

    public String getExecutionErrorMsg() {
        return executionErrorMsg;
    }

    public void setExecutionErrorMsg(String kmErrorMsg) {
        this.executionErrorMsg = kmErrorMsg;
    }

    public String getKmId() {
        return SessionId;
    }

    public void setKmId(String kmId) {
        this.SessionId = kmId;
    }

    public int getKmCurrentId() {
        return kmCurrentId;
    }

    public void setKmCurrentId(int kmCurrentId) {
        this.kmCurrentId = kmCurrentId;
    }

    public String getKmFunction() {
        return kmFunction;
    }

    public void setKmFunction(String kmFunction) {
        this.kmFunction = kmFunction;
    }

    public String getKmParams() {
        return kmParams;
    }

    public void setKmParams(String kmParams) {
        this.kmParams = kmParams;
    }

    public static String[] getKmTypeLabel() {
        return kmTypeLabel;
    }

    public static void setKmTypeLabel(String[] kmTypeLabel) {
        Connector.kmTypeLabel = kmTypeLabel;
    }

    public KmString getKmstring() {
        return kmstring;
    }

    public void setKmstring(KmString kmstring) {
        this.kmstring = kmstring;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public ArrayList<String> getLocalParams() {
        return localParams;
    }

    public void setLocalParams(ArrayList<String> localParams) {
        this.localParams = localParams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public String getServerBuild() {
        return ServerBuild;
    }

    public void setServerBuild(String serverBuild) {
        ServerBuild = serverBuild;
    }

    public void setReceived(String received) {
        this.received = received;
    }
    
    /**
     * This constructor creates an instance of the object Connector using the following parameters
     * @param aName is the string which identify this Connector
     * @param ip is the IP address of the marvin's Server
     * * @param port is the port on which the marvin's Server is running
     * @return true or false indicating if the command or the script is correctly executed by the server or not
     */

    public Connector(String aName, String ip, String port)
    {
        this.kmSessions = new ArrayDeque<>();
        this.lock = new ReentrantLock();
        name = aName;
        this.ip=ip;
        this.port=port;
        initialize();
    }
    
    public Connector()	 
	{   
        this.kmSessions = new ArrayDeque<>();
        this.lock = new ReentrantLock();
            name = "";
            initialize();
        }
    public void initialize()
    {

        analyze = true;
        kmError = 1;
        sock =			null;
        isConnected = 	false; //   State of the connection to the server 
    	isValid = 		false; //Checks if the TCP connection is valid 
   	isError = 		true;
    	isBlocking = 	true;
    	socketErrorMsg = 		"TCP Socket not created";
    	timeLimit =		10; 	// 10s is the value of the  time out (the default value). 0 is no time out.
    	SessionId = 			"-1";		// KM session id,  the default value is "-1" which means this session fails to get correct id consequently this session is not valid
    	toSend 	=		"";     
    	
    	directExecute = true;
    	
    	result=null;
    	result  = new MarcResult();
        kmScriptSession = "-1";
        
       
        // Definition of the mARC server data types 
    	kmTypeLabel [0] = "string"; 		kmTypeLabel [1] = "int32";		kmTypeLabel [2] = "uint32";
    	kmTypeLabel [3] = "int8"; 			kmTypeLabel [4] = "uint8";		kmTypeLabel [5] = "char";
    	kmTypeLabel [6] = "int64"; 			kmTypeLabel [7] = "uint64";		kmTypeLabel [8] = "string";
    	kmTypeLabel [9] = "float"; 			kmTypeLabel [10] = "double";	kmTypeLabel [11] = "bool";
    	kmTypeLabel [12] = "simpledate"; 	kmTypeLabel [13] = "rowid";		kmTypeLabel [14] = "sessionid";
       }    

    public String getReceived()
    {
        return received;
    }
/**
 * This method destroys close all the active sessions and close the client communication channel with the server.
 */
    public void finalize() 
	{

            kmSessions.clear();

		if (sock!=null) 
          {
          	 try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	 sock = null;
          }
    }
    
    /**
     * This method asks to the server to execute already well formatted command or script
     * @param cmd is the command or the script
     * @return true or false indicating if the command or the script is correctly executed by the server or not
     */
    public boolean  executeCommand (String cmd)
    { 
        
	//checking of validate of the session
        if ( kmScriptSession.equals("-1") ) {
          //  Logger.getLogger("mARQTA").log(Level.INFO, "session ID is "+kmScriptSession+". could not execute command :: not connected ");
            // we can return false in this place for optimization (because there is no need to send to server request for not valid session/
        }
       
       toSend = kmScriptSession +" "+ cmd;
       RawScript = cmd;
       return executeScript();
    }
    
    /**
     * This method checks if a given string is a numeric.
     * @param str is a string to check
     * @return true or false indicating if the string is a numeric or not.
     */
    private  boolean isNumeric(String str)  
    {  
      try  
      {          
		double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }     
    
    
    /**
     * This method ask mARC server to execute a command with a given parameters.
     * @param params is the list of  the parameters of the command to execute
     * @return true or false indicating if the execution is well done or not.
     */
    public boolean execute(String ...params )
    { 
    	String session;
        toSend ="";
        int idx = 0;
        String str =  params [idx];
        //the first parameter is the session id      
        if (isNumeric(str)) { session = str; idx +=1;}
        else { session = SessionId;}
        
    	String function = params [idx];
    	idx +=1;
    	toSend = session;
    	toSend += ' ';
    	toSend  += function;
    	toSend  += " ( ";
    	for (int i=idx ; i < params.length; i++ ) 
    	{
    		str = params [i];    		    		
    		if ((str.compareToIgnoreCase("null")!=0 )&& (str.compareToIgnoreCase("default")!=0))
    		{ 			
    			kmstring.SetKMString(str);
    			kmstring.toGpBinary();
    			str = kmstring.GetKMString();
    		}

    		toSend  += str;
    		toSend  += " " ;
    		if (i< params.length-1)
    		{
    			toSend += ", ";
    		}		
    		
    	}
    	
    	toSend  += ')';   
        RawScript = toSend.substring(toSend.indexOf(" ")+1)+"\n";
    	return executeScript ();        
    }	
    
    public void newCommand()
    {
        this.localParams.clear();
    }
    
    /**
     * This method ask mARC server clears all residual commands in the buffers and allows the user start a new script.
     * @param session is the session id. If the id is null, the value is of the default session id is set.
     */
    public void openScript(String session)
    {
    	if (session == null) 	
        {
            session = SessionId;
        } 

    	kmScriptSession = session;    	
    	toSend = kmScriptSession+" ";
        RawScript ="";
    	result.clear();
    	localParams.clear();
    	
    }
    
    
    /**
     * This method ask mARC server to execute a script.
     * @return true or false indicating if the execution is well done or not.
     */
    public boolean executeScript()
    {
    	send();
        receive();
        analyze();

        if (kmError == 0) return false;
    	return true ;
    	
    }
 
    /**
     * This method builds the command/script which will be sent to the the mARC server
     */
    public void addFunction()
    {
    	String[] params = new String[ localParams.size() ];
    	localParams.toArray( params );
    	addFunction( params );
        localParams.clear();
    }
    
    
    /**
     * This method builds the script/command which will be sent to the the mARC server
     * @param params is additional parameters for the request building
     */
    public void addFunction(String ...params)
    {

    	String str;
        String command = params[0]+"(";
    	toSend  += params[0]; 
    	toSend  += "(";
    	for (int i=1 ; i < params.length; i++ ) 
    	{
    		str = params [i];    		    		
    		if ((str.compareToIgnoreCase("null")!=0 )&& (str.compareToIgnoreCase("default")!=0))
    		{
			kmstring.SetKMString(str);
    			kmstring.toGpBinary();
    			str = kmstring.getStr();
    		}

    		toSend  += str;
    		toSend  += ' ';
                command += str+' ';
    		if (i< params.length-1)
    		{
    			toSend  += ", ";
                        command += ", ";
    		}		
    		
    	}
    	
    	toSend  += ") ;";
        command += ");\n";
        this.RawScript += command;
    }

    /**
     * This method asks the mARC server to close the current session. The current socket is not affected.
     * 
     */
    public void closeScript ()
    {
    	kmScriptSession = "-1";
    	if (toSend!=null) {toSend ="";}	
    	
    }     


    /**
     * This method analyzes the response received from the mARC server
 if an error is detected in the response, the kmError is set to 1
 and the executionErrorMsg contains the error sent from the server
     */    
    private void analyze() 
    {       
    	if ( received.isEmpty() )
    		return;
    	
    	result.clear();
        result._analyse = analyze;
    	result.analyse(received);
        
        executionErrorMsg = "OK";
        kmError = 1;
    	if  ( result.mError )
    	{
    		executionErrorMsg = result.mErrorMessage;
    		kmError = 0;
    	}
    }  
    
	
    /**
     * This method opens a new session on mARC server
     * @return returns -1 when error occurs otherwise the id of the new session is returned and then the list of opened sessions is updated
     */
	public String openKmSession() 
	{
		toSend = "-1 CONNECT (NULL);";
		executeScript();
		if (kmError == 0) return "NULL";	
		kmSessions.add( this.result.session_id ) ;	
                return this.result.session_id;
	}  

 

	/**
	 * This method disconnects the client from the mARC server.
	 * @return returns true if the disconnection is correctly done and false otherwise.
	 */
	public boolean disConnect()
        {
           // Logger.getLogger("mARQTA").log(Level.INFO, "DISconnecting from mARC server IP :"+ip+" Port : "+port);
            if ( sock == null )
            {
                //Logger.getLogger("mARQTA").log(Level.INFO, "socket was null so nothing was done.");
                isConnected = false;
                isValid = false;
                isError = false;
                kmScriptSession = "-1";
                this.kmSessions.clear();
                return  true;
            }
            if ( sock.isOpen())
            {
                try
                {
                sock.close();
                isConnected = false;
                isValid = false;
                isError = false;
                kmScriptSession = "-1";
                this.kmSessions.clear();
                //Logger.getLogger("mARQTA").log(Level.INFO, "socket is closed. Disconnection is complete.");
                return true;
                }
                catch( Exception e)
                {
                  //  Logger.getLogger("mARQTA").log(Level.SEVERE, "ERROR : socket could NOT be closed.");
                    e.printStackTrace();
                    return false;
                }
            }
            kmScriptSession = "-1";
            isError = false;
            isConnected = false;
            isValid = false;
            return true;
        }
    /**
     * clears the current command buffers
     */
    public void resultsClear()
    {
       if (directExecute)     openScript(null);
       localParams.clear();
       push ("Results.Clear");
       addFunction ();
       if (directExecute)
        {
           doIt();
        }

    }
    
    /**
	 * This method establishes a connection with a mARC server.
	 * @return returns true is the establishment of the connection with the server is correctly done and false if not or the connection already exists.
	 */
	public boolean connect() 
	{
        if (isValid == true)
        {
 
        	socketErrorMsg = 		"This socket is already exists : " ;
        	isError = 		true;
        	isValid = 		false;
        	return false;         	
        	
        }
        
        try 
        { 
          //  Logger.getLogger("mARQTA").log(Level.INFO, "connecting to mARC server IP :"+ip+" Port : "+port);
        	sock = SocketChannel.open();
        	sock.configureBlocking(true);        	
        	sock.connect(new InetSocketAddress(ip,Integer.parseInt(port) ));
        	
        } 
        catch(Exception e) 
        { 
          //  Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server REFUSED ");
        	socketErrorMsg = "socket creation failure : " +e.getMessage().toString();
        	e.printStackTrace(); 
        	isError = 		true;
        	isValid = 		false;
        	return false; 
        }
            
        if (sock == null)
        {
           // Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server NOT VALID. Trying to close ");
        	try 
                {
                    sock.close();
              //       Logger.getLogger("mARQTA").log(Level.INFO, "connection to mARC server closed. ");
		}
                catch (IOException e) 
                {
				// TODO Auto-generated catch block
				e.printStackTrace();
                // Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server could not be closed. PANIC ");
                }        	
        	sock = null;
                isError = 		true;
                isValid = 		false;
        	return false;
        }
        
// Logger.getLogger("mARQTA").log(Level.INFO, "connection to mARC server SUCCEEDED :: socket : "+sock.toString());
        	isValid = 		true;
        	isError = 		false;
        	socketErrorMsg = 		"ok";
        	
        toSend = "-1 CONNECT (NULL);";
        send();
        receive();
        analyze();		
        kmScriptSession =  result.session_id ;
        SessionId = kmScriptSession;
        
        session_name = result.session_name;
        
       // Logger.getLogger("mARQTA").log(Level.INFO, "session ID attributed by mARC server is '"+kmScriptSession+"'");
        SERVER_GetProperties("port;name;build;type;model;version;command_threads;time_local;idle_time;cache_size;cache_used;cache_hits;exec_timeout_default;session_timeout_default;time_gmt;up_time");
        String[] properties = result.getDataByName("prop_value", -1);
        ServerName = properties[1];
       // Logger.getLogger("mARQTA").log(Level.INFO, "server name set to '"+ServerName+"'");
        toSend = kmScriptSession+" Server.GetBuild();";
        ServerBuild = properties[2];
       // Logger.getLogger("mARQTA").log(Level.INFO, "server build set to '"+ServerBuild+"'");
        if (kmError == 0 ) return false;
        isConnected = true;

	ServerType = properties[3];
        ServerVersion = properties[5];
        ServerPort = properties[0];
        
        return true; 
    }
    
	/**
         *  sends the content of the script to mARC server
         * @return true when completed or false if no connection found
         */
    public boolean send() 
	{ 
		boolean ok = false;
		if (isValid == false) return ok;
                // RawScript = toSend.substring(toSend.indexOf(" ")+1);
		kmstring.SetKMString(toSend);
		kmstring.toProtocol();
		toSend = kmstring.GetKMString();
             //   System.out.println("Send() msg toSend '"+toSend+"'");
		//Send data over socket
	      String text = toSend;
                           
	      ByteBuffer buf = ByteBuffer.allocate(text.length());
	      buf.clear();
              byte[] isobytes = text.getBytes(Charset.forName("ISO-8859-15"));
	      buf.put(supportNoIso(isobytes, text.length()));
	      buf.flip();
	      while(buf.hasRemaining()) {
	          try {
                    sock.write(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }    	   
		ok = true;
		return ok;
	}


    public  byte[] supportNoIso(byte[]  isobytes, int lenghtbuf)
    {
         int j=0;
        byte[] bytetmp = new byte[lenghtbuf];
             for(int i=0; i<isobytes.length; i++)   {
                 if(isobytes[i]<255)
                 {                     
                     bytetmp[j] = isobytes[i];
                     j++;
                 }
             }
     return bytetmp;
     }

/**
 * receives the binary response from mARC server following a sent script
 * @return true if the receive was successfully completed, false otherwise
 */
    public boolean receive()
    {
    	//Receive text from server
        Charset charset = Charset.forName("ISO-8859-15");
        CharsetDecoder decoder = charset.newDecoder();
       
     
    	received="";
          try{
		boolean ok = false;

		int ByteToReceive = 4096;
		int ByteReceived  = 0;
		boolean isHeaderOk = false;
		received = "";

                 ByteBuffer buf = ByteBuffer.allocateDirect(ByteToReceive);

		while(ByteToReceive>0)
    		{
    			String recv = "";
    			ByteReceived+=sock.read(buf);           
    			buf.flip();
                        decoder.reset();
                        CharBuffer charBuf = decoder.decode(buf);
                        buf.clear();                    
                       recv+= charBuf.toString();
                       received+=recv;
                       kmstring.SetKMString(received);
                       ByteReceived+=recv.length();
                       ByteToReceive-=recv.length();

    			if (isHeaderOk == false)
    			{
    				if (received.length()>2)
    				{
    					if (charBuf.get(0)!='#') {return false;}
    					int len1 = Integer.parseInt( received.substring(1, 1+1));
    					if (len1 <= 0) {	return false;}
    					if (received.length()> 5 + len1)
    					{
    						if (charBuf.get(2) != '#') 		{return false;}
    						if (charBuf.get(3+len1) != ' ') 	{return false;}
    						int len2 = Integer.parseInt( received.substring(3, len1+3));                                               
                            received = received.substring( 4+len1);
                            kmstring.SetKMString(received);
                                                
    						ByteReceived = received.length();
    						ByteToReceive = len2-ByteReceived;
    						isHeaderOk = true;
    					}
    				}
    			if (isHeaderOk == true)
            		{

                		if (ByteToReceive == 0) 
                                {
                                   /* byte[] bytes = received.getBytes("ISO-8859-1");
                                    String s = new String(bytes);*/
                                   return true;
                                }
    			}
    		}
             }


          } catch (IOException e) {
            
              return false;
          }
          return false;

    }
    
    public static void print(ByteBuffer bb) {
        while (bb.hasRemaining())
        bb.rewind();
      }

   /**
    *    Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
    	 The new ByteBuffer is ready to be read.
    * @param _str the unicode encoded string
    * @return the ISO 8859-25 encoded string
    */
    public String unicodetoIso(String _str)
    {
    	Charset charset = Charset.forName("ISO-8859-1");
    	CharsetDecoder decoder = charset.newDecoder();
    	CharsetEncoder encoder = charset.newEncoder();

    	try {

    	   ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(_str));

    	    // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a string.
    	    // The new ByteBuffer is ready to be read.
    	    CharBuffer cbuf = decoder.decode(bbuf);
    	    String s = cbuf.toString();
//    	    System.out.println("isotoUni s "+s);
    	    return s;
    	} catch (CharacterCodingException e) {
    		return e.toString();
    	}
    }
    
    public MarcResult getMarcResult()
    {
    	return result;
    }
    
 /**
  *  execute the current script
  * 
  * direcExecute is a boolean specifying the execution mode:
  * 
  *         - false : script mode : do not execute current script now
  *         - true  : line by line mode : execute now
  */
    private void doIt()
    {
       if ( directExecute )
       {
           executeScript();
       }
    }

    /**
     * shuts down the server, and optionnaly restart it
     * @param option (default "")  if option is "restart", the server will then try to restart
     */
    public void SERVER_ShutDown(String option)
{
       if ( directExecute )     openScript(null);
       localParams.clear();
       push("SERVER.ShutDown");
       if ( option != null && !option.isEmpty() )
       push(option);
       addFunction();
       if ( directExecute )     doIt();

}
    /**
     * Lists all API methods of the server
     */
     public void SERVER_GetApi()
{
       if ( directExecute )     openScript(null);
       localParams.clear();
       push("SERVER.GetApi");

       addFunction();
       if ( directExecute )     doIt();

}
    
     /**
      * Access to server's properties
Trying to change the value of a Read Only property will not generate an error.
To see which properties are avalaible, see SERVER.GetProperties
To change one property,  use  a directive as  :  propertyname = propertyvalue
To change several properties values in one command, separate each directive with the character  semi column ( ; )
an accessor is a string like :
"propertyname = value"
and can be extended like
"propertyname1 = value1, … propertynameN = valueN"
Depending of your client application using only one extended accessor, as a parameter, is equivalent as using several accessors as parameters

      * @param accessors a list of properties accessors
      */
public void SERVER_SetProperties(String[] accessors)
{
        if ( accessors == null || accessors.length == 0)
        {
            return;
        }
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SERVER.SetProperties");
       for(String s: accessors)
       {
           push(s);
       }
       addFunction();
       if ( directExecute )     doIt();


}

/**
 * Gets one or several Session properties.
To access one property,  use  a directive as  :  propertyname
To access several properties values in one command, separate each directive with the character  semi column ( ; )
if there are no parameter, all properties will be accessed

 * @param accessor properties separated by a semi colon
 */
    public void SERVER_GetProperties(String accessor)
{

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SERVER.GetProperties");
       if ( accessor != null )
       {
           push(accessor);
       }
       addFunction();
       if ( directExecute )     doIt();


} 
    /**
     * Gets all currently socket connected to the server
     * @param start (default start = 1)	 the starting index
     * @param count (default count = -1 (all until the end is reached) ) the number of connexions required
     */
 public void SERVER_GetConnected (String start, String count)
{

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SERVER.GetConnected");
       push(start);
       push(count);
       addFunction();
       if ( directExecute )     doIt();


}
 /**
  * during intense execution, the Server performs background tasks that are logged.
it is possible to get the description of all current tasks through this method
it returns a list off all current server tasks

  */
  public void SERVER_GetTasks()
{

       if ( directExecute )     openScript(null);
       localParams.clear();
       push("SERVER.GetTasks");
       addFunction();
       if ( directExecute )     doIt();
}
    /**
     * Main connection entry point. 
     * this method returns a valid session Id that can be used for further commands
     */
public void SESSION_Connect()
{
       if ( directExecute )     openScript(null);
       localParams.clear();
       push("SESSION.Connect");
       addFunction();
       if ( directExecute )     doIt();

}
    /**
     * Fetches the list of all active session at server level.
a maximum of 100 session descriptors can be accessed at each method call

     * @param start beginning index connection (base 1)
     * @param count number of connections to retrieve (max 100)
     */
        public void SESSION_GetInstances(String start, String count)
    {

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.GetInstances");
       push(start);
       push(count);
       addFunction();
       if ( directExecute )     doIt();

    }

        
        /**
         * Resets all properties and objects of a session.
            Especially the Contexts and Results.
             It frees memory and ressources used a session, including
            Contexts stack
            Results stack
            Inhibitor context
            Profiler context
            Session variables
         * @param options option = ["all"(default), "contexts", "results","profiler","inhibitor","variables"]
         */
        public void SESSION_Clear(String options)
    {

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.Clear");
       if ( options !=null && !options.isEmpty())
       {
           push(options);
       }
       addFunction();
       if ( directExecute )     doIt();

    }
    /**
     * 
     * Gets one or several Session properties.
To access one property,  use  a directive as  :  propertyname
To access several properties values in one command, separate each directive with the character  semi column ( ; )
if there are no parameter, all properties will be accessed

     * @param accessor the properties values separated by a semi-colon
     */
        public void SESSION_GetProperties(String accessor)
    {

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.GetProperties");
       if ( accessor != null && !accessor.isEmpty())
       {
           push(accessor);
       }
       addFunction();
       if ( directExecute )     doIt();

    }

        /**
         * Access to session's  properties
Trying to change the value of a Read Only property will NOT generate an error
* during execution
         * @param accessors the list of properties to set
         */
    public void SESSION_SetProperties(String[] accessors)
    {
        if ( accessors == null || accessors.length == 0)
        {
            return;
        }
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.SetProperties");
       for(String s: accessors)
       {
           push(s);
       }
       addFunction();
       if ( directExecute )     doIt();

    }
                    /**
Gets the topmost context of the context's stack, and consolidate the profiler context with it.
The main differences with affecting the profiler through the Session.profiler_context_string property are :
-	it is faster
-	the profiler context will be consolidated, eg, it's previous content will be kept
     */
    public void SESSION_ContextToInhibitor()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.ContextToInhibitor");
       addFunction();
       if ( directExecute )     doIt();

    }
                /**
Gets the topmost context of the context's stack, and consolidate the profiler context with it.
The main differences with affecting the profiler through the Session.profiler_context_string property are :
-	it is faster
-	the profiler context will be consolidated, eg, it's previous content will be kept
     */
    public void SESSION_ContextToProfiler()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.ContextToProfiler");
       addFunction();
       if ( directExecute )     doIt();

    }
            /**
push the inhibitor context into a new context on the context's stack
if the inhibitor_context_string is void, the new context on top of the stack is empty (it's count is 0)
     */
    public void SESSION_InhibitorToContext()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.InhibitorToContext");
       addFunction();
       if ( directExecute )     doIt();

    }
        /**
push the profiler context into a new context on the context's stack
if the profiler_context_string is void, the new context on top of the stack is empty (it's count is 0)
     */
    public void SESSION_ProfilerToContext()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.ProfilerToContext");
       addFunction();
       if ( directExecute )     doIt();

    }
    /**
Retrieves informations about the last Data Base operation which occured for the current session.
     */
    public void SESSION_GetLastDBInfo()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.GetLastDBInfo");
       addFunction();
       if ( directExecute )     doIt();

    }
    
    /**
 Saving the mARC binary to the disk
the marc will be saved in the mARC repository in the folder
data\knowledge\marc\marc.knw.
during the process, the previous version is saved as marc.bak
the marc.bak file will be deleted if the saving process if successful.
in case of incident, and if a marc.bak file can be seen in the data\knowledge\marc\ the server will try to restore the previous state. If not possible, the server will request human supervision, and shutdown.

     */
    public void SESSION_MarcSave()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.MarcSave");
       addFunction();
       if ( directExecute )     doIt();

    }
    
    /**
     * Reloads the mARC from it's last state.
       a server task will be created
       * during this operation, all requests, except Server.xxx requests will be returned with an error
        "message = mARC reloading, please wait"


     */
    public void SESSION_MarcReload()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.MarcReload");
       addFunction();
       if ( directExecute )     doIt();

    }
    /**
     * Clears the mARC content, and the  document context field (KNW_ABSTRACT) of all lines of the associated master table, if there is one.
     */
    public void SESSION_MarcClear()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("SESSION.MarcClear");

       addFunction();
       if ( directExecute )     doIt();

    }

    /**
     * This method creates or updates the indexing information using the master table records
     * @param knw : the identifier or the name of the knowledge
     * @param columns : a string containing master table column name. The columns have to store iso-8859-15 text. column names are separated by a space
     * @param begin_rowid : the start line of the master table that will be processed
     * @param end_row_id : the last line of the master table, that will be processed
     * @param mode : "ref" or "none",  
               "NULL" : the mARC will read and learn from the columns
               "ref" :	the mARC will also indexing the master table lines
               

     */
    public void SESSION_MarcRebuild(String columns, String begin_rowid, String end_row_id, String mode)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.MarcRebuild");
       push( columns);
       push (begin_rowid);
       push (end_row_id);
       push( mode );
       addFunction();
       if ( directExecute )     doIt();
    } 
    /**
     * This method flushes the indexing buffer, and gets the indexing informations ready for querying 
       intended for applications that use the internal indexation engine of the mARC Server.
When a document is indexed through the Store ( ), or Index ( ) commands,  the indexation information is stored into the Indexation Buffer.
at this time, the documents are not yet accessible through a ContextToDoc ( ) command.
The indexation buffer will automatically flushed  when :
-	it reaches it's maximum size (property Server.indexation_cache_size (in KB))
-	if a MarcSave () command is issued
-	if a MarcPublish( ) command is issued
once the indexation cache flushed, all documents indexed during this time will be publishad, eg, they will be accessible through requests.
     */
    public void SESSION_MarcPublish()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.MarcPublish");

       addFunction();
       if ( directExecute )     doIt();
    }

    /**
     * available only when the indexation engine of the mARC Server is used, eg, when a master table is associated to the mARC. 
the indexation context of a document of the master table (the knw_abstract column content of the document)  will be copied into a new context on top of the contexts stack.
further operation on context can the be performed in order to contextually analyze the document.


     * @param rowid : the row id of the master table
     * @param boolSpectrum (default false) if set to true, the session Spectrum will automatically be applies to the new context
     */
    public void SESSION_DocToContext(String rowid, String boolSpectrum)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.DocToContext");
       push(rowid);
       push(boolSpectrum);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * available only when the indexation engine of the mARC Server is used, eg, when a master table is associated to the mARC. 
the topmost context of the context's stack is converted into a new result set of documents, that are ranked according to the importance of that context inside them.
it's behavior  is dependant of the current spectrum, especially the following properties of the spectrum:
                    - Spectrum.max_record
                    - Spectrum.min_activity

     */
        public void SESSION_ContextToDoc()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.ContextToDoc");

       addFunction();
       if ( directExecute )     doIt();
    }
        
    /**
     * converts a string to a new context on the context's stack.
it is equivalent to affecting contexts.context_string property, except that
-	it creates a new context on top of the stack 
-	it applies the current Spectrum to the process of converting the string signal into an internal mARC context.
-	it can learn new shapes if the optional learn parameter is set to true
by default no new shapes will be learned during this process.

     * @param signal the string to convert 
     * @param boolLearn (default false) if set to true it is learned on the fly
     */
    public void SESSION_StringToContext( String signal, String boolLearn)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.StringToContext");
       push(signal);
       push(boolLearn);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * It is one of the main methods in order to interact and query with the mARC.
It uses the topmost context of the context's stack in order to perform a contextual analysis
It's behavior will depend on :
-	the current Spectrum
-	the content of the profiler context SESSION.ContextToProfiler
-	the content of the inhibitor context SESSION.ContextToInhibitor
a full explanation will be found in the mARC user documentation, chapter contextual and semantic programming.
at least three new contexts (maybe empty) will be created on the contexts Stack.
it returns the number of newly created sub contexts.
If N subcontexts, the two last represents respectively the categories contexts, and the shapes contexts, the other N-2 subcontexts are associative contexts.
source context :	source
example of categories context : armée_rouge rouge_sang couleur_rouge….
example of shape context : rouges rougie….
example of associative context : blanc, noir, vert, bleu, blanche….
example of associative context : lumière longeur_d_onde nm …


     */
        public void SESSION_ContextToContext()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.ContextToContext");
       addFunction();
       if ( directExecute )     doIt();
    }
        
        /**
         * the main back office method for the mARC.
it stores a signal into the mARC
new shapes and contextual structures will be detected and learned by the mARC.
there is no need for tuning parameters, the mARC handles automatically the learning process.
it returns void
By default, the Store method only performs contextual learning
but  depending of the parameters mode and rowid, it can perform concurrently :
-	creation of a term vector on the top of the context's stack
-	index  the incoming text signal to a rowid of the associated master table of the mARC
-	get a term vector representing the incoming text signal
mode can be one of the following value
-	"none"
-	"ranked"
-	"raw"
-	"unique"
Warning : if mode is different of "none", a new context is created on top of the context's stack
therefore, you will have to handle it, by dropping after use if necessary. This can be done by using contexts.Drop ( ), session.Clear("contexts")

mode = "ranked" : the term vector is exactly the same than the term vector evaluated by the default indexation algorithm. This can be used if you do not want to use the internal indexation mechanism of the mARC Server.

         * @param text the text to learn
         * @param mode "none"(default) "ranked" "raw" "unique"
         * @param rowid the row id (Default -1 : no indexation) in the master table retrieved via Table:XXXX.insert
         */
     public void SESSION_Store(String text, String mode, String rowid)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.Store");
       push(text);
       push(mode);
       push(rowid);
       addFunction();
       if ( directExecute )     doIt();
    }
     /**
      * extract the indexation term vector from the topmost context of the context's stack and links it to a master table row id.
this method can be used to build a custom indexation algorithm.
its is different from the store method, since it does not use the default indexation algorithm
a full explanation of the indexation process will be found in the mARC user Documentation, chapter indexation  

      * @param rowid  the row id in the master table
      */
    public void SESSION_Index( String rowid)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.Index");
       push(rowid);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Gets the current contextual spectrum property of the current session, under the shape of a table of a set of properties.
If you need a string compatible with the SetSpectrum methos, you can use the Session.GetProperty ("spectrum_string") instead.

     */
        public void SESSION_GetSpectrum()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.GetSpectrum");

       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Sets the current contextual spectrum property of the current session, under the shape of a table of a set of properties.
SetSpectrum can change one or several spectrum properties at a time, by using an accessor.

     * @param accessor  the spectrum properties to set with their values
     * examples
SetSpectrum ("min_atom = 3 ; min_generality = 5; evaluate = true", );
SetSpectrum  ( <50 min_atom = 3 ; min_generality = 5; evaluate = true/>);

     */    
    public void SESSION_SetSpectrum(String accessor)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.SetSpectrum");
       push(accessor);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Modifies the context on top of the context's Stack
     * according to the current Spectrum of the session
     */
        public void SESSION_ApplySpectrum()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.ApplySpectrum");
       addFunction();
       if ( directExecute )     doIt();
    }
        
        /**
         * This method is useful in order to predict or complete a user entry string.
it return NULL
2 new contexts are created on top of the context's stack
WARNING : you will have to handle these 2 new contexts, generally to drop them after use
the topmost context will contain the best possible future entries
the second one will contain what has been detected by the mARC
prototype

         * @param text 
         */
    public void SESSION_Completion(String text)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push ("SESSION.Completion");
       push(text);
       addFunction();
       if ( directExecute )     doIt();
    }

    /**
     * Gets the name of all the database tables instanciated in the server
     * @param start starting index (default 1) (base 1)
     * @param count the number to retrieve (default -1 : all)
     */
       public void TABLE_GetInstances(String start, String count)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table.GetInstances");
            push(start);
            push(count);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
       
       /**
        * Retrieves the total number of lines in a database table
        * @param tbl the name of the table
        */
        public void TABLE_GetLines(String tbl)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".GetLines");
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }

        /**
         * Gets the structure of a given table.
           Once created, the structure of a table cannot be altered

         * @param tbl  the name of the table
         */
        public void TABLE_GetStructure(String tbl)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".GetStructure");
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * Gets the names and the status of all B-tree indexes linked to a given database table.
         * @param tbl the name of the table
         */
        public void TABLE_GetBIndexes(String tbl)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".GetBIndexes");
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * Gets the names and the status of all Ktree indexes linked to a given database table.
         * @param tbl the name of the table
         */
        public void TABLE_GetKIndexes(String tbl)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".GetKIndexes");
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * creates a table named tbl
         * @param tbl the name of the table
         * @param location if NULL in the server's repository, else a file path
         * @param previsional_size NULL, or possible number of lines (for optimization)
         * @param type simple, or master, default is simple.
         * @param structure a descriptor of the columns : name, type, size ; 
         */
        public void TABLE_Create(String tbl,String location,String previsional_size,String type,String structure)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table.Create");
            push(tbl);
            push("null");
            push(location);
            push(previsional_size);
            push(type);
            push(structure);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * kills a table
         * @param tbl  the name of the table
         */
        public void TABLE_Kill(String tbl)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".Kill");
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * inserts a new line in a database table
         * @param tbl the name of the tables
         * @param colnames the names of the fields
         * @param values the values to put in the fields
         * 
         * NOTE : the rowid of the new line is returned.
         */
        public void TABLE_Insert(String tbl, String[] colnames, String[] values)
        {
            if ( colnames == null || values == null || (values.length != colnames.length && colnames.length == 0) )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".Insert");
            int i = 0;
            for ( String col :colnames )
            {
                 push(col);
                 push(values[i++]);
            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }

        /**
         * updates (modifies) one or several columns of a preexisting line in a table
         * @param tbl the name of the table
         * @param rowid the rowid to update
         * @param colnames the names of the fields to update
         * @param values  the updated content
         */
        public void TABLE_Update(String tbl, String rowid, String[] colnames, String[] values)
        {
            if ( colnames == null || values == null || (values.length != colnames.length && colnames.length == 0) )
             return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".Update");
            push(rowid);
            int i = 0;
            for ( String col :colnames )
            {
                 push(col);
                 push(values[i++]);
            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        public void TABLE_Delete(String tbl, String[] rowids)
        {
            if ( rowids == null || rowids.length == 0 )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".Delete");
            for ( String id :rowids )
            {
                 push(id);

            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * Adds data to a field of a line of a database Table.
this method is generally used to store large binary or text data block by block into a variable length field, instead of a one time commit that could overload the network, and slow multiple access
DataAdd can only operate on a one single column of a line

         * @param tbl the name of the table
         * @param rowid the rowid where to add data
         * @param colname the name of the field where to add data
         * @param value  the data to add
         */
        public void TABLE_DataAdd(String tbl, String rowid, String colname, String value)
        {
            if ( colname == null || value == null )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".DataAdd");
            push(rowid);
            push(colname);
            push(value);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * Use the Select method in order to select lines of a table according to certain criterion.
Select is a classical procedural method, not a mARC based method.
Select uses a column name in order to test which line will be selected.
The colum must be indexed via a Bindex (classical Btree index), or you can use the RowId column.
The selected lines RowId are stored in a Result Set Object (RS)  on the RS stack of the RESULTS object. (notice that the max number of lines in a RS is limited by default to 100 000).

depending of the <mode> parameter,  
it will create a new RS, <mode> = <3 new/> 
add it's result inside the RS on top of the RESULTS stack,  <mode> = <3 add/> 
or  clear the top RS of the stack before filling it , <mode> = <5 clear/> 

         * @param tbl the name of the table
         * @param mode see above
         * @param colname the name of the field
         * @param comparison comparison operator (see API reference)
         * @param operand1 value1 to compare
         * @param operand2 value2 to compare
         */
        public void TABLE_Select(String tbl, String mode, String colname, String comparison, String operand1, String operand2)
        {

            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".Select");
            push(mode);
            push(colname);
            push(comparison);
            push(operand1);
            push(operand2);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * Reads the content of one line of a database table.
for a variable length, only the 254 first byte are read. In this case, you must use the ReadBlock method in order to acces to all data of a variable length field

         * @param tbl the name of the table
         * @param rowid the rowid to read
         * @param colnames the fields whose values are to be read
         */
         public void TABLE_ReadLine(String tbl, String rowid, String[] colnames)
        {
            if ( colnames == null || colnames.length == 0 )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".ReadLine");
            push(rowid);
            for ( String col : colnames)
            {
                push(col);
            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
         
         /**
          * Reads the first valid line, which RowId becomes the current Id stored in the Results.FetchID property.
since lines could have been previously deleted, valid rowid's are not guaranted being adjacent. 
using ReadFirstLine, ReadNextLine  methods can be used to build an iterator through all valid lines in a table.
The rowid of the last accessed valid line can be retrieved by using session.GetLastDBInfo

         * @param tbl the name of the table
         * @param colnames the fields whose values are to be read
          */
        public void TABLE_ReadFirstLine(String tbl,String[] colnames)
        {
            if ( colnames == null || colnames.length == 0 )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".ReadFirstLine");
            for ( String col : colnames)
            {
                push(col);
            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * Reads the next valid rowid after a ReadFirstLine command, or the first valid rowid after session.DBCursor  property.
setting the session.DBCursor  is another way of iterating from another origin than the first valid line.
         * @param tbl the name of the table
         * @param colnames the fields whose values are to be read
         */
        public void TABLE_ReadNextLine(String tbl, String[] colnames)
        {
            if ( colnames == null || colnames.length == 0 )
                return;
            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".ReadNextLine");

            for ( String col : colnames)
            {
                push(col);
            }
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        /**
         * provides a Streaming access to a variable size column of a line of a given table.
global and direct access to more than 254 byte data (through ReadLine) is not allowed for a variable size field.

         * @param tbl the name of the table
         * @param rowid the rowid whose content is to stream
         * @param colname the name of the field
         * @param start  the position where to start from
         * @param count the size to read in bytes.
         */
        public void TABLE_ReadBlock(String tbl, String rowid, String colname,String start, String count)
        {

            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".ReadBlock");
            push(rowid);
            push(colname);
            push(start);
            push(count);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * Creates a BTree index on the column of a table.
once created, such an index gives direct and fast access to the lines of a database table.
the column can be used with the TABLE:xxx.Select method  in order to creat a ResultSet 
Moreover, if the BIndex is created with the "unique" tag,  a duplicated document will not be inserted in the table, and an error is issued.
Only one Bindex can be cretad for a column.
the name of the btree index will be :
btree_tablename_colname
Once created, a btree index will automatically begin the rebuild process

         * @param tbl the name of the table
         * @param colname the name of the field on which to create a B-Tree
         * @param boolUnique (default false) if true each entry in the B-Tree is unique
         */
           public void TABLE_BIndexCreate(String tbl,  String colname, String boolUnique)
        {

            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".BIndexCreate");
            push(colname);
            push(boolUnique);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
           
           /**
            * Deletes a BIndex
            * @param tbl the name of the table
            * @param colname  the name of the associated field
            */
        public void TABLE_BIndexDelete(String tbl,  String colname)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".BIndexDelete");
            push(colname);

             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * administration operation.
Rebuilds a given index.
Since the process can be long,  issuing the same command, with the interrupt flag set to true, will abort the process.
If aborted, the index is no more valid, and must be deleted and then recreated
Table:tablename.GetBindexes command via another session will display the state and the completion of this task. 

         * @param tbl the name of the table
         * @param colname the name of the associated field
         * @param boolinterrupt (default false) if true the build process may be interrupted at any time
         */
        public void TABLE_BIndexRebuild(String tbl,  String colname, String boolinterrupt)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".BIndexRebuild");
            push(colname);
            push(boolinterrupt);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        

        /**
         * A KIndex is a special kind of index that can be used ONLY on a text column of the linked master table.
each time, a table.insert, or table.update occurs on a Kindexed column,  the new content is automatically indexed.
this is a very simple way to design a back office application using the internal mARC server indexation engine.
Once created, the KTree will NOT begin to rebuild
the name of the ktree index will be :
                    ktree_tablename_colname
         * @param tbl the name of the table
         * @param colname the name of the associated field
         */
        public void TABLE_KIndexCreate(String tbl, String colname)
        {

            
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".KIndexCreate");
            push(colname);

             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
           
           /**
            * Deletes a KIndex
            * @param tbl the name of the table
            * @param colname  the name of the associated field
            */
        public void TABLE_KIndexDelete(String tbl,  String colname)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".KIndexDelete");
            push(colname);

             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * administration operation.
Rebuilds a given KIndex.
Since the process can be long,  issuing the same command, with the interrupt flag set to true, will abort the process.
If aborted, the index is no more valid, and must be deleted and then recreated
Table:tablename.GetKindexes command via another session will display the state and the completion of this task. 

         * @param tbl the name of the table
         * @param colname the name of the associated field
         * @param boolinterrupt (default false) if true the build process may be interrupted at any time
         */
        public void TABLE_KIndexRebuild(String tbl,  String colname, String boolinterrupt)
        {
            if (directExecute)
            {
                openScript(null);
            }
            push("Table:"+tbl+".KIndexRebuild");
            push(colname);
            push(boolinterrupt);
             
            addFunction();
            if (directExecute)
            {
                doIt();
            }
        }
        
        /**
         * Gets one or several Context properties.
To access one property,  use  a directive as  :  propertyname
To access several properties values in one command, separate each directive with the character  semi column ( ; )
if there are no parameter, all properties of the topmost context of the context's stack will be accessed

         * @param accessor porperties separated by semi-colon
         * @param index (base 1) the index of the context in the contexts stack
         */
     public void CONTEXTS_GetProperties(String accessor, String index)
    {

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.GetProperties");
       if ( accessor != null && !accessor.isEmpty() )
       {
           push(accessor);
       }
       push(index);
       addFunction();
       if ( directExecute )     doIt();

    }

/**
 * Access to context's  properties
Trying to change the value of a Read Only property will not generate an error.
To see which properties are avalaible, see CONTEXTS.GetProperties
To change one property,  use  a directive as  :  propertyname = propertyvalue
To change several properties values in one command, separate each directive with the character  semi column ( ; )
properties of the topmost context of the context's stack will be changed


 * @param index (default 1) index of context parameter on the stack. optional, between  1 to stack_count

 * @param accessors several properties values separated by semi column ( ; )
 */
    public void CONTEXTS_SetProperties(String index, String[] accessors)
    {
        if ( accessors == null || accessors.length == 0)
        {
            return;
        }
        if ( index == null )
            index = "1";
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.SetProperties");
       push(index);
       for(String s: accessors)
       {
           push(s);
       }
       addFunction();
       if ( directExecute )     doIt();

    }
    /**
     * Creates a new (empty) context  object on top of the Contexts stack.
     */
    public void CONTEXTS_New()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.New");
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Drops the topmost context on the stack. ressource will be freed

     * @param count (default 1) the number of contexts to drop
     *              if count = -1, the whole stack will be dropped.
     */
    public void CONTEXTS_Drop(String count)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Drop");
       push(count);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Duplicates the topmost context, or context block of size range,  on top of the contexts stack
     * @param range (default 1) the number of consecutive contexts to duplicate on the stack
     */
      public void CONTEXTS_Dup(String range)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Dup");
       push(range);
       addFunction();
       if ( directExecute )     doIt();
    }
      
      /**
       * Swaps the topmost context, or context block of size range,  on top of the contexts stack
       * @param range (default = 1) the number of contexts to swap on the top of the contexts stack
       */
    public void CONTEXTS_Swap(String range)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Swap");
       push(range);
       addFunction();
       if ( directExecute )     doIt();
    }
            /**
             * Selects a context of the context's stack and put it topmost on the stack.
the selection parameter is a vartype parameter, eg, it can be an int32, or a string depending of it's shape.
it is first evaluated as an integer numeric value.
if true, OnTop will put the context at index selection, on top of the stack. the range of the context will become 1.
if it is a string, OnTop will select the first context whose name property is selection, and put it on top of the stack.

             * @param selection numeric index of context on the stack or context identifier
             */
    public void CONTEXTS_OnTop(String selection)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.OnTop");
       push(selection);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Intersection of 2, or range+1 contexts on the context's stack
Activities will be consolidated according to the value of parameter consolidation

     * @param range number of topmost contexts to intersect
     * @param consolidation (default "simple") algorithm of consolidation
     *                       consolidation in ["simple", "min","max", "mean","maxinc"]
     */
    public void CONTEXTS_Intersection(String range, String consolidation)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Intersection");
       push(range);
       push(consolidation);
       addFunction();
       if ( directExecute )     doIt();
    }
    public void CONTEXTS_Union(String range, String consolidation)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Union");
       push(range);
       push(consolidation);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Performs an affine transform on activities in the topmost context of the context's stack
for each activity of a particule in the context
activity = a*activity + b

     * @param a the slope
     * @param b the constant
     */
        public void CONTEXTS_Amplify(String a, String b)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Amplify");
       push(a);
       push(b);
       addFunction();
       if ( directExecute )     doIt();
    }
        
    public void CONTEXTS_Split()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Split");
       addFunction();
       if ( directExecute )     doIt();
    }
       
    /**
     * Fetches the content of a context.
it is different from context_string, since it gives the state of the particles present in the contex, and not a textual signal.
an iterator can use a sequence of 
Fetch (n,1);		//gets the first n particles, starting at 1
Fetch();		// gets the next n particles, starting at n+1
Fetch();		// gets the next n particles, starting at 2*n+1

properties contexts.fetch_size and contexts.fetch_start  maintain the next Fetch parameters
if propertie contexts.fetch_start  = 0, then the end of the context has been reached

     * @param size (default 48) the number of elements to fetch
     * @param start (default 1) the starting index from where to fetch
     * @param index  (default 1) the index of the context on the stack to fetch
     */
    public void CONTEXTS_Fetch(String size, String start, String index)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Fetch");
       push(size);
       push(start);
       push(index);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Sorts the topmost context on the context's stack
        criterion can be 	["generality", "activity"]
        order can be		["descending", "ascending"]

     * @param criterion
     * @param order 
     */
        public void CONTEXTS_SortBy(String criterion, String order)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.SortBy");
       push(criterion);
       push(order);
       addFunction();
       if ( directExecute )     doIt();
    }
        
               
        /**
         * Learns and detects contexts from the topmost context of the context's stack.
This is an advanced causal (contextual) method.
It is used in order to develop over-learning strategies.
It must be used on genuine contexts, eg, contexts that have been deducted by a preliminary contextual analysis

         */
    public void CONTEXTS_Learn()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Learn");

       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Normalizes the activity of atoms in the topmost context of the context's stack.
The highest abs (activity) will become the reference for 100%
The topmost context remains sorted by activity, descending order.
if behaviour is "absolute", the reference of the 100% will be the highest activity OR 100 if the highest absolute value of activity is less than 100.
if behaviour is "relative", the reference of the 100% will allways be the highest absolute value of activity.

     * @param behaviour (default "absolute") the algorithm to compute activities
     */
        public void CONTEXTS_Normalize(String behaviour)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("CONTEXTS.Normalize");
       push(behaviour);
       addFunction();
       if ( directExecute )     doIt();
    }
        
        /**
         * Gets one or several Result Set (RS) properties.
To access one property,  use  a directive as  :  propertyname
To access several properties values in one command, separate each directive with the character  semi column ( ; )
if there are no parameter, all properties of the topmost context of the context's stack will be accessed

         * @param accessor properties to access
         * @param index (default) index = 1	(base 1) the index of the RS on the stack
         */
    public void RESULTS_GetProperties(String accessor, String index)
    {

       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.GetProperties");
       if ( accessor != null && !accessor.isEmpty())
       {
           push(accessor);
       }
       push(index);
       addFunction();
       if ( directExecute )     doIt();

    }

/**
 * Access to result's  properties
Trying to change the value of a Read Only property will not generate an error.
To see which properties are avalaible, see CONTEXTS.GetProperties
To change one property,  use  a directive as  :  propertyname = propertyvalue
To change several properties values in one command, separate each directive with the character  semi column ( ; )
properties of the topmost ResultSet of the context's stack will be changed
an accessor is a string like :
"propertyname = value"
and can be extended like
"propertyname1 = value1, … propertynameN = valueN"
Depending of your client application using only one extended accessor, as a parameter, is equivalent as using several accessors as parameters

 * @param index index of the RS on the stack
 * @param accessors properties to access
 */
    public void RESULTS_SetProperties(String index, String[] accessors)
    {
        if ( accessors == null || accessors.length == 0)
        {
            return;
        }
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.SetProperties");
       push(index);
       for(String s: accessors)
       {
           push(s);
       }
       addFunction();
       if ( directExecute )     doIt();

    }
    
    /**
     * Creates a new ResultSet (RS) on top of the Result's stack.
a RS must be linked to a table.
by default,  the RS is linked to the master table, if it exists, or to NULL.
in such a situation,  this link can be explicitely set using the RESULTS.SetProperties method over the property owner_table.

     */
     public void RESULTS_New()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.New");
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
Drops the topmost RS on the stack. ressource will be freed

     * @param count (default 1) the number of contexts to drop
     *              if count = -1, the whole stack will be dropped.
     */
    public void RESULTS_Drop(String count)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Drop");
       push(count);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Duplicates the topmost Result Set  on top of the RS stack
     */
      public void RESULTS_Dup()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Dup");

       addFunction();
       if ( directExecute )     doIt();
    }
      
      /**
       * Swaps the topmost Result Set  on top of the RS stack
       * @param range (default = 1) the number of contexts to swap on the top of the contexts stack
       */
    public void RESULTS_Swap()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Swap");
       addFunction();
       if ( directExecute )     doIt();
    }
            /**
             * Selects a RS (Result Set) of the RSt's stack and put it topmost on the stack.
the selection parameter is a vartype parameter, eg, it can be an int32, or a string depending of it's shape.
it is first evaluated as an integer numeric value.
if true, OnTop will put the RS at range selection, on top of the stack. the range of the RS will become 1.
if it is a string, OnTop will select the first RS whose name property is selection, and put it on top of the stack.

             * @param selection the index or name of the RS
             */
    public void RESULTS_OnTop(String selection)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.OnTop");
       push(selection);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Intersection of 2, topmost Result Set (RS) on the RS's stack
Activities will be consolidated by summing the activities
     */
    public void RESULTS_Intersection()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Intersection");
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Union of 2, topmost Result Set (RS) on the RS's stack
Activities will be consolidated by summing the activities
     */
    public void RESULTS_Union()
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Union");
       addFunction();
       if ( directExecute )     doIt();
    }
    

    /**
     * Selects the rowids of the topmost Result Set (RS) on top of the RS's stacks, according to a given rule
     * @param column the field
     * @param operator the comparison operator (see API reference page 130) 
     * @param operand1 the first operand of comparison
     * @param operand2 the second operand of comparison
     */
    public void RESULTS_SelectBy( String column, String operator, String operand1, String operand2)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.SelectBy");
       push(column);
       push(operator);
       push(operand1);
       push(operand2);
       addFunction();
       if ( directExecute )     doIt();
    }
        
    /**
     * Deletes the rowids of the topmost Result Set (RS) on top of the RS's stacks, according to a given rule
     * @param column the field
     * @param operator the comparison operator (see API reference page 130) 
     * @param operand1 the first operand of comparison
     * @param operand2 the second operand of comparison
     */
    public void RESULTS_DeleteBy( String column, String operator, String operand1, String operand2)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.DeleteBy");
              push(column);
       push(operator);
       push(operand1);
       push(operand2);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Sorts the topmost Result Set (RS) of the RS's stack, according to the content of the specified  column of the linked table .
the linked table of a RS is accessible through the property results.owner_table


     * @param column the field to sort
     * @param order (default "descending") order can be ["descending", "ascending"]
     */
    
    public void RESULTS_SortBy( String column,  String order)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.SortBy");
       push(column);
       push(order);

       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Makes the topmost Result Set (RS) of the RS's stack unique, according to the content of the specified column of the linked table .
     * @param column the field
     */
    public void RESULTS_UniqueBy( String column)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.UniqueBy");
       push(column);
       addFunction();
       if ( directExecute )     doIt();
    }
    
    /**
     * Transforms the topmost Result Set (RS) of the RS's stack, linked to an initial table, into anothe RS linked to another table, acording to the content of a specified column.
the column parameter must be a colum containing rowid's of  the destination table, specified by the parameter table of the method.
Since the column is containing a rowid, it must be of type int32 at least, or int64, for future extensions of the database capacity.
SelectToTable is useful to solve relations between several tables.

     * @param column the field
     * @param table the table
     * @param boolunique (default true) remove doublings
     */
    public void RESULTS_SelectToTable( String column, String table, String boolunique)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.UniqueBy");
       push(column);
       push(table);
       push(boolunique);
       addFunction();
       if ( directExecute )     doIt();
    }
    /**
     * Fetches the content of a context.
Fetches the content of the topmost Result Set (RS) of the RS's stack.
The results are fetched according to the format defined in the property results.format
an iterator can use a sequence of 
Fetch (n,1);		//gets the first n particles, starting at 1
Fetch();		// gets the next n particles, starting at n+1
Fetch();		// gets the next n particles, starting at 2*n+1

properties results.fetch_size and results.fetch_start  maintain the next Fetch parameters
if propertie results.fetch_start  = 0, then the end of the RS has been reached

     * @param size (default 10) the number of elements to fetch
     * @param start (default 1) the starting index from where to fetch
     * @param index  (default 1) the index of the context on the stack to fetch
     */
    public void RESULTS_Fetch(String size, String start, String index)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Fetch");
       if ( size != null && !size.isEmpty())
       {
           push(size);
       }
       if ( start != null && !start.isEmpty())
       {
           push(start);
       }
       push(index);
       addFunction();
       if ( directExecute )     doIt();
    }
    

/**
 * Normalizes the activity of Id's in the topmost Result Set of the RS's stack.
The highest abs (activity) will become the reference for 100%
The topmost context remains sorted by activity, descending order.
if behaviour is "absolute", the reference of the 100% will be the highest activity OR 100 if the highest absolute value of activity is less than 100.
if behaviour is "relative", the reference of the 100% will allways be the highest absolute value of activity.

 * @param behaviour (default "absolute") "absolute", "relative"
 */
     public void RESULTS_Normalize(String behaviour)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Normalize");
       push(behaviour);
       addFunction();
       if ( directExecute )     doIt();
    }
           /**
Performs an transform ont activities in the topmost RS of the RS's stack
for each activity of a particule in the Result Set (RS)
activity = a*activity + b


     * @param a the slope
     * @param b the constant
     */
        public void RESULTS_Amplify(String a, String b)
    {
       if ( directExecute )     openScript(null);
       
       localParams.clear();
       push("RESULTS.Amplify");
       push(a);
       push(b);
       addFunction();
       if ( directExecute )     doIt();
    }

    
}

