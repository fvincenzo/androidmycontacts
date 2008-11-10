package android.server;

/*
 * Classe dedicata alla gestione del Database.
 * Gestisce l'apertura e la chiusura della connessione col Database
 * Fornisce i metodi per l'esecuzione delle query sul Database
 */
import java.sql.*;
import java.util.Vector;

public class Database {
   private String nomeDB;       // Nome del Database a cui connettersi
   private String nomeUtente;   // Nome utente utilizzato per la connessione al Database
   private String pwdUtente;    // Password usata per la connessione al Database
   private String errore;       // Raccoglie informazioni riguardo l'ultima eccezione sollevata
   private Connection db;       // La connessione col Database
   private boolean connesso;    // Flag che indica se la connessione è attiva o meno
   private String host;
   
   public Database(String host, String nomeDB) { this(host, nomeDB, "", ""); }

   public Database(String host, String nomeDB, String nomeUtente, String pwdUtente) {
      this.nomeDB = nomeDB;
      this.nomeUtente = nomeUtente;
      this.pwdUtente = pwdUtente;
      connesso = false;
      errore = "";
      this.host = host;
   }

   // Apre la connessione con il Database
   public boolean connetti() {
      connesso = false;
      try {

         // Carico il driver JDBC per la connessione con il database MySQL
         Class.forName("com.mysql.jdbc.Driver");

         // Controllo che il nome del Database non sia nulla
         if (!nomeDB.equals("")) {

            // Controllo se il nome utente va usato o meno per la connessione
            if (nomeUtente.equals("")) {

               // La connessione non richiede nome utente e password
               db = DriverManager.getConnection("jdbc:mysql://"+ host +"/" + nomeDB);
            } else {

               // La connessione richiede nome utente, controllo se necessita anche della password
               if (pwdUtente.equals("")) {

                  // La connessione non necessita di password
                  db = DriverManager.getConnection("jdbc:mysql://"+ host +"/" + nomeDB + "?user=" + nomeUtente);
               } else {

                  // La connessione necessita della password
                  db = DriverManager.getConnection("jdbc:mysql://"+ host +"/" + nomeDB + "?user=" + nomeUtente + "&password=" + pwdUtente);
               }
            }

            // La connessione è avvenuta con successo
            connesso = true;
         } else {
            System.out.println("Manca il nome del database!!");
            System.out.println("Scrivere il nome del database da utilizzare all'interno del file \"config.xml\"");
            System.exit(0);
         }
      } catch (Exception e) { errore = e.getMessage(); }
      return connesso;
   }

   // Esegue una query di selezione dati sul Database
   // query: una stringa che rappresenta un'istruzione SQL di tipo SELECT da eseguire
   // colonne: il numero di colonne di cui sarà composta la tupla del risultato
   // ritorna un Vector contenente tutte le tuple del risultato
   public Vector<String[]> eseguiQuery(String query) {
      Vector<String[]> v = null;
      String [] record;
      int colonne = 0;
      try {
         Statement stmt = db.createStatement();     // Creo lo Statement per l'esecuzione della query
         ResultSet rs = stmt.executeQuery(query);   // Ottengo il ResultSet dell'esecuzione della query
         v = new Vector();
         ResultSetMetaData rsmd = rs.getMetaData();
         colonne = rsmd.getColumnCount();

         while(rs.next()) {   // Creo il vettore risultato scorrendo tutto il ResultSet
            record = new String[colonne];
            for (int i=0; i<colonne; i++) record[i] = rs.getString(i+1);
            v.add( (String[]) record.clone() );
         }
         rs.close();     // Chiudo il ResultSet
         stmt.close();   // Chiudo lo Statement
      } catch (Exception e) { e.printStackTrace(); errore = e.getMessage(); }

      return v;
   }

   // Esegue una query di aggiornamento sul Database
   // query: una stringa che rappresenta un'istuzione SQL di tipo UPDATE da eseguire
   // ritorna TRUE se l'esecuzione è adata a buon fine, FALSE se c'è stata un'eccezione
   public boolean eseguiAggiornamento(String query) {
      int numero = 0;
      boolean risultato = false;
      try {
         Statement stmt = db.createStatement();
         numero = stmt.executeUpdate(query);
         risultato = true;
         stmt.close();
      } catch (Exception e) {
         e.printStackTrace();
         errore = e.getMessage();
         risultato = false;
      }
      return risultato;
   }

   // Chiude la connessione con il Database
   public void disconnetti() {
      try {
         db.close();
         connesso = false;
      } catch (Exception e) { e.printStackTrace(); }
   }

   public boolean isConnesso() { return connesso; }   // Ritorna TRUE se la connessione con il Database è attiva
   public String getErrore() { return errore; }       // Ritorna il messaggio d'errore dell'ultima eccezione sollevata
}