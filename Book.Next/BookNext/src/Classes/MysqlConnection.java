/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.sql.*;
import com.mysql.jdbc.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Classes.CUser;

/**
 *
 * @author Ingesis
 */
public class MysqlConnection {

    private Connection connection;
    private Statement statement;

    // <editor-fold desc="Connections & Statements">
    public Connection connect() throws SQLException {

        if (connection == null) {

            new Driver();
            // buat koneksi
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/booknext",
                    "booknext",
                    "book");

        }
        return connection;

    }

    public void close() throws SQLException {

        if (connection != null) {

            connection.close();

        }

    }

    public Statement makeStatement() throws SQLException {

        if (connection != null) {
            statement = connection.createStatement();
        }
        return statement;

    }
    // </editor-fold>

    // <editor-fold desc="User Querys">
    public boolean addNewUser(String user, String fullname, String birthday, String password, String imagen, String country) throws SQLException {

        boolean add = false;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("insert into users(username,fullname,birthday,passwoord,imagen,country) values (?,?,?,?,?,?)");
                query.setString(1, user);
                query.setString(2, fullname);
                query.setString(3, birthday);
                query.setString(4, password);
                query.setString(5, imagen);
                query.setString(6, country);
                if (query.executeUpdate() > 0) {
                    add = true;
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            close();
        }

        return add;
    }

    public boolean addWordToBayes(int genre, String word, String table) {
        boolean add = false;
        try {
            if (makeStatement() != null) {
                String squery = "SELECT * FROM " + table + " WHERE word = '" + word + "'";
                PreparedStatement q = connection.prepareStatement(squery);
                ResultSet result = q.executeQuery(squery);
                if (!result.next()) //The word is new
                {
                    PreparedStatement query = null;
                    query = connection.prepareStatement("INSERT INTO " + table + " VALUES (?,?,?,?,?,?)");
                    int[] gs = {0, 0, 0, 0, 0};
                    gs[genre] = 1;
                    query.setString(1, word);
                    query.setInt(2, gs[0]);
                    query.setInt(3, gs[1]);
                    query.setInt(4, gs[2]);
                    query.setInt(5, gs[3]);
                    query.setInt(6, gs[4]);
                    if (query.executeUpdate() > 0) {
                        add = true;
                    }
                } else {
                    PreparedStatement query = null;
                    query = connection.prepareStatement("UPDATE " + table + " SET genre1 = genre1 + ?,"
                            + "genre2 = genre2 + ?, genre3 = genre3 + ?, genre4 = genre4 + ?, genre5 = genre5 + ? WHERE word = '" + word + "'");
                    int[] gs = {0, 0, 0, 0, 0};
                    gs[genre] = 1;
                    for (int i = 0; i < gs.length; i++) {
                        query.setInt(i + 1, gs[i]);
                    }
                    if (query.executeUpdate() > 0) {
                        add = true;
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return add;
    }

    public int[] getGenresForWord(String word, String table) {
        String squery = "SELECT * FROM " + table + " WHERE word = '" + word + "'";
        PreparedStatement q;
        int[] genres = {0, 0, 0, 0, 0};
        try {
            q = connection.prepareStatement(squery);
            ResultSet result = q.executeQuery(squery);
            if (result.next()) {
                for (int i = 0; i < genres.length; i++) {
                    genres[i] = result.getInt(i + 2);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return genres;
    }
    
    public int getVocabularyTotal(String table) {
        int total = -1;
        String squery = "SELECT COUNT(*) FROM " + table;
        PreparedStatement q;
        try {
            q = connection.prepareStatement(squery);
            ResultSet result = q.executeQuery(squery);
            if (result.next()) {
                total = result.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public int[] getTotalsForGenre(String table) {
        String squery = "SELECT SUM(genre1) as g1, SUM(genre2) as g2, SUM(genre3) as g3, SUM(genre4) as g4, SUM(genre5) as g5 FROM " + table;
        PreparedStatement q;
        int[] genres = {0, 0, 0, 0, 0};
        try {
            q = connection.prepareStatement(squery);
            ResultSet result = q.executeQuery(squery);
            if (result.next()) {
                for (int i = 0; i < genres.length; i++) {
                    genres[i] = result.getInt(i + 1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return genres;
    }

    private CUser getUser(String user) {
        CUser cuser = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select * from users where username = ?");
                q.setString(1, user);

                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    int id = result.getInt("id");
                    cuser = new CUser(
                            result.getString("username"),
                            result.getString("fullname"),
                            result.getString("passwoord"),
                            result.getString("birthday"),
                            result.getString("imagen"),
                            result.getString("country")
                    );
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cuser;
    }

    public CUser consultUser(String user) {
        boolean exist = false;
        CUser cuser = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select * from users where username = ?");
                q.setString(1, user);

                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    exist = true;
                    cuser = getUser(user);
                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cuser;
    }

    public int getUserId(String user) {
        int id = 0;
        CUser cuser = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select * from users where username = ?");
                q.setString(1, user);

                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    id = result.getInt("id");
                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    public boolean updateUser(String user, String fullname, String birthday, String password, String country) throws SQLException {

        boolean add = false;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("update users set username = ?, fullname = ?, birthday = ? ,passwoord = ?, country = ?  where username = ?");
                query.setString(1, user);
                query.setString(2, fullname);
                query.setString(3, birthday);
                query.setString(4, password);
                query.setString(5, country);
                query.setString(6, user);

                if (query.executeUpdate() > 0) {
                    add = true;
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            close();
        }

        return add;
    }

    // </editor-fold>
    
    //<editor-fold desc="Books Querys">
    public CBook getBookFromISBN(String isbn)
    {;

        CBook cbook = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("Select * from book WHERE isbn = ?");
                query.setString(1, isbn);
                ResultSet result = null;
                result = query.executeQuery();
                if (result.next())
                {
                    cbook = new CBook();

                    cbook.fillCBook(
                            result.getString("isbn"),
                            result.getString("book_name"),
                            result.getString("author"),
                            result.getString("imagen"),
                            result.getString("publish_date"),
                            result.getString("publisher"),
                            result.getString("rating_average"),
                            result.getString("description"),
                            result.getString("genre")
                    );
                }
                    

                
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cbook;
    }
    
    public List<CBook> getBooks() {

        List<CBook> books = null;

        CBook cbook = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("Select * from book");

                ResultSet result = null;
                result = query.executeQuery();
                books = new ArrayList<CBook>();
                while (result.next()) {
                    cbook = new CBook();
                    
                    cbook.fillCBook(
                            result.getString("isbn"),
                            result.getString("book_name"),
                            result.getString("author"),
                            result.getString("imagen"),
                            result.getString("publish_date"),
                            result.getString("publisher"),
                            result.getString("rating_average"),
                            result.getString("description"),
                            result.getString("genre")
                    );
                    books.add(cbook);

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return books;
    } 
    
    public List<CBook> getBooksByGenre(String genre) {

        List<CBook> books = null;

        CBook cbook = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                String ugenre = "%"+genre+"%";
                query =connection.prepareStatement("Select * from book where LOWER(genre) LIKE ?");
                                
                
                query.setString(1, ugenre);
                ResultSet result =null;
                result = query.executeQuery();
                books = new ArrayList<CBook>();
                while(result.next()){
                    cbook = new CBook();
                    cbook.fillCBook(
                    result.getString("isbn"),
                    result.getString("book_name"),
                    result.getString("author"),                            
                    result.getString("imagen"),
                    result.getString("publish_date"),
                    result.getString("publisher"),
                    result.getString("rating_average"),
                    result.getString("description"),
                    result.getString("genre")                           
                    );
                    books.add(cbook);
                    
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return books;
    }    

    public List<CBook> getUserSavedBooks(int id) {

        List<CBook> books = null;

        CBook cbook = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("Select book.* from book JOIN user_book on book.isbn = user_book.isbn AND user_book.id = ? AND user_book.user_saved = 1");
                query.setInt(1, id);
                ResultSet result = null;
                result = query.executeQuery();

                books = new ArrayList<CBook>();
                while (result.next()) {
                    cbook = new CBook();
                    cbook.fillCBook(
                            result.getString("isbn"),
                            result.getString("book_name"),
                            result.getString("author"),
                            result.getString("imagen"),
                            result.getString("publish_date"),
                            result.getString("publisher"),
                            result.getString("rating_average"),
                            result.getString("description"),
                            result.getString("genre")
                    );
                    books.add(cbook);

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return books;
    }
    
    public List<CBook> getUserFavBooks(int id) {

        List<CBook> books = null;

        CBook cbook = null;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("Select book.* from book JOIN user_book on book.isbn = user_book.isbn AND user_book.id = ? AND user_book.user_liked = 1");
                query.setInt(1, id);
                ResultSet result = null;
                result = query.executeQuery();

                books = new ArrayList<CBook>();
                while (result.next()) {
                    cbook = new CBook();
                    cbook.fillCBook(
                            result.getString("isbn"),
                            result.getString("book_name"),
                            result.getString("author"),
                            result.getString("imagen"),
                            result.getString("publish_date"),
                            result.getString("publisher"),
                            result.getString("rating_average"),
                            result.getString("description"),
                            result.getString("genre")
                    );
                    books.add(cbook);

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return books;
    }

    public void updateDescriptionBook(String isbn, String description) {
        try {
            if (makeStatement() != null) {
                String squery = "UPDATE book SET description = '" + description + "' WHERE isbn = '" + isbn + "'";
                PreparedStatement q = connection.prepareStatement(squery);
                /*q.setString(1, description);
                q.setString(2, isbn);*/
                int bla = q.executeUpdate(squery);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public boolean addBook(CBook cbook) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("insert ignore into book(isbn,book_name,author,imagen,publish_date,publisher,rating_average,description,genre) values (?,?,?,?,?,?,?,?,?)");

                query.setString(1, cbook.isbn);
                query.setString(2, cbook.getBook_name());
                query.setString(3, cbook.getBook_authorsStr());
                query.setString(4, cbook.getBook_image());
                query.setString(5, cbook.getBook_publishYear());
                query.setString(6, cbook.getBook_publisher());
                query.setString(7, cbook.getRating_String());
                query.setString(8, cbook.getBook_description());
                query.setString(9, cbook.getBook_genre());

                if (query.executeUpdate() > 0) {
                    add = true;
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return add;
    }

    public long countBooks() {

        long id = -1;

        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select count(*) from book");

                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    id = result.getLong("count(*)");
                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;

    }

    public void setBookRating(String isbn, String average)
    {
        //UPDATE book SET description = 'This is a shanghai book' WHERE isbn = '000654861X';
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("UPDATE book SET rating_average = ? WHERE isbn = ?");
                q.setString(1, average);
                q.setString(2, isbn);
                q.executeUpdate();
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getBookRating(String isbn)
    {
        double rating = 0;
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select rating_average from book where isbn = ?");
                q.setString(1, isbn);
                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    rating = Double.parseDouble(result.getString("rating_average"));
                } 
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rating;
    }
    
    /**
     * This function is used to know the global users rating, using an specific of stars
     * @param isbn book
     * @param range #stars
     * @return # users who rate this book wit @param stars
     */
    public int getGlobalRating(String isbn, String range) {
        int id = 0;
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select COUNT(*) from user_book where isbn = ? AND user_rating = ?");
                q.setString(1, isbn);
                q.setString(2, range);
                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    id = result.getInt("count(*)");
                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    //</editor-fold>
    
    //<editor-fold desc="User_Books Querys">
    public boolean doesThisRelationExists(int id, String ISBN)
    {
        boolean exists = false;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("SELECT * FROM user_book WHERE id = ? AND isbn = ?");

                query.setInt(1, id);
                query.setString(2, ISBN);
                ResultSet result = null;
                result = query.executeQuery();
                if (result.next()) {
                     exists = true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
    }
    
    public boolean setUserBook(int id, String ISBN) {
        boolean add = false;
        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;
                query = connection.prepareStatement("Insert into user_book(isbn,id,user_rating,user_liked,user_view,user_saved) values (?,?,0,0,1,0)");

                query.setString(1, ISBN);
                query.setInt(2, id);;
                if (query.executeUpdate() > 0) {
                    add = true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }

    public boolean updateRating(int id, String ISBN, int rating) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("UPDATE user_book SET user_rating = ? Where id = ? AND isbn = ?");

                query.setInt(1, rating);
                query.setInt(2, id);
                query.setString(3, ISBN);
                if (query.executeUpdate() > 0) {
                    add = true;

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }

    public boolean updateLiked(int id, String ISBN, int liked) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("UPDATE user_book SET user_liked = ? Where id = ? AND isbn = ?");

                query.setInt(1, liked);
                query.setInt(2, id);
                query.setString(3, ISBN);
                if (query.executeUpdate() > 0) {
                    add = true;

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }

    public boolean updateView(int id, String ISBN) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query;
                query=connection.prepareStatement("UPDATE user_book SET user_view = user_view+1 Where id = ? AND isbn = ?");
                
                query.setInt(1, id);
                query.setString(2, ISBN);
                if (query.executeUpdate() > 0) {
                    add = true;

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }

    public boolean updateSaved(int id, String ISBN, int saved) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("UPDATE user_book SET user_saved = ? Where id = ? AND isbn = ?");

                query.setInt(1, saved);
                query.setInt(2, id);
                query.setString(3, ISBN);
                if (query.executeUpdate() > 0) {
                    add = true;

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }
    
    public boolean isThisBookSaved(int id, String ISBN) {
        boolean add = false;

        try {
            if (makeStatement() != null) {

                PreparedStatement query = null;

                query = connection.prepareStatement("Select user_saved from user_book where id = ? AND isbn = ?");

                
                query.setInt(1, id);
                query.setString(2, ISBN);
                
                ResultSet result = null;
                result = query.executeQuery();
                result.next();
                if (result.getInt("user_saved") == 1)
                    add = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return add;
    }
    
    public boolean isThisBookFavorite(int id, String ISBN) {
      boolean add = false;

      try {
          if (makeStatement() != null) {

              PreparedStatement query = null;

              query = connection.prepareStatement("Select user_liked from user_book where id = ? AND isbn = ?");


              query.setInt(1, id);
              query.setString(2, ISBN);

              ResultSet result = null;
              result = query.executeQuery();
              result.next();
                if (result.getInt("user_liked") == 1)
                    add = true;
          }

      } catch (SQLException ex) {
          Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
      }
      return add;
  }
    
    public int getUserRating(String isbn, int id) {
        int rating = 0;
        String sId = String.valueOf(id);
        try {
            if (makeStatement() != null) {

                PreparedStatement q = null;
                q = connection.prepareStatement("Select user_rating from user_book where isbn = ? AND id = ?");
                q.setString(1, isbn);
                q.setString(2, sId);
                ResultSet result = null;
                result = q.executeQuery();

                if (result.next()) {
                    rating = result.getInt("user_rating");
                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rating;
    }
    
    //</editor-fold> 

      
    public double getCategoryPorcent(int uid, String genre) {
        double id = 0;
        try {
            if (makeStatement() != null) {

                PreparedStatement query_0 = null;
                PreparedStatement query_1 = null;
                PreparedStatement query_2 = null;
                PreparedStatement query_3 = null;
                PreparedStatement query_4 = null;

                //Return the total of books
                query_0 = connection.prepareStatement("SELECT COUNT(*)AS result FROM user_book WHERE id  = ?");
                query_0.setInt(1, uid);

                String ugenre = "%"+genre+"%";
                //Return how many book the user has liked
                query_1 = connection.prepareStatement("SELECT COUNT(*)AS result FROM user_book JOIN book ON user_book.isbn = book.isbn"
                        + "			 WHERE user_book.id = ? AND user_book.user_liked = 1 AND book.genre LIKE ?");
                query_1.setInt(1, uid);
                query_1.setString(2, ugenre);

                //Return how many book the user has saved
                query_2 = connection.prepareStatement("SELECT COUNT(*) AS result FROM user_book JOIN book ON user_book.isbn = book.isbn" +
"			 WHERE user_book.id = ? AND user_book.user_saved = 1 AND book.genre LIKE ?");
                query_2.setInt(1, uid);
                query_2.setString(2, ugenre);

                //Return how many books the user has rating with value up to 3
                query_3 = connection.prepareStatement("SELECT COUNT(*) AS result FROM user_book JOIN book ON user_book.isbn = book.isbn" +
"			 WHERE user_book.id = ? AND user_book.user_rating > 3 AND book.genre LIKE ?");
                query_3.setInt(1, uid);
                query_3.setString(2, ugenre);
                
                //Return how many book the user has viwed
                query_4 = connection.prepareStatement("SELECT COUNT(*)AS result FROM user_book WHERE id  = ? AND user_liked=0 AND user_saved=0");
                query_4.setInt(1, uid);
                
                ResultSet result_0 = null;
                ResultSet result_1 = null;
                ResultSet result_2 = null;
                ResultSet result_3 = null;
                ResultSet result_4 = null;
                result_0 = query_0.executeQuery();
                result_1 = query_1.executeQuery();
                result_2 = query_2.executeQuery();
                result_3 = query_3.executeQuery();
                result_4 = query_4.executeQuery();

                if (result_0.next() & result_1.next() & result_2.next()& result_3.next()& result_4.next()) {
                    int r1 = result_1.getInt("result");
                    int r2 =  result_2.getInt("result");
                    int r3 =  result_3.getInt("result");
                    int r4=  result_4.getInt("result");
                    int r5 = result_0.getInt("result");
                    id = (double)(r1+r2+r3+r4) /(double) r5;

                } else {
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    
}
