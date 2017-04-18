
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public final static int SOCKET_PORT = 13267;  // you may change this
  public final static String FILE_TO_SEND = "C:\\Users\\User\\workspace\\CSE\\bin\\Assignment2\\smallFile.txt";  // you may change this

  public static void main (String [] args ) throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        //System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);

          //authenticate message sent by client eg. "is this secstore?"
          PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
          String clientmsg = in.readLine(); 
          System.out.println("encoding clientmsg: "+clientmsg+"..");
          signmsg(msg);

          // send file
          File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }
  //server must sign message from client w private key & send back 
  //along with signed certificate from which client can extract
  //public key 
  public static void signmsg(String msg) {

    InputStream fis = new FileInputStream("CA.crt");
    InputStream CAkey = new FileInputStream("1001849.crt");
    CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
    X509Certificate CAcert =(X509Certificate)cf.generateCertificate(fis);
    X509Certificate shajcert =(X509Certificate)cf.generateCertificate(CAkey);
    PublicKey cakey = CAcert.getPublicKey();
    PublicKey shajkey = shajcert.getPublicKey();
    //check validity
    shajcert.checkValidity();
    shajcert.verify(cakey);
  }
}
